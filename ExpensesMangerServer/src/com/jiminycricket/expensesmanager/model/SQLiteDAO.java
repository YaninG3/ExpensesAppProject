package com.jiminycricket.expensesmanager.model;
import java.util.Date;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * this class provides implementations for the IexpnsmngrDAO API
 * using SQLite JDBC
 * @author Jiminy Cricket
 *
 */
public class SQLiteDAO implements IexpnsmngrDAO
{
	private String dBpath;
	
	/**
	 * default c'tor
	 * @throws DaoException 
	 */
	public SQLiteDAO() throws DaoException{
		System.out.println("new SQLiteDAO object was created");
		dBpath = "expmngrdb.sqlite";
		initDB();
	}
	
	/**
	 * initialize database tables
	 * @throws DaoException 
	 */
	private void initDB() throws DaoException{
		initCategoriesTable();
		initUsersTable();
		initExpensesTable();
	}
	
	/**
	 * initialize Categories Table
	 * @throws DaoException 
	 */
	private void initCategoriesTable() throws DaoException{
		String sql = "CREATE TABLE IF NOT EXISTS \"Categories\" (\"name\" TEXT,\"budgetlimit\" INTEGER DEFAULT (null) ,\"userid\" INTEGER,\"id\" INTEGER PRIMARY KEY  NOT NULL )";
		Connection conn = getConnector();
		Statement stmt;
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
		    stmt.close();
		    conn.commit();
		    conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new DaoException(e.getMessage(), e);
		}
	}
	
	/**
	 * initialize Users Table
	 * @throws DaoException 
	 */
	private void initUsersTable() throws DaoException{
		String sql = "CREATE TABLE IF NOT EXISTS \"Users\" (\"id\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , \"username\" TEXT NOT NULL  UNIQUE , \"password\" TEXT NOT NULL )";
		Connection conn = getConnector();
		Statement stmt;
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
		    stmt.close();
		    conn.commit();
		    conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new DaoException(e.getMessage(), e);
		}
	}
	/**
	 * initialize Expenses Table
	 * @throws DaoException 
	 */
	private void initExpensesTable() throws DaoException{
		String sql = "CREATE TABLE IF NOT EXISTS \"Expenses\" (\"id\" INTEGER PRIMARY KEY  NOT NULL ,\"category\" TEXT,\"subcategory\" TEXT,\"name\" TEXT NOT NULL ,\"cost\" NUMERIC NOT NULL ,\"date\" DATETIME NOT NULL  DEFAULT (CURRENT_DATE) ,\"userid\" INTEGER, \"comment\" TEXT)";
		Connection conn = getConnector();
		Statement stmt;
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
		    stmt.close();
		    conn.commit();
		    conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new DaoException(e.getMessage(), e);
		}
	}
	
	/**
	 * connect a SQLite database, will create a new database if one is not created yet 
	 * @return Connection
	 * @throws DaoException 
	 */
	private Connection getConnector() throws DaoException
	 {
		 Connection conn = null;
		 System.out.println("Database path: " + dBpath);
		 try {
		      Class.forName("org.sqlite.JDBC");
	          conn = DriverManager.getConnection("jdbc:sqlite:" + dBpath);
		      conn.setAutoCommit(false);
		      System.out.println("Opened database successfully");
	    } catch ( Exception e ) {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      System.exit(1);
		      throw new DaoException(e.getMessage(), e);
	    }
		return conn;
  	}

	/**
	 * add new user to DB
	 * will return new user id;
	 * @throws DaoException 
	 */
	@Override
	public Integer addNewUser(User user) throws DaoException {
		
		String query = "INSERT INTO \"Users\" (username,password) " +
						"VALUES ('" + user.username + "','" + user.password + "');";
		Connection conn = getConnector();
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(query);
		    stmt.close();
		    conn.commit();
		    conn.close();
			User newUser = getUserByUsername(user.getUsername());
			System.out.println("User has been added (" + newUser.getId() + ", " + newUser.getUsername() + ", " + newUser.getPassword() + ")");
			return newUser.getId();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new DaoException(e.getMessage(), e);
		}
		finally{
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new DaoException(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * get a user object by matching user ID
	 * @throws DaoException 
	 */
	@Override
	public User getUserById(Integer id) throws DaoException {
		Connection conn = getConnector();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT * FROM USERS " +
												"WHERE ID=" + id );
			User user = new User();
			user.setId(rs.getInt("id"));
			user.setUsername(rs.getString("username"));
			user.setPassword(rs.getString("password"));
			rs.close();
			stmt.close();
			conn.commit();
			conn.close();
			System.out.println("user retrived from database: " + user.getId() + ", " + user.getUsername());
			return user;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new DaoException(e.getMessage(), e);
		}
		finally{
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new DaoException(e.getMessage(), e);
			}
		}
	}

	/**
	 * return User object by matching username
	 * @throws DaoException 
	 */
	@Override
	public User getUserByUsername(String username) throws DaoException {
		Connection conn = getConnector();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT * FROM USERS " +
												"WHERE USERNAME='" + username + "'");
			User user = new User();
			user.setId(rs.getInt("id"));
			user.setUsername(rs.getString("username"));
			user.setPassword(rs.getString("password"));
			rs.close();
			stmt.close();
			conn.commit();
			conn.close();
			System.out.println("user retrived from database: " + user.getId() + ", " + user.getUsername());
			return user;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new DaoException(e.getMessage(), e);
		}
		finally{
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new DaoException(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * return true/false whether username exists
	 * @throws DaoException 
	 */
	@Override
	public Boolean usernameExists(String username) throws DaoException {
		Connection conn = getConnector();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT * FROM Users " +
												"WHERE username='" + username + "';");
			if (rs.next() == true){
				rs.close();
				stmt.close();
				conn.close();
				return true;
			}
			rs.close();
			stmt.close();
			conn.commit();
			conn.close();
			return false;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new DaoException(e.getMessage(), e);
		}
		finally{
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new DaoException(e.getMessage(), e);
			}
		}
		
	}

	/**
	 * update user's password by matching used ID
	 * @throws DaoException 
	 */
	@Override
	public void updateUserPassword(Integer id, String password) throws DaoException {
		String hashPassword = User.hashFunction(password);
		try {
			Connection conn = getConnector();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate( "UPDATE Users " +
								"SET password='" + hashPassword +"' "+
								"WHERE ID=" + id + ";");
			stmt.close();
			conn.commit();
			conn.close();
			System.out.println("password has been modified");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new DaoException(e.getMessage(), e);
		}
		
	}
	
	/**
	 * return true/false check is a password matched the username (authentication)
	 * @throws DaoException 
	 */
	@Override
	public Boolean doesPasswordFitsUsername(String username, String password) throws DaoException {
		User user = this.getUserByUsername(username);
		if (user == null){
			return false;
		}
		
		if (user.getPassword().equals(password)){
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * add new user object to the DB and return the user ID
	 * @throws DaoException 
	 */
	@Override
	public Integer addNewExpenseItem(ExpenseItem item) throws DaoException {
		String sql = "INSERT INTO Expenses (category,subcategory,name,comment,cost,userid) " +
						"VALUES ('" + item.getCategory() + "','" +
									item.getSubcategory() + "','" +
									item.getName()+ "','" +
									item.getComment()+ "'," +
									item.getCost()+ "," +
									item.getUserid() + ");";
		try {
			Connection conn = getConnector();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		    stmt.close();
		    conn.commit();
		    System.out.println("a new item has been added");
		    ResultSet rs = stmt.executeQuery("SELECT id FROM Expenses ORDER BY id DESC LIMIT 1;");
		    int newId = rs.getInt("id");
		    rs.close();
		    stmt.close();
		    conn.close();
			return newId;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new DaoException(e.getMessage(), e);
		}
	}

	/**
	 * get all expenses that have the user ID specified
	 * @throws DaoException 
	 */
	@Override
	public ExpenseItemList getExpensesByUserId(Integer userid) throws DaoException {
		String query = "SELECT * FROM Expenses WHERE userid=" + userid + ";";
		List<ExpenseItem> expensesList = new ArrayList<ExpenseItem>();
		try {
			Connection conn = getConnector();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				Integer itemId = rs.getInt("id");
				String category = rs.getString("category");
				String subcategory = rs.getString("subcategory");
				String name = rs.getString("name");
				String comment = rs.getString("comment");
				Double cost = rs.getDouble("cost");
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date date = null;
				try {
					date = (Date) dateFormat.parse(rs.getString("date"));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new DaoException(e.getMessage(), e);
				}
				expensesList.add(new ExpenseItem(itemId, category, subcategory, name, cost, userid, date, comment));
			}
			rs.close();
			stmt.close();
			conn.close();
			ExpenseItemList expensesListObject = new ExpenseItemList(expensesList); 
			return expensesListObject;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new DaoException(e.getMessage(), e);
		}
	}

	/**
	 * get a specific expense item object by the matched item id
	 * @throws DaoException 
	 */
	public ExpenseItem getExpenseItemByItemid(Integer itemid) throws DaoException{
		String query = "SELECT * FROM Expenses WHERE id=" + itemid + ";";
		try {
			Connection conn = getConnector();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			Integer userId = rs.getInt("userid");
			String category = rs.getString("category");
			String subcategory = rs.getString("subcategory");
			String name = rs.getString("name");
			String comment = rs.getString("comment");
			Double cost = rs.getDouble("cost");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = null;
			try {
				date = (Date) dateFormat.parse(rs.getString("date"));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new DaoException(e.getMessage(), e);
			}
			rs.close();
			stmt.close();
			conn.close();
			return new ExpenseItem(itemid, category, subcategory, name, cost, userId, date, comment);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new DaoException(e.getMessage(), e);
		}
	}
	
	/**
	 * delete the expense item that match the given id
	 * @throws DaoException 
	 */
	@Override
	public void deleteExpenseItemById(Integer id) throws DaoException {
		try {
			Connection conn = getConnector();
			Statement stmt = conn.createStatement();
			String sql = "DELETE FROM Expenses WHERE id=" + id + ";";
			stmt.executeUpdate(sql);
			conn.commit();
			stmt.close();
			conn.close();
			System.out.println("item id " + id + " was deleted from database");
		} catch(SQLException e){
			e.printStackTrace();
			throw new DaoException(e.getMessage(), e);
		}
	}

	/**
	 * update an expense item with the expense item properties recieved
	 * return the updated object
	 * @throws DaoException 
	 */
	@Override
	public ExpenseItem updateExpenseItem(ExpenseItem item) throws DaoException {
		try {
			Connection conn = getConnector();
			Statement stmt = conn.createStatement();
			String sql = "UPDATE Expenses" +
						" SET category='" + item.getCategory() +
						"', subcategory='" + item.getSubcategory() +
						"', name='" + item.getName() +
						"', comment='" + item.getComment() +
						"', cost=" + item.getCost() +
						" WHERE id=" + item.id + ";";
			stmt.executeUpdate(sql);
			conn.commit();
			stmt.close();
			conn.close();
			//System.out.println("item id " + id + " was deleted from database");
		} catch(SQLException e){
			e.printStackTrace();
			throw new DaoException(e.getMessage(), e);
		}
		
		return getExpenseItemByItemid(item.id);
	}

	/**
	 * check whether there are any items with the given category 
	 * @throws DaoException 
	 */
	public Boolean areItemsStillExistsWithCategory(String categoryName, Integer userid) throws DaoException{
		String query = "SELECT COUNT(*) AS categoryMatches FROM Expenses WHERE category='" + categoryName + "' AND userid=" + userid + ";";
		try {
			Connection conn = getConnector();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			Integer count = rs.getInt("categoryMatches");	
			rs.close();
			stmt.close();
			conn.close();
			if(count==0)
				return false;
			else
				return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new DaoException(e.getMessage(), e);
		}
	}
	
	/**
	 * add the given category object to the DB
	 * return the new category id
	 * @throws DaoException 
	 */
	@Override
	public Integer addCategory(Category category) throws DaoException {
		String sql = "INSERT INTO Categories (name,budgetlimit,userid) " +
				"VALUES ('" + category.getName() + "'," +
							category.getBudgetLimit() + "," +
							category.getUserId() + ");";
		try {
			Connection conn = getConnector();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		    stmt.close();
		    conn.commit();
		    System.out.println("a new category has been added");
		    ResultSet rs = stmt.executeQuery("SELECT id FROM Categories ORDER BY id DESC LIMIT 1;");
		    Integer newId = rs.getInt("id");
		    rs.close();
		    stmt.close();
		    conn.close();
			return newId;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new DaoException(e.getMessage(), e);
		}
	}

	/**
	 * delete the category that matched category id given
	 * @throws DaoException 
	 */
	@Override
	public void deleteCategory(Integer categoryId) throws DaoException {
		try {
			Connection conn = getConnector();
			Statement stmt = conn.createStatement();
			String sql = "DELETE FROM Categories WHERE id=" + categoryId + ";";
			stmt.executeUpdate(sql);
			conn.commit();
			stmt.close();
			conn.close();
			System.out.println("category id " + categoryId + " was deleted from database");
		} catch(SQLException e){
			e.printStackTrace();
			throw new DaoException(e.getMessage(), e);
		}
	}

	/**
	 * return a category list object that contain all categories of a certain user
	 * @throws DaoException 
	 */
	@Override
	public CategoryList getCategoryListByUserId(Integer userid) throws DaoException {
		String query = "SELECT * FROM Categories WHERE userid=" + userid + ";";
		List<Category> cl = new ArrayList<Category>();
		try {
			Connection conn = getConnector();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				Integer categoryId = rs.getInt("id");
				String name = rs.getString("name");
				Integer budgetLimit = rs.getInt("budgetlimit");
				cl.add(new Category(categoryId, name, budgetLimit, userid));
			}
			rs.close();
			stmt.close();
			conn.close();
			CategoryList categoryListObject = new CategoryList(cl); 
			return categoryListObject;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new DaoException(e.getMessage(), e);
		}
	}

	/**
	 * check whether the category name given is already exist for the given user id
	 * @throws DaoException 
	 */
	@Override
	public Boolean isCategoryNameExists(String name, Integer userid) throws DaoException {
		String query = "SELECT COUNT(*) AS sameNameMatches FROM Categories WHERE name='" + name + "' AND userid=" + userid + ";";
		try {
			Connection conn = getConnector();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			Integer count = rs.getInt("sameNameMatches");	
			rs.close();
			stmt.close();
			conn.close();
			if(count==0)
				return false;
			else
				return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new DaoException(e.getMessage(), e);
		}
	}
}

