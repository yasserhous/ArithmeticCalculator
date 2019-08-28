
public class Operator {
	private int priority;
	private String operator;
	
	public Operator(int priority,String operator) {
		this.priority=priority;
		this.operator=operator;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}
	

}
