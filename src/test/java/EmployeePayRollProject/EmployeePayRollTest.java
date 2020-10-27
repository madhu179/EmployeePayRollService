package EmployeePayRollProject;

import org.junit.*;

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
		int entries = empPayRollService.readData("File");
		boolean result = entries == 2 ? true : false;
		Assert.assertTrue(result);
	}

	@Test
	public void readingFromDB_NoOfEntries_ShouldMatchActual() {
		EmployeePayRollService empPayRollService = new EmployeePayRollService();
		int entries = empPayRollService.readData("DB");
		boolean result = entries == 4 ? true : false;
		Assert.assertTrue(result);
	}

	@Test
	public void givenNewSalary_UpdatinginDB_ShouldMatch() {
		EmployeePayRollService empPayRollService = new EmployeePayRollService();
		int entries = empPayRollService.readData("DB");
		empPayRollService.updateSalary(1, "Natasha", 90000.0);
		boolean result = empPayRollService.checkDBInSyncWithList("Natasha");
		Assert.assertTrue(result);
	}

	@Test
	public void givenNewSalary_UpdatinginDB_UsingPreparedStatement_ShouldMatch() {
		EmployeePayRollService empPayRollService = new EmployeePayRollService();
		int entries = empPayRollService.readData("DB");
		empPayRollService.updateSalary(2, "Natasha", 80000.0);
		boolean result = empPayRollService.checkDBInSyncWithList("Natasha");
		Assert.assertTrue(result);
	}
	
	@Test
	public void giveName_RetreiveDataFromDB_UsingPreparedStatement_ShouldMatch() {
		EmployeePayRoll employee;
		LocalDate startDate = LocalDate.parse("2020-04-29");
		EmployeePayRollService empPayRollService = new EmployeePayRollService();
		int entries = empPayRollService.readData("DB");	
		employee = empPayRollService.preparedStatementReadData("Natasha");
		boolean result = employee.getStartDate().equals(startDate);
		Assert.assertTrue(result);
	}

}
