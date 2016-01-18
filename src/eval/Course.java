package eval;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Course {
	
	private String name;
	private Map<Double, String> gradeScale;
	private List<Assignment> grades;
	
	public Course(String name) {
		this.name = name;
		this.gradeScale = new HashMap<Double, String>();
		this.grades = new ArrayList<Assignment>();
	}
	
	public Course(String name, Map gradeScale) {
		this(name);
		this.gradeScale = gradeScale;
		
	}
	
	public void setGradeScale(Map gradeScale) {
		this.gradeScale = gradeScale;
	}
	
	public void addAssignment(Assignment a) {
		grades.add(a);
	}
	
	public String removeAssignment(Assignment a) {
		String removed = a.getName();
		grades.remove(a);
		return removed;
	}
	
	public double getAverage() {
		if (grades.isEmpty())
			return 0;
		
		double total = 0;
		
		for (Assignment cur : grades) {
			total += ((cur.getWeight()/100) * cur.getVal());
		}
		
		//total /= grades.size();
		return total;
	}
	
	public String getName() {
		return this.name;
	}
	
	public List<Assignment> getAssignments() {
		return this.grades;
	}
	
	public void removeAssignments() {
		grades = new ArrayList<Assignment>();
	}
	
	public String getLetterGrade() {
		Double avg = getAverage();
		// create an array list and sort it so that the order is preserved from CSV
		List<Double> keys = new ArrayList<Double>();
		for (Double d : gradeScale.keySet()) {
			keys.add(d);
		}
		
		// sort ascending, then reverse so the list starts with the highest grade
		Collections.sort(keys);
		Collections.reverse(keys);
		
		
		for (Double d : keys) {
			
			String curr = gradeScale.get(d);
			System.out.println(curr);
			if (avg.compareTo(d) > 0) {
				return avg + "%: " + curr;
			}
		}
		
		return (Math.round(avg * 100.0) / 100) + "%: F";
	}
	
	
}
