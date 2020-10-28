package EmployeePayRollProject;

public class CustomSQLException extends Exception {
	
	enum Exception_Type{
		CONNECTION_FAILED,
		QUERY_FAILED,
		READ_FAILED,
		UPDATE_FAILED,
		READ_IN_DATERANGE_FAILED
	}
	
	public Exception_Type type;
	
	public CustomSQLException(String message,Exception_Type type)
	{
		super(message);
		this.type = type;
	}

}
