package EmployeePayRollProject;

import org.junit.*;
import java.util.*;

public class EmployeePayRollTest {
	
	@Test
	public void given3EmployeesStoreToFileShouldPassTest()
	{
		ArrayList<EmployeePayRoll> empPayRoll = new ArrayList<EmployeePayRoll>();
		empPayRoll.add(new EmployeePayRoll(1,"Tony Stark",1000000));
		empPayRoll.add(new EmployeePayRoll(2,"Bruce Wayne",500000));
		EmployeePayRollService empPayRollService = new EmployeePayRollService(empPayRoll);
		empPayRollService.writeData("File");
		empPayRollService.printData("File");
		int entries = empPayRollService.noOfEntries("File");
		boolean result = entries==2 ? true : false;
		Assert.assertTrue(result);
	}

}
