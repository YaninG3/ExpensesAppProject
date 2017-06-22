package com.jiminycricket.expensesmanager.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * the class provides methods for managing different aspects of budget managing
 * @author Jiminy Cricket
 *
 */
public class UserBudgetManager {
	List<MonthTotalExpense> lastTwelveMonths = null;
	IexpnsmngrDAO dao;
	Integer userid;
	
	public UserBudgetManager() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * the UserBudgetManager is relevant for specific user, hence it must have a user id reference 
	 * @param userid
	 * @throws DaoException 
	 */
	public UserBudgetManager(Integer userid) throws DaoException {
		super();
		dao = new SQLiteDAO();
		setUserid(userid);
		lastTwelveMonths = new ArrayList<MonthTotalExpense>();
	}
	
	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	/**
	 * will provide information of the last twelve months expenses with a json format string
	 * @return
	 * @throws DaoException 
	 */
	public String getLastTwelveMonthsJson() throws DaoException{
		setLastTwelveMonthsFromDB();
		StringBuilder sb = new StringBuilder();
		sb.append("{\"months\" : [\n");
		for(int i=0; i<this.lastTwelveMonths.size(); i++ ){
			sb.append(this.lastTwelveMonths.get(i).getJsonString());
			//if it's not the last item put an ","
			if(i < this.lastTwelveMonths.size()-1){
				sb.append(",\n");
			}
		}
		sb.append("\n]}");
		return sb.toString();
	}

	public List<MonthTotalExpense> getLastTwelveMonths() {
		return lastTwelveMonths;
	}

	public void setLastTwelveMonths(List<MonthTotalExpense> lastTwelveMonths) {
		this.lastTwelveMonths = lastTwelveMonths;
	}
	
	/**
	 * private function - set the lastTwelveMonths member with information form the database
	 * @throws DaoException 
	 */
	private void setLastTwelveMonthsFromDB() throws DaoException{
		initLastTwelveMonthsList();
		ExpenseItemList itemList = dao.getExpensesByUserId(getUserid());
		Calendar cal = Calendar.getInstance();
		
		CategoryList categoryList = dao.getCategoryListByUserId(userid);
		Integer budgetsTotal = 0;
		for(Integer i = 0; i < categoryList.size(); i++){
			budgetsTotal += categoryList.get(i).budgetLimit;
		}
		
		for(Integer i = 0; i < lastTwelveMonths.size(); i++){
			lastTwelveMonths.get(i).setTotalbudgetsLimit(budgetsTotal);
			Integer month = lastTwelveMonths.get(i).getMonth();
			for(Integer j = 0; j < itemList.size() ; j++){
				cal.setTime(itemList.get(j).getDate());
				Integer itemMonth = cal.get(Calendar.MONTH) + 1;
				if(month == itemMonth){
					lastTwelveMonths.get(i).addExpense(itemList.get(j).getCost());
				}
			}
		}
	}

	/**
	 * private method - initialize the lastTwelveMonths list with months numbers and years
	 */
	private void initLastTwelveMonthsList(){
		Integer monthNow = Calendar.getInstance().get(Calendar.MONTH) + 1;
		Integer yearNow = Calendar.getInstance().get(Calendar.YEAR);
		Integer month = monthNow + 1;
		Integer year = yearNow - 1;
		lastTwelveMonths.clear();
		
		if(month > 12){
			month = 1;
		}
	
		while(month > monthNow && month <=12){
			MonthTotalExpense mte = new MonthTotalExpense(0.00, 0, month, year);
			lastTwelveMonths.add(mte);
			month++;
		}
		
		month = 1;
		year = yearNow;
		
		while(month <= monthNow){
			MonthTotalExpense mte = new MonthTotalExpense(0.00, 0, month, year);
			lastTwelveMonths.add(mte);
			month++;
		}
	}
	
	/**
	 * will provide an answer if a category in question is in state of Exceeded
	 * @param category
	 * @return
	 * @throws DaoException 
	 */
	public Boolean doesCategoryBudgetIsExceeded(Category category) throws DaoException{
		Integer categoryBudgetLimit = dao.getCategoryListByUserId(userid).getCategoryByName(category.getName()).getBudgetLimit();
		Integer monthNow = Calendar.getInstance().get(Calendar.MONTH) + 1;
		ExpenseItemList itemListForThisMonth = getItemListPerMonth(monthNow);
		ExpenseItemList itemListForThisMonthAndCategory = getExpensesMatchedCategory(category, itemListForThisMonth);
		Double valueForThisMonthAndCaegory = 0.0;
		for(Integer i=0; i<itemListForThisMonthAndCategory.size(); i++)
			valueForThisMonthAndCaegory += itemListForThisMonthAndCategory.get(i).getCost();
		
		if (valueForThisMonthAndCaegory > categoryBudgetLimit){
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * private method - within the last years month select a month to get the expenses relevant for this month
	 * @param selectedMonthNumber
	 * @return
	 * @throws DaoException 
	 */
	private ExpenseItemList getItemListPerMonth(Integer selectedMonthNumber) throws DaoException{
		ExpenseItemList itemList = dao.getExpensesByUserId(getUserid());
		ExpenseItemList monthItemList = new ExpenseItemList();
		ExpenseItem item;
		Calendar cal = Calendar.getInstance();
		for (Integer i=0; i<itemList.size(); i++){
			item = itemList.get(i);
			cal.setTime(item.getDate());
			Integer itemMonth = cal.get(Calendar.MONTH) + 1;
			if(itemMonth == selectedMonthNumber){
				monthItemList.push(item);
			}
		}
		return monthItemList;
	}
	
	/**
	 * private method - within the received expenses list
	 * return only the expenses that relevant to specific received category
	 * @param category
	 * @param list
	 * @return
	 */
	private ExpenseItemList getExpensesMatchedCategory(Category category, ExpenseItemList list){
		String categoryName = category.getName();
		ExpenseItem item;
		ExpenseItemList newExpenseItemList = new ExpenseItemList();
		for(Integer i=0; i<list.size(); i++){
			item = list.get(i);
			if(item.getCategory().equals(categoryName)){
				newExpenseItemList.push(item);
			}
		}
		return newExpenseItemList;
	}
	
	/**
	 * for the received category - return expenses details for this month is json format string
	 * @param category
	 * @return
	 * @throws DaoException 
	 */
	public String budgetStatusJson(Category category) throws DaoException{
		String categoryName = category.getName();
		CategoryList categoryList = dao.getCategoryListByUserId(userid);
		String jsonCategoryDetails = "";
		for (int i=0; i<categoryList.size(); i++){
			if (categoryList.get(i).getName().equals(categoryName)){
				jsonCategoryDetails = categoryList.get(i).getJsonString();
				break;
			}
		}
		ExpenseItemList ItemListForThisMonthandCategory = getItemListForThisMonthandCategory(category);
		Double expensesSumForCategoryThisMonth = 0.0;
		for (int i=0; i<ItemListForThisMonthandCategory.size(); i++){
			expensesSumForCategoryThisMonth += ItemListForThisMonthandCategory.get(i).getCost();
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("{CategoryDetails:\n");
		sb.append(jsonCategoryDetails + ",");
		sb.append("ExpensesSum: " + expensesSumForCategoryThisMonth + "}");
		
		return sb.toString();
	}
	
	/**
	 * Private method - get items list for this month and relevant for received category
	 * @param category
	 * @return
	 * @throws DaoException 
	 */
	private ExpenseItemList getItemListForThisMonthandCategory(Category category) throws DaoException{
		Integer monthNow = Calendar.getInstance().get(Calendar.MONTH) + 1;
		ExpenseItemList itemListForThisMonth = getItemListPerMonth(monthNow);
		return getExpensesMatchedCategory(category,itemListForThisMonth);
	}
}
