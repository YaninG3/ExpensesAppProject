package com.jiminycricket.expensesmanager.model;

import java.util.List;

public class CategoryList {

	List<Category> list = null;

	public CategoryList() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CategoryList(List<Category> list) {
		super();
		this.list = list;
	}

	public List<Category> getList() {
		return list;
	}

	public void setList(List<Category> list) {
		this.list = list;
	}
	
	public String getJsonString(){
		StringBuilder sb = new StringBuilder();
		sb.append("{\"categories\" : [\n");
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
	
	public Category get(Integer index){
		return this.list.get(index);
	}
	
	public Category getCategoryByName(String categoryName){
		Category category;
		for (int i=0; i<list.size(); i++){
			category = list.get(i);
			if (category.getName().equals(categoryName)){
				return category;
			}
		}
		return null;
	}
}
