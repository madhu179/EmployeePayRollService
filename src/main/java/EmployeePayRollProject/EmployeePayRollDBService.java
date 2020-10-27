package EmployeePayRollProject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayRollDBService {
	
	List<EmployeePayRoll> employeePayRollList = new ArrayList<EmployeePayRoll>();

	public List<EmployeePayRoll> readData() {
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
	
	public int updateSalary(String name,Double salary){	
		return this.updateSalaryUsingStatement(name,salary);
	}

	private int updateSalaryUsingStatement(String name, Double salary) {
		String query = String.format("update employee_payroll set salary = %.2f where name = '%s' ;",salary,name);
		Statement statement;
		int result=0;
		try(Connection connection = this.getConnection();) {
			statement = connection.createStatement();
			 result = statement.executeUpdate(query);
			 return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
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

	public EmployeePayRoll getEmployee(String name) {
		this.readData();
		return employeePayRollList.stream().filter(e->e.getName().equals(name)).findFirst().orElse(null);
	}

}
