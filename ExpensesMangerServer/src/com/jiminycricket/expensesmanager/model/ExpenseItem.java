package com.jiminycricket.expensesmanager.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExpenseItem {


	Integer id = null;
	String category;
	String comment;
	String subcategory;
	String name;
	Double cost;
	Integer userid;
	Date date;
	

	public ExpenseItem() {
		System.out.println("new ExpenseItem object was created");
	}

	public ExpenseItem(String category, String subcategory, String name, Double cost, Integer userid) {
		this();
		this.category = category;
		this.subcategory = subcategory;
		this.name = name;
		this.cost = cost;
		this.userid = userid;
		this.comment = "";
	}
	
	public ExpenseItem(String category, String subcategory, String name, Double cost, Integer userid, String comment) {
		this(category, subcategory, name, cost, userid);
		this.comment = comment;
	}
	
	public ExpenseItem(Integer id, String category, String subcategory, String name, Double cost, Integer userid, Date date) {
		this(category, subcategory, name, cost, userid);
		this.id = id;
		this.date = date;
		this.comment = "";
	}

	/*
	 * this is a new constructor with comment field that was added later on
	 */
	public ExpenseItem(Integer id, String category, String subcategory, String name, Double cost, Integer userid, Date date, String comment) {
		this(id, category, subcategory, name, cost, userid, date);
		this.comment = comment;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getSubcategory() {
		return subcategory;
	}
	public void setSubcategory(String subcategory) {
		this.subcategory = subcategory;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getCost() {
		return cost;
	}
	public void setCost(Double cost) {
		this.cost = cost;
	}
	public Integer getUserid() {
		return userid;
	}
	public void setUserid(Integer userid) {
		this.userid = userid;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	@Override
	public String toString() {
		return "ExpenseItem [id=" + id + ", category=" + category + ", subcategory=" + subcategory + ", name=" + name
				+ ", cost=" + cost + ", userid=" + userid + ", date=" + date + "]";
	}

	
	public String getJsonString(){
		StringBuilder sb = new StringBuilder();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		sb.append("{\n");
		sb.append("\"id\" : \"" + this.id.toString() + "\",\n");
		sb.append("\"name\" : \"" + this.name + "\",\n");
		sb.append("\"comment\" : \"" + this.comment + "\",\n");
		sb.append("\"category\" : \"" + this.category + "\",\n");
		sb.append("\"subcategory\" : \"" + this.subcategory + "\",\n");
		sb.append("\"cost\" : \"" + this.cost.toString() + "\",\n");
		sb.append("\"userid\" : \"" + this.userid.toString() + "\",\n");
		sb.append("\"date\" : \"" + dateFormat.format(date) + "\"\n");
		sb.append("}");
		
		return sb.toString();
	}
}
