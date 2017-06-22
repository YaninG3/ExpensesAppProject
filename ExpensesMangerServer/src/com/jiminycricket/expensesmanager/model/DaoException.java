package com.jiminycricket.expensesmanager.model;

public class DaoException extends Exception{

	/**
	 * an exception that is used by the {@link HibernateToDoListDAO} 
	 */
	private static final long serialVersionUID = 1L;
	public DaoException(String msg) {
		super(msg);
	}
	public DaoException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
}
