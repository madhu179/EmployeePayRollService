package EmployeePayRollProject;

import java.util.*;
import java.util.stream.Stream;
import java.io.*;
import java.nio.file.*;

public class EmployeePayRollFileService {

	public String DATA_FILE = "PayRollData.txt";

	EmployeePayRollFileService() {

	}

	public void writeData(List<EmployeePayRoll> empPayRollList) {
		StringBuffer data = new StringBuffer();
		for (EmployeePayRoll e : empPayRollList) {
			data.append(e.toString()+"\n");
		}
		Path filePath = Paths.get(DATA_FILE);
		try {
			Files.write(filePath, data.toString().getBytes());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public int noOfEntries() {
		int count = 0;
		Path filePath = Paths.get(DATA_FILE);
		try {
			count = (int) Files.lines(filePath).count();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return count;
	}

	public void printData() {
		Path filePath = Paths.get(DATA_FILE);
		try {
			Stream<String> stringStream= Files.lines(filePath);
			stringStream.forEach(System.out::println);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
