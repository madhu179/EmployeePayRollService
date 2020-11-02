package EmployeePayRollProject;

import org.junit.*;

import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

public class EmployeePayRollTest {

	@Test
	public void given3Employees_StoreToFile_ShouldPassTest() {
		ArrayList<EmployeePayRoll> empPayRoll = new ArrayList<EmployeePayRoll>();
		empPayRoll.add(new EmployeePayRoll(1, "Tony Stark", 1000000));
		empPayRoll.add(new EmployeePayRoll(2, "Bruce Wayne", 500000));
		EmployeePayRollService empPayRollService = new EmployeePayRollService(empPayRoll);
		empPayRollService.writeData("File");
		empPayRollService.printData("File");
		int entries = empPayRollService.noOfEntries("File");
		boolean result = entries == 2 ? true : false;
		Assert.assertTrue(result);
	}

	@Test
	public void readingFromFile_NoOfEntries_ShouldMatchActual() {
		EmployeePayRollService empPayRollService = new EmployeePayRollService();
		int entries;
		try {
			entries = empPayRollService.readData("File");
			boolean result = entries == 2 ? true : false;
			Assert.assertTrue(result);
		} catch (CustomSQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void readingFromDB_NoOfEntries_ShouldMatchActual() {
		EmployeePayRollService empPayRollService = new EmployeePayRollService();
		int entries;
		try {
			entries = empPayRollService.readData("DB");
			boolean result = entries == 7 ? true : false;
			Assert.assertTrue(result);
		} catch (CustomSQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void givenNewSalary_UpdatinginDB_ShouldMatch() {
		EmployeePayRollService empPayRollService = new EmployeePayRollService();
		try {
			int entries = empPayRollService.readData("DB");
			empPayRollService.updateSalary(1, "Natasha", 90000.0);
			boolean result = empPayRollService.checkDBInSyncWithList("Natasha");
			Assert.assertTrue(result);
		} catch (CustomSQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void givenNewSalary_UpdatinginDB_UsingPreparedStatement_ShouldMatch() {
		EmployeePayRollService empPayRollService = new EmployeePayRollService();
		try {
			int entries = empPayRollService.readData("DB");
			empPayRollService.updateSalary(2, "Natasha", 80000.0);
			boolean result = empPayRollService.checkDBInSyncWithList("Natasha");
			Assert.assertTrue(result);
		} catch (CustomSQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void giveName_RetreiveDataFromDB_UsingPreparedStatement_ShouldMatch() {
		EmployeePayRoll employee;
		LocalDate startDate = LocalDate.parse("2020-04-29");
		EmployeePayRollService empPayRollService = new EmployeePayRollService();
		try {
			int entries = empPayRollService.readData("DB");
			employee = empPayRollService.preparedStatementReadData("Natasha");
			boolean result = employee.getStartDate().contains(startDate);
			Assert.assertTrue(result);
		} catch (CustomSQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void giveDateRange_RetreiveDataFromDB_UsingPreparedStatement_ShouldMatch() {
		List<EmployeePayRoll> empPayRoll = new ArrayList<EmployeePayRoll>();
		EmployeePayRollService empPayRollService = new EmployeePayRollService();
		try {
			empPayRoll = empPayRollService.getDataInDateRange("2010-04-29", "2018-04-29");
			boolean result = empPayRoll.size() == 3;
			Assert.assertTrue(result);
		} catch (CustomSQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void findMinMaxSumAvgcount_GroupedByGender_ShouldMatch() {
		EmployeePayRollService empPayRollService = new EmployeePayRollService();
		try {
			HashMap<String, Double> output = empPayRollService.getMinMaxSumAvgCount();
			boolean result = output.get("minMale").equals(60000.0) && output.get("maxMale").equals(200000.0)
					&& output.get("sumMale").equals(660000.0) && output.get("avgMale").equals(110000.0)
					&& output.get("minFemale").equals(90000.0) && output.get("sumFemale").equals(90000.0)
					&& output.get("countMale").equals(6.0) && output.get("countFemale").equals(1.0);
			Assert.assertTrue(result);
		} catch (CustomSQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void givenNewEmployee_WhenAdded_ShouldSyncWithDB() {
		EmployeePayRollService empPayRollService = new EmployeePayRollService();
		try {
			int entries = empPayRollService.readData("DB");
			LocalDate startDate = LocalDate.parse("2008-09-01");
			int companyId = 3;
			String departmentName = "Management";
			empPayRollService.addEmployeeAndPayRoll("Bruce", "M", 100000.0, companyId, Arrays.asList(departmentName),
					Arrays.asList(startDate));
			boolean result = empPayRollService.checkDBInSyncWithList("Bruce");
			Assert.assertTrue(result);
		} catch (CustomSQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void givenNewEmployee_WhenAddedWithPayrollData_ShouldSyncWithDB() {
		EmployeePayRollService empPayRollService = new EmployeePayRollService();
		try {
			int entries = empPayRollService.readData("DB");
			LocalDate startDate = LocalDate.parse("2016-11-04");
			int companyId = 3;
			String departmentName = "Sales";
			empPayRollService.addEmployeeAndPayRoll("Strange", "M", 60000.0, companyId, Arrays.asList(departmentName),
					Arrays.asList(startDate));
			boolean result = empPayRollService.checkDBInSyncWithList("Strange");
			Assert.assertTrue(result);
		} catch (CustomSQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void givenNewEmployee_WhenAddedWithPayrollDataNewERDiagram_ShouldSyncWithDB() {
		EmployeePayRollService empPayRollService = new EmployeePayRollService();
		try {
			int entries = empPayRollService.readData("DB");
			LocalDate startDate = LocalDate.parse("2019-01-29");
			int companyId = 4;
			String departmentName = "Sales";
			empPayRollService.addEmployeeAndPayRoll("Tchalla", "M", 200000.0, companyId, Arrays.asList(departmentName),
					Arrays.asList(startDate));
			boolean result = empPayRollService.checkDBInSyncWithList("Tchalla");
			Assert.assertTrue(result);
		} catch (CustomSQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void givenEmployeeName_ShouldRemoveFromListAndDB() {
		EmployeePayRollService empPayRollService = new EmployeePayRollService();
		List<EmployeePayRoll> empPayRoll = new ArrayList<EmployeePayRoll>();
		try {
			int entries = empPayRollService.readData("DB");
			System.out.println("before " + entries);
			empPayRollService.deleteEmployee("Strange");
			int entrie = empPayRollService.readData("DB");
			System.out.println("after " + entrie);
			boolean result = empPayRollService.checkIFDeletedFromList("Strange");
			Assert.assertFalse(result);
		} catch (CustomSQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void givenMultipleEmployee_WhenAddedToDB_ShouldMatchCountOfEntries() {
		EmployeePayRollService empPayRollService = new EmployeePayRollService();
		try {
			int entries = empPayRollService.readData("DB");
			String departmentName = "Management";
			EmployeePayRoll[] employeeArray = {
					new EmployeePayRoll(0, "Tony", "M", 130000.0, 3, Arrays.asList(departmentName),
							Arrays.asList(LocalDate.parse("2008-05-01"))),
					new EmployeePayRoll(0, "Steve", "M", 100000.0, 3, Arrays.asList(departmentName),
							Arrays.asList(LocalDate.parse("2011-07-29"))),
					new EmployeePayRoll(0, "Peter", "M", 70000.0, 4, Arrays.asList(departmentName),
							Arrays.asList(LocalDate.parse("2017-07-07"))),
					new EmployeePayRoll(0, "Natasha", "F", 60000.0, 3, Arrays.asList(departmentName),
							Arrays.asList(LocalDate.parse("2020-04-29"))) };
			Instant start = Instant.now();
			int countOfEntries = empPayRollService.addEmployeeAndPayRoll(Arrays.asList(employeeArray),"DB");
			Instant end = Instant.now();
			System.out.println("Duration without Thread : " + Duration.between(start, end));
			boolean result = countOfEntries == 7 ? true : false;
			Assert.assertTrue(result);
		} catch (CustomSQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void givenMultipleEmployeeWithThreads_WhenAddedToDB_ShouldMatchCountOfEntries() {
		EmployeePayRollService empPayRollService = new EmployeePayRollService();
		try {
			int entries = empPayRollService.readData("DB");
			System.out.println("Entries" + entries);
			String departmentName = "Management";
			EmployeePayRoll[] employeeArray = {
					new EmployeePayRoll(0, "Tony", "M", 130000.0, 3, Arrays.asList(departmentName),
							Arrays.asList(LocalDate.parse("2008-05-01"))),
					new EmployeePayRoll(0, "Steve", "M", 100000.0, 3, Arrays.asList(departmentName),
							Arrays.asList(LocalDate.parse("2011-07-29"))),
					new EmployeePayRoll(0, "Peter", "M", 70000.0, 4, Arrays.asList(departmentName),
							Arrays.asList(LocalDate.parse("2017-07-07"))),
					new EmployeePayRoll(0, "Natasha", "F", 60000.0, 3, Arrays.asList(departmentName),
							Arrays.asList(LocalDate.parse("2020-04-29"))) };
			Instant start = Instant.now();
			empPayRollService.addEmployeeAndPayRoll(Arrays.asList(employeeArray),"DB");
			Instant end = Instant.now();
			System.out.println("Duration without Thread : " + Duration.between(start, end));
			Instant startThread = Instant.now();
			int countOfEntries = empPayRollService.addEmployeeAndPayRollWithThread(Arrays.asList(employeeArray));
			Instant endThread = Instant.now();
			System.out.println("Duration with Thread : " + Duration.between(startThread, endThread));			
			boolean result = countOfEntries == 11 ? true : false;
			Assert.assertTrue(result);
		} catch (CustomSQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void givenUpdatedSalaries_UpdatingMultipleEmployeesinDB_UsingPreparedStatement_ShouldMatch() {
		EmployeePayRollService empPayRollService = new EmployeePayRollService();
		try {
			int entries = empPayRollService.readData("DB");
			HashMap<String,Double> salaryMap = new HashMap<String,Double>();
			salaryMap.put("Tony",110000.0);
			salaryMap.put("Steve",70000.0);
			salaryMap.put("Peter",90000.0);
			salaryMap.put("Natasha",40000.0);		
			empPayRollService.updateMultipleSalary(salaryMap);
			boolean result = empPayRollService.checkDBInSyncWithList("Natasha");
			Assert.assertTrue(result);
		} catch (CustomSQLException e) {
			e.printStackTrace();
		}
	}
	
	@Before
	public void setup()
	{
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
	}
	
	public Response addEmployeeToJsonServer(EmployeePayRoll employeePayroll)
	{
		String jsonString = new Gson().toJson(employeePayroll);
		RequestSpecification request = RestAssured.given();	
		request.header("Content-Type","application/json");
		request.body(jsonString);
		return request.post("/employee_payroll");
	}
	
	public EmployeePayRoll[] getEmployeeList()
	{
		Response response = RestAssured.get("/employee_payroll/");
		System.out.println(response.asString());
		EmployeePayRoll[] employees = new Gson().fromJson(response.asString(), EmployeePayRoll[].class);	
		return employees;
	}
	
	@Test
	public void employeeWhenAdded_ShouldMatchCount()
	{
		EmployeePayRollService empPayRollService = new EmployeePayRollService();
		List<EmployeePayRoll> employeeList = new ArrayList<EmployeePayRoll>();
		
		EmployeePayRoll employee = new EmployeePayRoll(0,"Tony", "M",130000.0,3,Arrays.asList("Management"),
				Arrays.asList(LocalDate.parse("2008-05-01")));
		Response response = addEmployeeToJsonServer(employee);
		int statusCode = response.getStatusCode();
		int countOfEntries=0;
		if(statusCode==201)
		{
			employee = new Gson().fromJson(response.asString(), EmployeePayRoll.class);
			employeeList.add(employee);
			countOfEntries = empPayRollService.addEmployeeAndPayRoll(employeeList,"REST_IO");
		}
		Assert.assertEquals(201,response.getStatusCode());
		Assert.assertEquals(1, countOfEntries);
	}
	
	@Test
	public void givenmultipleEmployee_WhenAdded_ShouldMatchCount(){
		EmployeePayRollService empPayRollService = new EmployeePayRollService();
		List<EmployeePayRoll> employeeList = new ArrayList<EmployeePayRoll>();
		int countOfEntries=0;
		EmployeePayRoll[] employees =  {
				new EmployeePayRoll(0, "Steve", "M", 100000.0, 3, Arrays.asList("Management"),
						Arrays.asList(LocalDate.parse("2011-07-29"))),
				new EmployeePayRoll(0, "Peter", "M", 70000.0, 4, Arrays.asList("Management"),
						Arrays.asList(LocalDate.parse("2017-07-07"))),
				new EmployeePayRoll(0, "Natasha", "F", 60000.0, 3, Arrays.asList("Management"),
						Arrays.asList(LocalDate.parse("2020-04-29"))) };
		for(EmployeePayRoll employee : employees)
		{
		Response response = addEmployeeToJsonServer(employee);
		int statusCode = response.getStatusCode();
		if(statusCode==201)
		{
			employee = new Gson().fromJson(response.asString(), EmployeePayRoll.class);
			employeeList.add(employee);
		}
		Assert.assertEquals(201,response.getStatusCode());
		}
		countOfEntries = empPayRollService.addEmployeeAndPayRoll(employeeList,"REST_IO");
		Assert.assertEquals(3, countOfEntries);
	}
	
	@Test
	public void givenNewSalary_WhenUpdated_ShouldReturnSucessCode(){
		EmployeePayRoll employeePayRoll = new EmployeePayRoll(4, "Natasha", "F", 80000.0, 3, Arrays.asList("Management"),
				Arrays.asList(LocalDate.parse("2020-04-29")));
		String jsonString = new Gson().toJson(employeePayRoll);
		RequestSpecification request = RestAssured.given();	
		request.header("Content-Type","application/json");
		request.body(jsonString);
		Response  response = request.put("/employee_payroll/"+employeePayRoll.id);
		int statusCode = response.getStatusCode();
		Assert.assertEquals(200, statusCode);
	}
	
	@Test
	public void retreiveEmployees_FromJsonServer_ShouldMatchCount(){
		EmployeePayRoll[] employees = getEmployeeList();
		EmployeePayRollService empPayRollService = new EmployeePayRollService(Arrays.asList(employees));
		int CountOfEntries = empPayRollService.noOfEntries("REST_IO");
		Assert.assertEquals(4, CountOfEntries);
	}

}
