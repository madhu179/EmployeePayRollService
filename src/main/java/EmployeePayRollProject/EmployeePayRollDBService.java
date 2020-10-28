package EmployeePayRollProject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EmployeePayRollDBService {

	private PreparedStatement preparedStatement;

	private static EmployeePayRollDBService employeePayRollDBService;

	private EmployeePayRollDBService() {
	}

	public static EmployeePayRollDBService getInstance() {
		if (employeePayRollDBService == null)
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
			throw new CustomSQLException(e.getMessage(), CustomSQLException.Exception_Type.READ_FAILED);
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
			preparedStatement = connection.prepareStatement("update employee_payroll set salary = ? where name = ?");
			preparedStatement.setDouble(1, salary);
			preparedStatement.setString(2, name);
			int result = preparedStatement.executeUpdate();
			return result;
		} catch (SQLException e) {
			throw new CustomSQLException(e.getMessage(), CustomSQLException.Exception_Type.UPDATE_FAILED);
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
			throw new CustomSQLException(e.getMessage(), CustomSQLException.Exception_Type.UPDATE_FAILED);
		}
	}

	public EmployeePayRoll preparedStatementReadData(String name) throws CustomSQLException {
		List<EmployeePayRoll> employeePayRollList = new ArrayList<EmployeePayRoll>();
		try (Connection connection = this.getConnection();) {
			preparedStatement = connection.prepareStatement("select * from  employee_payroll where name = ?");
			preparedStatement.setString(1, name);
			ResultSet result = preparedStatement.executeQuery();
			employeePayRollList = getDatafromResultset(result);
			return employeePayRollList.get(0);
		} catch (SQLException e) {
			throw new CustomSQLException(e.getMessage(), CustomSQLException.Exception_Type.READ_FAILED);
		}
	}

	public List<EmployeePayRoll> getDataInDateRange(String startDate, String endDate) throws CustomSQLException {
		List<EmployeePayRoll> employeePayRollList = new ArrayList<EmployeePayRoll>();
		try (Connection connection = this.getConnection();) {
			preparedStatement = connection.prepareStatement(
					"select * from employee_payroll where id in (select emp_id from employee_department where start_date between cast(? as date) and cast(? as date))");
			preparedStatement.setString(1, startDate);
			preparedStatement.setString(2, endDate);
			ResultSet result = preparedStatement.executeQuery();
			employeePayRollList = getDatafromResultset(result);
			System.out.println("here "+employeePayRollList.size());
			return employeePayRollList;
		} catch (SQLException e) {
			throw new CustomSQLException(e.getMessage(), CustomSQLException.Exception_Type.READ_IN_DATERANGE_FAILED);
		}
	}

	public HashMap<String, Double> getMinMaxSumAvgCount() throws CustomSQLException {
		HashMap<String, Double> functionMap = new HashMap<String, Double>();
		List<Double> min = getDataBasedOnQuery("select min(salary),gender from employee_payroll group by gender");
		List<Double> max = getDataBasedOnQuery("select max(salary),gender from employee_payroll group by gender");
		List<Double> sum = getDataBasedOnQuery("select sum(salary),gender from employee_payroll group by gender");
		List<Double> avg = getDataBasedOnQuery("select avg(salary),gender from employee_payroll group by gender;");
		List<Double> count = getDataBasedOnQuery("select count(*),gender from employee_payroll group by gender;");
		functionMap.put("minMale", min.get(0));
		functionMap.put("minFemale", min.get(1));
		functionMap.put("maxMale", max.get(0));
		functionMap.put("maxFemale", max.get(1));
		functionMap.put("sumMale", sum.get(0));
		functionMap.put("sumFemale", sum.get(1));
		functionMap.put("avgMale", avg.get(0));
		functionMap.put("avgFemale", avg.get(1));
		functionMap.put("countMale", count.get(0));
		functionMap.put("countFemale", count.get(1));
		return functionMap;
	}

	public List<Double> getDataBasedOnQuery(String query) throws CustomSQLException {
		List<Double> functionList = new ArrayList<Double>();
		Statement statement;
		ResultSet result;
		try (Connection connection = this.getConnection();) {
			statement = connection.createStatement();
			result = statement.executeQuery(query);
			int i = 0;
			while (result.next()) {
				i = i + 1;
				functionList.add(result.getDouble(1));
			}
			return functionList;
		} catch (SQLException e) {
			throw new CustomSQLException(e.getMessage(), CustomSQLException.Exception_Type.QUERY_FAILED);
		}
	}

	public EmployeePayRoll addEmployee(String name, String gender, double salary, LocalDate startDate)
			throws CustomSQLException {
		int employeeId = 0;
		EmployeePayRoll employee = null;
		String query = String.format(
				"insert into employee_payroll(name,gender,salary,startdate) " + "values('%s','%s',%s,'%s')", name,
				gender, salary, startDate);
		try (Connection connection = this.getConnection();) {
			Statement statement = connection.createStatement();
			int rowAffected = statement.executeUpdate(query, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet result = statement.getGeneratedKeys();
				if (result.next())
					employeeId = result.getInt(1);
			}
//			employee = new EmployeePayRoll(employeeId,name,salary,startDate);
			return employee;
		} catch (SQLException e) {
			throw new CustomSQLException(e.getMessage(), CustomSQLException.Exception_Type.ADD_FAILED);
		}
	}

	public EmployeePayRoll addEmployeeAndPayRoll(String name, String gender, double salary, int companyId, String departmentName, LocalDate startDate)
			throws CustomSQLException {
		int employeeId = 0;
		EmployeePayRoll employee = null;
		String query = String.format(
				"insert into employee_payroll(name,gender,salary,company_id) " + "values('%s','%s',%s,'%s')", name,
				gender, salary, companyId);
		Connection connection = null;
		try {
			connection = this.getConnection();
			connection.setAutoCommit(false);
		} catch (SQLException e2) {
			e2.printStackTrace();
		}
		try (Statement statement = connection.createStatement();) {

			int rowAffected = statement.executeUpdate(query, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet result = statement.getGeneratedKeys();
				if (result.next())
					employeeId = result.getInt(1);
			}
		} catch (SQLException e) {
			try {
				connection.rollback();
				return employee;
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new CustomSQLException(e.getMessage(), CustomSQLException.Exception_Type.ADD_FAILED);
		}
		
		int departmentId=0;
		try {		
			PreparedStatement preparedStatement3 = connection
					.prepareStatement("select * from department where name = ?");
			preparedStatement3.setString(1, departmentName);
			ResultSet result3 = preparedStatement3.executeQuery();
			while (result3.next()) {
				departmentId = (result3.getInt("id"));
			}
		} catch (SQLException e2) {
			e2.printStackTrace();
		}		
		query = String.format(
				"insert into employee_department(emp_id,dept_id,start_date) " + "values('%s',%s,'%s')", employeeId,
				departmentId,startDate);	
		try (Statement statement = connection.createStatement();) {
			int rowAffected = statement.executeUpdate(query);
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new CustomSQLException(e.getMessage(), CustomSQLException.Exception_Type.ADD_FAILED);
		}

		try (Statement statement = connection.createStatement();) {
			double basic_pay = salary;
			double deductions = salary * 0.2;
			double taxablePay = salary - deductions;
			double tax = taxablePay * 0.1;
			double netPay = salary - tax;
			query = String.format("insert into payroll_details(emp_id,basic_pay,deductions,taxable_pay,tax,net_pay) "
					+ "values(%s,%s,%s,%s,%s,%s)", employeeId, basic_pay, deductions, taxablePay, tax, netPay);
			int rowAffected = statement.executeUpdate(query);
			if (rowAffected == 1) {
				List<String> departmentNameList = new ArrayList<String>();
				departmentNameList.add(departmentName);
				List<LocalDate> startDates = new ArrayList<LocalDate>();
				startDates.add(startDate);
			employee = new EmployeePayRoll(employeeId,name,salary,companyId,departmentNameList,startDates);
			}
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new CustomSQLException(e.getMessage(), CustomSQLException.Exception_Type.ADD_FAILED);
		}
		try {
			connection.commit();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return employee;
	}

	private Connection getConnection() throws CustomSQLException {
		String jdbcURL = "jdbc:mysql://localhost:3306/employee_payroll_service?useSSL=false";
		String userName = "root";
		String password = "Fightclub@8.8";
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(jdbcURL, userName, password);
		} catch (SQLException e) {
			throw new CustomSQLException(e.getMessage(), CustomSQLException.Exception_Type.CONNECTION_FAILED);
		}
		return connection;
	}

	public EmployeePayRoll getEmployee(String name) throws CustomSQLException {
		List<EmployeePayRoll> employeePayRollList = new ArrayList<EmployeePayRoll>();
		employeePayRollList = this.readData();
		return employeePayRollList.stream().filter(e -> e.getName().equals(name)).findFirst().orElse(null);
	}

	public List<EmployeePayRoll> getDatafromResultset(ResultSet result) throws CustomSQLException {

		List<EmployeePayRoll> employeePayRollList = new ArrayList<EmployeePayRoll>();
		try {
			while (result.next()) {
				List<LocalDate> startDates = new ArrayList<LocalDate>();
				List<Integer> department_id = new ArrayList<Integer>();
				List<String> department_name = new ArrayList<String>();
				try (Connection connection2 = this.getConnection();) {
					PreparedStatement preparedStatement2 = connection2
							.prepareStatement("select * from employee_department where emp_id = ?");
					preparedStatement2.setInt(1, result.getInt("id"));
					ResultSet result2 = preparedStatement2.executeQuery();

					while (result2.next()) {
						startDates.add(result2.getDate("start_date").toLocalDate());
						department_id.add(result2.getInt("dept_id"));
					}

					for (int i : department_id) {
						PreparedStatement preparedStatement3 = connection2
								.prepareStatement("select * from department where id = ?");
						preparedStatement3.setInt(1, i);
						ResultSet result3 = preparedStatement3.executeQuery();
						while (result3.next()) {
							department_name.add(result3.getString("name"));
						}
					}
				}
				employeePayRollList.add(new EmployeePayRoll(result.getInt("id"), result.getString("name"),
						result.getDouble("salary"), result.getInt("company_id"), department_name, startDates));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("In function "+employeePayRollList.size());
		return employeePayRollList;
	}

}
