package com.jiminycricket.expensesmanager.model;

public class User {
	
	Integer id = null;
	String username;
	String password;
	
	/**
	 * Default c'tor
	 */
	public User(){
	}
	
	public User(String username, String password){
		this();
		this.setUsername(username);
		this.setPassword(password);  //need to change this to hash function
		System.out.println("username: " + this.username + "\npassword(hash): " + this.password);
	}
	
	public User(Integer id, String username, String password){
		this(username, password);
		this.setId(id);
		System.out.println("id: " + this.id);
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public static String hashFunction(String password){
		Integer hash = 0;
		for (int i = 0; i < password.length(); i++){
		    hash = (31 * hash + password.charAt(i));
		}
		return String.valueOf(hash);
	}
	
	public StringBuffer getUserXml(){
		StringBuffer sb=new StringBuffer();
		sb.append("<?xml version='1.0' encoding='ISO-8859-1'?>\n");
		sb.append("<user>");
		sb.append("<id>" + this.id + "</id>\n");
		sb.append("<username>" + this.username + "</username>\n");
		sb.append("</user>");
		System.out.println(sb.toString());
		return sb;
	}
}
