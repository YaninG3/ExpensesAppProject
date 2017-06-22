package com.jiminycricket.expensesmanager.model;

import java.util.ArrayList;
import java.util.List;

/*
 * ExpenseItemList represent a list of expenses items
 * the main goal of this object is to generate a Json String for the whole expenses items list
 */
public class ExpenseItemList {

	List<ExpenseItem> list = null;
	
	public ExpenseItemList() {
		super();
		list = new ArrayList<ExpenseItem>();
		// TODO Auto-generated constructor stub
	}

	public ExpenseItemList(List<ExpenseItem> list) {
		super();
		this.list = list;
	}

	public List<ExpenseItem> getList() {
		return list;
	}

	public void setList(List<ExpenseItem> list) {
		this.list = list;
	}
	
	public String getJsonString(){
		StringBuilder sb = new StringBuilder();
		sb.append("{\"expenses\" : [\n");
		for(int i=0; i<this.list.size(); i++ ){
			sb.append(this.list.get(i).getJsonString());
			//if it's not the last item put an ","
			if(i < this.list.size()-1){
				sb.append(",\n");
			}
		}
		sb.append("\n]}");
		return sb.toString();
	}
	
	public Integer size(){
		return this.list.size();
	}
	
	public ExpenseItem get(Integer index){
		return this.list.get(index);
	}
	
	public void push(ExpenseItem item){
		list.add(item);
	}
	
}
