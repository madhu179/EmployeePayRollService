package EmployeePayRollProject;

import java.time.LocalDate;
import java.util.List;

public class EmployeePayRoll {

	private int id;
	private String name;
	private double salary;
	private int companyId;
	private List<String> departmentName;
	private List<LocalDate> startDate;

	public EmployeePayRoll(int id, String name, double salary) {
		this.id = id;
		this.name = name;
		this.salary = salary;
	}
	
	public EmployeePayRoll(int id, String name, double salary, int companyId, List<String> departmentName,
			List<LocalDate> startDate) {
		this(id,name,salary);
		this.companyId = companyId;
		this.departmentName = departmentName;
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

	public String toString() {
		return "id=" + id + ", name=" + name + ", salary=" + salary;
	}
	
	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public List<String> getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(List<String> departmentName) {
		this.departmentName = departmentName;
	}

	public List<LocalDate> getStartDate() {
		return startDate;
	}

	public void setStartDate(List<LocalDate> startDate) {
		this.startDate = startDate;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmployeePayRoll other = (EmployeePayRoll) obj;
		if (companyId != other.companyId)
			return false;
		if (departmentName == null) {
			if (other.departmentName != null)
				return false;
		} else if (!departmentName.equals(other.departmentName))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (Double.doubleToLongBits(salary) != Double.doubleToLongBits(other.salary))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}
	
}
