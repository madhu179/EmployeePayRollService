package EmployeePayRollProject;

import java.time.LocalDate;

public class EmployeePayRoll {
	private int id;
	private String name;
	private String gender;
	private double salary;
	private LocalDate startDate;

	public EmployeePayRoll(int id, String name, double salary) {
		this.id = id;
		this.name = name;
		this.salary = salary;
	}
	
	public EmployeePayRoll(int id, String name, String gender, double salary, LocalDate startDate) {
		this(id,name,salary);
		this.gender = gender;
		this.startDate = startDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}
	
	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public String toString() {
		return "id=" + id + ", name=" + name + ", salary=" + salary;
	}
}
