package com.jiminycricket.expensesmanager.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jiminycricket.expensesmanager.model.Category;
import com.jiminycricket.expensesmanager.model.CategoryList;
import com.jiminycricket.expensesmanager.model.DaoException;
import com.jiminycricket.expensesmanager.model.ExpenseItem;
import com.jiminycricket.expensesmanager.model.ExpenseItemList;
import com.jiminycricket.expensesmanager.model.IexpnsmngrDAO;
import com.jiminycricket.expensesmanager.model.SQLiteDAO;
import com.jiminycricket.expensesmanager.model.User;
import com.jiminycricket.expensesmanager.model.UserBudgetManager;

/**
 * Servlet implementation class ServletControlrer
 */

@WebServlet({"/servletcontroller","/servletcontroller/*"})
public class ServletControlrer extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private IexpnsmngrDAO dao;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletControlrer() {
        super();
        try {
			dao = new SQLiteDAO();
		} catch (DaoException e) {
			
			e.printStackTrace();
		}
    }

    /**
     * processRequest serves as an index,
     * calls the relevant handling method base on the request url  
     * @param request
     * @param response
     */
	private void processRequest(HttpServletRequest request, HttpServletResponse response) {
		String url = request.getRequestURL().toString();
		System.out.println("Request URL: " + url);

		if(url.endsWith("registeraction")){
			registerAction(request,response);
		}
		else if(url.endsWith("loginaction")){
			loginAction(request,response);
		}
		else if(url.endsWith("getuserobject")){
			getUserObject(request,response);
		}
		else if(url.endsWith("addnewitemaction")){
			addNewItemAction(request,response);
		}
		else if(url.endsWith("getexpenseslist")){
			getExpensesList(request,response);
		}
		else if(url.endsWith("deleteitemaction")){
			deleteItemAction(request,response);
		}
		else if(url.endsWith("updateitemaction")){
			updateItemAction(request,response);
		}
		else if(url.endsWith("getcategorieslist")){
			getCategoriesList(request,response);
		}
		else if(url.endsWith("deletecategoryaction")){
			deleteCategoryAction(request,response);
		}
		else if(url.endsWith("addcategoryaction")){
			addCategoryAction(request,response);
		}
		else if(url.endsWith("getlasttwelvemonths")){
			getLastTwelveMonths(request,response);
		}
		else{
			registerAction(request,response);
		}
	}
	
	/**
	 * if a register action request recieved 
	 * add the user to the db
	 * repond with the user details in xml format string
	 * @param request
	 * @param response
	 */
	private void registerAction(HttpServletRequest request, HttpServletResponse response){
		System.out.println("registeration action request recieved by the Servlet Controller");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		User user = new User(username, password);
		try {
			dao.addNewUser(user);
			user = dao.getUserByUsername(username);
			StringBuffer sb = user.getUserXml();
			response.setContentType("text.xml");
			response.getWriter().write(sb.toString());
		} catch (DaoException e1) {
			
			e1.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	/**
	 * when a login request recieved 
	 * checks if the user name exists
	 * checks authentication
	 * and respond with the user id
	 * or '0' case authentication failed
	 * @param request
	 * @param response
	 */
	private void loginAction(HttpServletRequest request, HttpServletResponse response){
		System.out.println("login action request recieved");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		try{
			if(dao.usernameExists(username)){
				System.out.println("username exists");
				if(dao.doesPasswordFitsUsername(username, password)){
					String userid = dao.getUserByUsername(username).getId().toString();
					response.getWriter().write(userid);
				}
				else{
					response.getWriter().write("0"); //incorrect password
				}
			}
			else{
				System.out.println("username wasn't found");
				response.getWriter().write("-1"); //username wasn't found
			}
		}catch (IOException e){
			e.printStackTrace();
		} catch (DaoException e) {
			
			e.printStackTrace();
		}
	}
	
	/**
	 * case getuserobjecdt request recieved
	 * send an xml representation of the user object from the DB
	 * @param request
	 * @param response
	 */
	private void getUserObject(HttpServletRequest request, HttpServletResponse response){
		Integer id = Integer.valueOf(request.getParameter("userid"));
		try {
			String xmlUser = dao.getUserById(id).getUserXml().toString();
			response.setContentType("text.xml");
			response.getWriter().write(xmlUser);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DaoException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * add a new item to the DB
	 * checks wehether a certian budget exceeded
	 * and respond with '0' - not exceeded, '1' - exceeded
	 * @param request
	 * @param response
	 */
	private void addNewItemAction(HttpServletRequest request, HttpServletResponse response){
		System.out.println("addNewItemAction received");
		String name = request.getParameter("name");
		String comment = request.getParameter("comment");
		Double cost = Double.parseDouble( request.getParameter("cost") );
		String category = request.getParameter("category");
		String subcategory = request.getParameter("subcategory");
		Integer userid = Integer.parseInt( request.getParameter("userid") );
		
		ExpenseItem item = new ExpenseItem(category, subcategory, name, cost, userid, comment);

		try {
			dao.addNewExpenseItem(item);
			UserBudgetManager ubm = new UserBudgetManager(userid);
			Integer budgetExceeded = 0;
			if( ubm.doesCategoryBudgetIsExceeded( new Category(category) ) ){
				budgetExceeded = 1;
			}
			
			response.getWriter().write(budgetExceeded.toString());
			System.out.println("budgetExceeded: " + budgetExceeded);
			response.setStatus(200); //Status code (200) indicating the request succeeded normally.
		} catch (IOException e) {	
			e.printStackTrace();
		} catch (DaoException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * as a getExpensesList request is received the servlet response with an
	 * Expenses List JSON String relevant to specific user
	 */
	private void getExpensesList(HttpServletRequest request, HttpServletResponse response){
		System.out.println("getExpensesList received");
		Integer userid = Integer.valueOf( request.getParameter("userid") );		
		try {
			ExpenseItemList list = dao.getExpensesByUserId(userid);
			String listJsonString = list.getJsonString();
			response.setContentType("application/json");
			response.getWriter().write(listJsonString);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DaoException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * delete the item from the DB
	 * respond with a text message
	 * @param request
	 * @param response
	 */
	private void deleteItemAction(HttpServletRequest request, HttpServletResponse response){
		System.out.println("deleteItemAction received");
		Integer itemid = Integer.valueOf( request.getParameter("itemid"));
		try {
			dao.deleteExpenseItemById(itemid);
			response.getWriter().write("item was removed successfully");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DaoException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * update the DB with a specific expense item
	 * will return item's json representation String  
	 * @param request
	 * @param response
	 */
	private void updateItemAction(HttpServletRequest request, HttpServletResponse response){
		System.out.println("updateItemAction received");
		Integer itemid = Integer.valueOf( request.getParameter("itemid"));
		String name = request.getParameter("name");
		String comment = request.getParameter("comment");
		Double cost = Double.parseDouble( request.getParameter("cost") );
		String category = request.getParameter("category");
		String subcategory = request.getParameter("subcategory");
		Integer userid = Integer.valueOf( request.getParameter("userid"));

		
		ExpenseItem item = new ExpenseItem(itemid ,category, subcategory, name, cost, userid, null, comment );
		try {
			item = dao.updateExpenseItem(item);
			String itemJsonString = item.getJsonString();
			response.setContentType("application/json");
			response.getWriter().write(itemJsonString);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DaoException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * getthe categories list from the db that relevant to the given userid
	 * return the json representation of the list
	 * @param request
	 * @param response
	 */
	private void getCategoriesList(HttpServletRequest request, HttpServletResponse response){
		Integer userid = Integer.valueOf( request.getParameter("userid") );
		System.out.println("getCategoriesList request received with userid=" + userid);
		try {
			CategoryList list = dao.getCategoryListByUserId(userid);
			String listJsonString = list.getJsonString();
			response.setContentType("application/json");
			response.getWriter().write(listJsonString);
			System.out.println(listJsonString);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DaoException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * checks if the given category exists in the DB
	 * than delete it
	 * respond with a text message
	 * 
	 * @param request
	 * @param response
	 */
	private void deleteCategoryAction(HttpServletRequest request, HttpServletResponse response){
		System.out.println("deleteCategoryAction request received");
		Integer categoryid = Integer.valueOf( request.getParameter("categoryid"));
		String categoryName = request.getParameter("categoryname");
		Integer userid = Integer.valueOf( request.getParameter("userid"));
		try {
			if(dao.areItemsStillExistsWithCategory(categoryName, userid)){
				response.setStatus(409); // Conflict code - The request could not be completed due to a conflict with the current state of the resource
				response.getWriter().write("Category still in use\n Delete expenses items first");
			}
			else{
				dao.deleteCategory(categoryid);
				response.getWriter().write("categry was removed successfully");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DaoException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * add a category to the DB
	 * will return a text message whether such a category name was already exist
	 * else will respond with category's json representation
	 * @param request
	 * @param response
	 */
	private void addCategoryAction(HttpServletRequest request, HttpServletResponse response){
		System.out.println("addCategoryAction request received");
		String name = request.getParameter("name");
		Integer budgetLimit = Integer.parseInt( request.getParameter("budgetlimit") );
		Integer userid = Integer.parseInt( request.getParameter("userid") );
		try {
			if(dao.isCategoryNameExists(name, userid)){
				response.setStatus(409); // Conflict code - The request could not be completed due to a conflict with the current state of the resource
				response.getWriter().write("Category name is already exists");
			}else{
				Category category = new Category(name, budgetLimit, userid);
				Integer categoryId = dao.addCategory(category);
				category.setId(categoryId);
				response.getWriter().write(category.getJsonString());
				response.setStatus(200); //Status code (200) indicating the request succeeded normally.
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DaoException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * will respond with the last twelve months summery of expenses
	 * json representation
	 * @param request
	 * @param response
	 */
	private void getLastTwelveMonths(HttpServletRequest request, HttpServletResponse response){
		System.out.println("getLastTwelveMonths request received");
		Integer userid = Integer.parseInt( request.getParameter("userid") );	
		try {
			UserBudgetManager ubm = new UserBudgetManager(userid);
			String lastTwelveMonthsJson = ubm.getLastTwelveMonthsJson();
			response.setContentType("application/json");
			response.getWriter().write(lastTwelveMonthsJson);
			response.setStatus(200); //Status code (200) indicating the request succeeded normally.
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DaoException e) {
			e.printStackTrace();
		}
	}
	/**
	 * will direct the Ddo request to processRequest
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request,response);
	}


	/**
	 * wil direct the post request to the do request handling 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		doGet(request, response);
	}

}
