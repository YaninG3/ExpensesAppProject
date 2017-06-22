package com.jiminycricket.expensesmanager.model;

/**
 * interface class describing the API for comunicating with the database 
 * @author Jiminy Cricket
 *
 */
public interface IexpnsmngrDAO {

	//API for users handling
	public Integer addNewUser(User user) throws DaoException;
	public User getUserById(Integer id) throws DaoException;
	public User getUserByUsername(String username) throws DaoException;
	public void updateUserPassword(Integer id, String password) throws DaoException;
	public Boolean usernameExists(String username) throws DaoException;
	public Boolean doesPasswordFitsUsername(String username, String password) throws DaoException;
	
	//API for expense items handling
	public Integer addNewExpenseItem(ExpenseItem item) throws DaoException;
	public ExpenseItemList getExpensesByUserId(Integer userid) throws DaoException;
	public ExpenseItem getExpenseItemByItemid(Integer itemid) throws DaoException;
	public void deleteExpenseItemById(Integer id) throws DaoException;
	public ExpenseItem updateExpenseItem(ExpenseItem item) throws DaoException;
	public Boolean areItemsStillExistsWithCategory(String categoryName, Integer userid) throws DaoException;
	
	//API for category handling
	public Integer addCategory(Category category) throws DaoException;
	public void deleteCategory(Integer categoryId) throws DaoException;
	public CategoryList getCategoryListByUserId(Integer userid) throws DaoException;
	public Boolean isCategoryNameExists(String name, Integer userid) throws DaoException;
	
}
