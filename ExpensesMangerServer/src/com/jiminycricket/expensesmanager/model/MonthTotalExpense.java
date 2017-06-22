package com.jiminycricket.expensesmanager.model;

import java.util.Calendar;

public class MonthTotalExpense {
	Double expense = 0.00;
	Integer totalbudgetsLimit = 0;
	Integer month = null;
	Integer year = null;
	
	public MonthTotalExpense(Double expense, Integer totalbudgetsLimit, Integer month, Integer year) {
		super();
		setExpense(expense);
		setTotalbudgetsLimit(totalbudgetsLimit);
		setMonth(month);
		setYear(year);
	}
	
	public Double getExpense() {
		return expense;
	}
	public void setExpense(Double expense) {
		if(expense > 0)
			this.expense = expense;
	}
	public Integer getTotalbudgetsLimit() {
		return totalbudgetsLimit;
	}
	public void setTotalbudgetsLimit(Integer totalbudgetsLimit) {
		if(totalbudgetsLimit > 0)
			this.totalbudgetsLimit = totalbudgetsLimit;
	}
	public Integer getMonth() {
		return month;
	}
	public void setMonth(Integer month) {
		if (month >= 1 && month <=12)
			this.month = month;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		Integer thisYear = Calendar.getInstance().get(Calendar.YEAR);
		if (year >= 1970 && year <= thisYear)
			this.year = year;
	}
	public String getJsonString(){
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		sb.append("\"expense\" : " + getExpense() + ",\n");
		sb.append("\"totalbudgetsLimit\" : " + getTotalbudgetsLimit() + ",\n");
		sb.append("\"month\" : " + getMonth() + ",\n");
		sb.append("\"year\" : " + getYear() + "\n");
		sb.append("}");
		
		return sb.toString();
	}
	public void addExpense(Double newExpense){
		setExpense(getExpense() + newExpense);
	}
}
