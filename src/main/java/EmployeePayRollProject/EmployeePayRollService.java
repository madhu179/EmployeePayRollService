package EmployeePayRollProject;

import java.time.LocalDate;
import java.util.*;

public class EmployeePayRollService {
	private static List<EmployeePayRoll> empPayRollList;
	private static EmployeePayRollDBService employeePayRollDBService;
	private static Scanner sc = new Scanner(System.in);

	public EmployeePayRollService(List<EmployeePayRoll> empPayRollList) {
		this();
		this.empPayRollList = empPayRollList;
	}

	public EmployeePayRollService() {
		employeePayRollDBService = EmployeePayRollDBService.getInstance();
	}

	public static void main(String[] args) {
		ArrayList<EmployeePayRoll> empPayRollList = new ArrayList<EmployeePayRoll>();
		EmployeePayRollService empPayRollService = new EmployeePayRollService(empPayRollList);

		empPayRollService.readData();

		empPayRollService.writeData("Console");

	}

	public static void readData() {
		System.out.println("Please Enter the following details :");
		System.out.println("Enter ID");
		int id = sc.nextInt();
		sc.nextLine();
		System.out.println("Enter Name :");
		String name = sc.nextLine();
		System.out.println("Enter Salary :");
		double salary = sc.nextDouble();

		EmployeePayRoll empPayRollObject = new EmployeePayRoll(id, name, salary);
		empPayRollList.add(empPayRollObject);
	}

	public int readData(String source) {
		if (source.equals("File")) {
			empPayRollList = new EmployeePayRollFileService().readData();
			return empPayRollList.size();
		} else if (source.equals("DB")) {
			empPayRollList = employeePayRollDBService.readData();
			return empPayRollList.size();
		}
		return 0;
	}

	public void updateSalary(int n, String name, Double salary) {
		int success = employeePayRollDBService.updateSalary(n, name, salary);
		if (success == 1) {
			for (EmployeePayRoll e : empPayRollList) {
				if (e.getName().equals(name)) {
					e.setSalary(salary);
				}
			}
		}
	}

	public void writeData(String destination) {
		if (destination.equals("Console"))
			System.out.println("Employee Pay Roll Data : \n" + empPayRollList.get(0).toString());
		else if (destination.equals("File"))
			new EmployeePayRollFileService().writeData(empPayRollList);
	}

	public int noOfEntries(String destination) {
		if (destination.equals("Console"))
			return empPayRollList.size();
		else if (destination.equals("File"))
			return new EmployeePayRollFileService().noOfEntries();
		return 0;
	}

	public void printData(String destination) {
		if (destination.equals("Console"))
			for (EmployeePayRoll e : empPayRollList) {
				System.out.println(e.toString() + "\n");
			}
		else if (destination.equals("File"))
			new EmployeePayRollFileService().printData();

	}

	public boolean checkDBInSyncWithList(String name) {
		EmployeePayRoll employeeDB = employeePayRollDBService.getEmployee(name);
		EmployeePayRoll employeeList = getEmployee(name);
		return employeeDB.equals(employeeList);
	}

	private EmployeePayRoll getEmployee(String name) {
		return empPayRollList.stream().filter(e -> e.getName().equals(name)).findFirst().orElse(null);
	}

	public EmployeePayRoll preparedStatementReadData(String name) {
		return employeePayRollDBService.preparedStatementReadData(name);
	}

	public List<EmployeePayRoll> getDataInDateRange(String startDate, String endDate) {
		return employeePayRollDBService.getDataInDateRange(startDate,endDate);
	}

}
