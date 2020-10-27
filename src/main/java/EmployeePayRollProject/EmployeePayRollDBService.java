package EmployeePayRollProject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayRollDBService {
	
	private PreparedStatement preparedStatement;
	
	private static EmployeePayRollDBService employeePayRollDBService;
	
	private EmployeePayRollDBService(){	
	}
	
	public static EmployeePayRollDBService getInstance()
	{
		if(employeePayRollDBService==null)
			employeePayRollDBService = new EmployeePayRollDBService();
		return employeePayRollDBService;
	}

	public List<EmployeePayRoll> readData() {
		List<EmployeePayRoll> employeePayRollList = new ArrayList<EmployeePayRoll>();
		String query = "select * from employee_payroll";
		Statement statement;
		ResultSet result = null;
		try (Connection connection = this.getConnection();) {
			statement = connection.createStatement();
			result = statement.executeQuery(query);
			employeePayRollList = getDatafromResultset(result);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayRollList;
	}

	public int updateSalary(int n, String name, Double salary) {
		if (n == 1)
			return this.updateSalaryUsingStatement(name, salary);
		else
			return this.updateSalaryUsingPreparedStatement(name, salary);
	}

	private int updateSalaryUsingPreparedStatement(String name, Double salary) {
		try (Connection connection = this.getConnection();) {
			preparedStatement = connection
					.prepareStatement("update employee_payroll set salary = ? where name = ?");
			preparedStatement.setDouble(1, salary);
			preparedStatement.setString(2, name);
			int result = preparedStatement.executeUpdate();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private int updateSalaryUsingStatement(String name, Double salary) {
		String query = String.format("update employee_payroll set salary = %.2f where name = '%s' ;", salary, name);
		Statement statement;
		int result = 0;
		try (Connection connection = this.getConnection();) {
			statement = connection.createStatement();
			result = statement.executeUpdate(query);
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public EmployeePayRoll preparedStatementReadData(String name) {	
		List<EmployeePayRoll> employeePayRollList = new ArrayList<EmployeePayRoll>();
		try (Connection connection = this.getConnection();) {
			preparedStatement = connection
					.prepareStatement("select * from  employee_payroll where name = ?");
			preparedStatement.setString(1, name);
			ResultSet result = preparedStatement.executeQuery();
			employeePayRollList = getDatafromResultset(result);
			return employeePayRollList.get(0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Connection getConnection() {
		String jdbcURL = "jdbc:mysql://localhost:3306/employee_payroll_service?useSSL=false";
		String userName = "root";
		String password = "Fightclub@8.8";
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(jdbcURL, userName, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connection;
	}

	public EmployeePayRoll getEmployee(String name) {
		List<EmployeePayRoll> employeePayRollList = new ArrayList<EmployeePayRoll>();
		employeePayRollList = this.readData();
		return employeePayRollList.stream()
				.filter(e -> e.getName().equals(name))
				.findFirst()
				.orElse(null);
	}
	
	public List<EmployeePayRoll> getDatafromResultset(ResultSet result)
	{
		List<EmployeePayRoll> employeePayRollList = new ArrayList<EmployeePayRoll>();
		try {
			while (result.next()) {
				
					employeePayRollList.add(
							new EmployeePayRoll(result.getInt("id"), result.getString("name"),
									result.getDouble("salary"), result.getDate("startdate").toLocalDate()));
				} 
			}catch (SQLException e) {
				e.printStackTrace();
			}
		return employeePayRollList;		
	}

	

}
