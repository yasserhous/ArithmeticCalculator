
/**
 * 
 * @author MOHAMED-YASSER HOUSSEIN
 * COMP 352 ASSIGNMENT 2
 * IMPORTANT NOTE: THE CODE WAS NOT COMPLETED. THIS CODE IS SIMPLY USING AN ARRAY BASED STACK AND SELECTING PRECEDENCE OVER BASIC
 * OPERATORS. SPECIAL CASES OR PARENTHESIS WILL NOT WORK.
 *
 */

public class Stack {
	private  String[] arrayStack;
	private int pointer;
	int size=0;
	int capacity=20;
	
	
	public Stack() {
		arrayStack = new String[capacity];
		pointer=-1;
		
	}
	
	public void push(String c) {
		//doubly strategy
		if(size==capacity)
			doubleSize();
		arrayStack[pointer+1]=c;
		pointer++;
		size++;
	}
	public String pop() {
		String temp=arrayStack[pointer];
		arrayStack[pointer]=null;;
		pointer-=1;
		size--;
		return temp;
	}
	public String peek() {
		if(empty())
			return null;
		return arrayStack[pointer];
	}
	public boolean empty() {
		if(arrayStack[0]==null) {return true;}
		return false;
	}
	public int size() {
		return size;
	}
	private  void doubleSize() {
		String[] temp= new String[capacity];
		for(int i=0;i<capacity;i++)
			temp[i]=this.arrayStack[i];
		this.arrayStack=new String[capacity*=2];
		for(int i=0;i<temp.length;i++)
			this.arrayStack[i]=temp[i];
	}

}

