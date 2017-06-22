package com.jiminycricket.expensesmanager.model;

/**
 * this class provide the properties and methods for a category object
 * 
 * @author Jiminy Cricket
 *
 */
public class Category {
	Integer id;
	String name;
	Integer budgetLimit = 0;
	Integer userId = null;
	

	public Category() {
		super();
		System.out.println("Category object was created");
		// TODO Auto-generated constructor stub
	}
	
	public Category(String name, Integer budgetLimit, Integer userId) {
		this();
		this.name = name;
		this.budgetLimit = budgetLimit;
		this.userId = userId;
		this.id = null;
	}
	
	public Category(String name){
		this(name,0,0);
	}
	
	public Category(Integer id, String name, Integer budgetLimit, Integer userId) {
		this(name, budgetLimit, userId);
		this.id = id;
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getBudgetLimit() {
		return budgetLimit;
	}
	public void setBudgetLimit(Integer budgetLimit) {
		this.budgetLimit = budgetLimit;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	public String getJsonString(){
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		sb.append("\"userid\" : \"" + this.userId.toString() + "\",\n");
		sb.append("\"name\" : \"" + this.name + "\",\n");
		sb.append("\"id\" : \"" + this.id.toString() + "\",\n");
		sb.append("\"budgetlimit\" : \"" + this.budgetLimit.toString() + "\"\n");
		sb.append("}");
		
		return sb.toString();
	}
}
