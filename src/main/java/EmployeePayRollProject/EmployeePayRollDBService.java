package EmployeePayRollProject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayRollDBService {

	public List<EmployeePayRoll> readData() {
		List<EmployeePayRoll> employeePayRollList = new ArrayList<EmployeePayRoll>();
		String query = "select * from employee_payroll";
		Statement statement;
		ResultSet result=null;
		try(Connection connection = this.getConnection();) {
			statement = connection.createStatement();
			 result = statement.executeQuery(query);
			 while(result.next())
				{
				 employeePayRollList.add(new EmployeePayRoll(result.getInt("id"),result.getString("name"),result.getString("gender"),result.getDouble("salary"),result.getDate("startdate").toLocalDate()));	
				}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayRollList;
	}

	private Connection getConnection() {
		String jdbcURL = "jdbc:mysql://localhost:3306/employee_payroll_service?useSSL=false";
		String userName = "root";
		String password = "Fightclub@8.8";
		Connection connection=null;
		try {
			 connection = DriverManager.getConnection(jdbcURL, userName, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return connection;
	}

}
