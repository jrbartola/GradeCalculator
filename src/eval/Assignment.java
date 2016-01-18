package eval;

public class Assignment {
	private String name;
	private double val;
	private double weight;
	
	public Assignment(String name, double value, double weight) {
		this.name = name;
		this.val = value;
		this.weight = weight;
	}
	
	public double getWeight() {
		return this.weight;
	}
	
	public void setWeight(double newWeight) {
		this.weight = newWeight;
	}
	
	public double getVal() {
		return this.val;
	}
	
	public void setVal(double newVal) {
		this.val = newVal;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String newName) {
		this.name = newName;
	}
}
