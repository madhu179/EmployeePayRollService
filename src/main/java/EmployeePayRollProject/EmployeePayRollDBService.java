package EmployeePayRollProject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
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

	public List<EmployeePayRoll> readData() throws CustomSQLException {
		List<EmployeePayRoll> employeePayRollList = new ArrayList<EmployeePayRoll>();
		String query = "select * from employee_payroll";
		Statement statement;
		ResultSet result = null;
		try (Connection connection = this.getConnection();) {
			statement = connection.createStatement();
			result = statement.executeQuery(query);
			employeePayRollList = getDatafromResultset(result);
		} catch (SQLException e) {
			throw new CustomSQLException(e.getMessage(),CustomSQLException.Exception_Type.READ_FAILED);
		}
		return employeePayRollList;
	}

	public int updateSalary(int n, String name, Double salary) throws CustomSQLException {
		if (n == 1)
			return this.updateSalaryUsingStatement(name, salary);
		else
			return this.updateSalaryUsingPreparedStatement(name, salary);
	}

	private int updateSalaryUsingPreparedStatement(String name, Double salary) throws CustomSQLException {
		try (Connection connection = this.getConnection();) {
			preparedStatement = connection
					.prepareStatement("update employee_payroll set salary = ? where name = ?");
			preparedStatement.setDouble(1, salary);
			preparedStatement.setString(2, name);
			int result = preparedStatement.executeUpdate();
			return result;
		} catch (SQLException e) {
			throw new CustomSQLException(e.getMessage(),CustomSQLException.Exception_Type.UPDATE_FAILED);
		}
	}

	private int updateSalaryUsingStatement(String name, Double salary) throws CustomSQLException {
		String query = String.format("update employee_payroll set salary = %.2f where name = '%s' ;", salary, name);
		Statement statement;
		int result = 0;
		try (Connection connection = this.getConnection();) {
			statement = connection.createStatement();
			result = statement.executeUpdate(query);
			return result;
		} catch (SQLException e) {
			throw new CustomSQLException(e.getMessage(),CustomSQLException.Exception_Type.UPDATE_FAILED);
		}
	}
	
	public EmployeePayRoll preparedStatementReadData(String name) throws CustomSQLException {	
		List<EmployeePayRoll> employeePayRollList = new ArrayList<EmployeePayRoll>();
		try (Connection connection = this.getConnection();) {
			preparedStatement = connection
					.prepareStatement("select * from  employee_payroll where name = ?");
			preparedStatement.setString(1, name);
			ResultSet result = preparedStatement.executeQuery();
			employeePayRollList = getDatafromResultset(result);
			return employeePayRollList.get(0);
		} catch (SQLException e) {
			throw new CustomSQLException(e.getMessage(),CustomSQLException.Exception_Type.READ_FAILED);
		}
	}
	
	public List<EmployeePayRoll> getDataInDateRange(String startDate, String endDate) throws CustomSQLException {
		List<EmployeePayRoll> employeePayRollList = new ArrayList<EmployeePayRoll>();
		try (Connection connection = this.getConnection();) {
			preparedStatement = connection
					.prepareStatement("select * from employee_payroll where startdate between cast(? as date) and cast(? as date)");
			preparedStatement.setString(1,startDate);
			preparedStatement.setString(2,endDate);			
			ResultSet result = preparedStatement.executeQuery();
			employeePayRollList = getDatafromResultset(result);
			return employeePayRollList;
		} catch (SQLException e) {
			throw new CustomSQLException(e.getMessage(),CustomSQLException.Exception_Type.READ_IN_DATERANGE_FAILED);
		}
	}
	
	public HashMap<String, Double> getMinMaxSumAvgCount() throws CustomSQLException {
		HashMap<String,Double> functionMap = new HashMap<String,Double>();
		List<Double> min = getDataBasedOnQuery("select min(salary),gender from employee_payroll group by gender");
		List<Double> max = getDataBasedOnQuery("select max(salary),gender from employee_payroll group by gender");
		List<Double> sum = getDataBasedOnQuery("select sum(salary),gender from employee_payroll group by gender");
		List<Double> avg = getDataBasedOnQuery("select avg(salary),gender from employee_payroll group by gender;");
		List<Double> count = getDataBasedOnQuery("select count(*),gender from employee_payroll group by gender;");
		functionMap.put("minMale",min.get(0));
		functionMap.put("minFemale",min.get(1));
		functionMap.put("maxMale",max.get(0));
		functionMap.put("maxFemale",max.get(1));
		functionMap.put("sumMale",sum.get(0));
		functionMap.put("sumFemale",sum.get(1));
		functionMap.put("avgMale",avg.get(0));
		functionMap.put("avgFemale",avg.get(1));
		functionMap.put("countMale",count.get(0));
		functionMap.put("countFemale",count.get(1));	
		return functionMap;
	}
	
	public List<Double>  getDataBasedOnQuery(String query) throws CustomSQLException {
		List<Double> functionList = new ArrayList<Double>();
		Statement statement;
		ResultSet result;
		try (Connection connection = this.getConnection();) {
			statement = connection.createStatement();
			result = statement.executeQuery(query);
			int i=0;
			while(result.next())
			{
                i=i+1;
                functionList.add(result.getDouble(1));
			}
			return functionList;
		} catch (SQLException e) {
			throw new CustomSQLException(e.getMessage(),CustomSQLException.Exception_Type.QUERY_FAILED);
		}
	}

	private Connection getConnection() throws CustomSQLException {
		String jdbcURL = "jdbc:mysql://localhost:3306/employee_payroll_service?useSSL=false";
		String userName = "root";
		String password = "Fightclub@8.8";
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(jdbcURL, userName, password);
		} catch (SQLException e) {
			throw new CustomSQLException(e.getMessage(),CustomSQLException.Exception_Type.CONNECTION_FAILED);
		}
		return connection;
	}

	public EmployeePayRoll getEmployee(String name) throws CustomSQLException {
		List<EmployeePayRoll> employeePayRollList = new ArrayList<EmployeePayRoll>();
		employeePayRollList = this.readData();
		return employeePayRollList.stream()
				.filter(e -> e.getName().equals(name))
				.findFirst()
				.orElse(null);
	}
	
	public List<EmployeePayRoll> getDatafromResultset(ResultSet result) throws CustomSQLException
	{
		List<EmployeePayRoll> employeePayRollList = new ArrayList<EmployeePayRoll>();
		try {
			while (result.next()) {	
					employeePayRollList.add(
							new EmployeePayRoll(result.getInt("id"), result.getString("name"),
									result.getDouble("salary"), result.getDate("startdate").toLocalDate()));
				} 
			}catch (SQLException e) {
				throw new CustomSQLException(e.getMessage(),CustomSQLException.Exception_Type.READ_FAILED);
			}
		return employeePayRollList;		
	}

}
