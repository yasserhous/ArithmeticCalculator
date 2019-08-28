/* AUTHOR: MOHAMED-YASSER HOUSSEIN
 * PURPOSE: DEVELOPED AS AN ASSIGNMENT FOR A DATA STRUCTURES AND ALGORITHMS COURSE
 * DESCRIPTION: THIS CALCULATOR READS ARITHMETIC EXPRESSIONS FROM A FILE AND OUTPUTS THE RESULT
 * BACK IN THE FILE. IN THE TERMINAL, A STEP BY STEP PROCESS IS PRINTED.
 */
import java.util.Scanner;
import java.io.*;
import java.util.ArrayList;
public class ArithmeticCalculator {
	
	private Stack numericStack;
	private Stack operatorStack;
	private ArrayList<String> expression;
	private Operator[] operatorset;
	
	public ArithmeticCalculator() {
		operatorset= new Operator[23];
		operatorset[0]=new Operator(1,"!");
		operatorset[1]=new Operator(2,"^");
		operatorset[2]=new Operator(2,"^-");
		operatorset[3]=new Operator(3,"*");
		operatorset[4]=new Operator(3,"/");
		operatorset[5]=new Operator(3,"*-");
		operatorset[6]=new Operator(3,"/-");
		operatorset[7]=new Operator(4,"+");
		operatorset[8]=new Operator(4,"-");
		operatorset[9]=new Operator(4,"--");
		operatorset[10]=new Operator(4,"+-");
		operatorset[11]= new Operator(5,">");
		operatorset[12]= new Operator(5,"<");
		operatorset[13]= new Operator(5,"<=");
		operatorset[14]= new Operator(5,">=");
		operatorset[15]= new Operator(5,"<=-");
		operatorset[16]= new Operator(5,">=-");
		operatorset[17]= new Operator(5,"<-");
		operatorset[18]= new Operator(5,">-");
		operatorset[19]= new Operator(6,"==");
		operatorset[20]= new Operator(6,"!=");
		operatorset[21]= new Operator(6,"==-");
		operatorset[22]= new Operator(6,"!=-");
		
		numericStack = new Stack();
		operatorStack = new Stack();
		expression = new ArrayList<>();
	}
	public void compute(String fileName) throws IOException,InvalidCharacterException {
		Scanner readFromFile = null;
		ArrayList<String> results = new ArrayList<>();
		try {
			readFromFile = new Scanner(new FileInputStream(fileName));
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		while(readFromFile.hasNextLine()) {
			String temp = readFromFile.nextLine();
			temp = temp.replaceAll("\\s+","");
			System.out.println(temp);
			expression.add(0,temp);
		}
			for(int i=0;i<expression.size();i++) {
				//System.out.println(expression.get(i));
				calculate(expression.get(i),numericStack,operatorStack,null);
				String result =( expression.get(i)+" = " + numericStack.pop() );
				results.add(result);
			}
			readFromFile.close();
			PrintWriter writer = new PrintWriter(new FileWriter(fileName));
			
			for(int i=0;i<expression.size();i++) {
				writer.println(expression.get(expression.size()-i-1));
				writer.println(results.get(results.size()-i-1));
				writer.println();
				
			}
			writer.flush();
			writer.close();
			//printToFile(toPrint,fileName);
		
		
		
	}
	private int calculate(String subExpression,Stack currentNumStack,Stack currentOpStack,Stack previousNumStack) {
		System.out.println(subExpression);
		
		int[] subPointer = {0};
		while(subPointer[0]<subExpression.length()) {
			//special case: expression starts with a negative value ex: -3 + 5
			if((subExpression.charAt(subPointer[0])=='-') && subPointer[0]==0) {
				String negativeValue="-";
				subPointer[0]++;
				String numericString = buildStringFromExpression(subExpression,"numeric",subPointer);
				negativeValue+=numericString;
				currentNumStack.push(negativeValue);
			}
			
			else if(subExpression.charAt(subPointer[0])==')') {
				String result = fullStackComputation(currentNumStack,currentOpStack);
				if(previousNumStack==null) {
					numericStack.push(result);
					System.out.println("im here");
				}
				else
					previousNumStack.push(result);
				subPointer[0]++;
				return subPointer[0];
			}
			else if(subExpression.charAt(subPointer[0])=='(') {
				Stack tempNumStack= new Stack();
				Stack tempOpStack= new Stack();
				String newSubExpression = subExpression.substring(subPointer[0]+1, subExpression.length());
				subPointer[0]++;
				subPointer[0]+=calculate(newSubExpression,tempNumStack,tempOpStack,currentNumStack);//recursive call
				
			}
			else if(Character.isDigit(subExpression.charAt(subPointer[0]))) {
				
				String numericString = buildStringFromExpression(subExpression,"numeric",subPointer);
				currentNumStack.push(numericString);
			}
			else if(isFactorial(subExpression.charAt(subPointer[0]),subPointer[0],subExpression)) {
				int operand =0;
				boolean isDouble=false;
				for(char charFromExpression:currentNumStack.peek().toCharArray()) {
					if(!Character.isDigit(charFromExpression)) {
						isDouble =true;
					}
				}
				if(isDouble) {
					String temp = currentNumStack.peek().substring(0,currentNumStack.pop().indexOf('.'));
					operand=Integer.parseInt(temp);
				}
				else {
					operand = Integer.parseInt(currentNumStack.pop());
				}
				
				long result=computeFactorial(operand);
				currentNumStack.push(String.valueOf(result));
				System.out.println("*****" + currentNumStack.peek() );
				
				subPointer[0]++;
				
			}
			else {
				String operatorString=buildStringFromExpression(subExpression,"operator",subPointer);
				int compareResult=comparePrecedenceWithStack(operatorString,currentOpStack);
				while(compareResult>=0) {
				/*test*/ System.out.println("*test*" + "compareResult = " + compareResult);
				if(compareResult>=0)//comparing same precedence or higher precedence on stack than on expression
					performSingleOperationOnStack(currentNumStack,currentOpStack);
				compareResult=comparePrecedenceWithStack(operatorString,currentOpStack);
				}
				currentOpStack.push(operatorString);
			}
			
		}
		if(!currentNumStack.empty()) {
		String endResult = fullStackComputation(currentNumStack,currentOpStack);
		System.out.println("end result is = "+ endResult);
		}
		return subPointer[0]+1; 
	}
	private String fullStackComputation(Stack numStack,Stack opStack) {
		while(numStack.size()>1 && !opStack.empty()) { 
			performSingleOperationOnStack(numStack,opStack);
		}
		return numStack.peek();
	}
	private int comparePrecedenceWithStack(String expressionOperator,Stack operatorStack) {
		if(operatorStack.peek()==null) {return -2;}//integer is trivial. it can return any integer < -1
		int indexOfStackOperatorInOperatorSet = priority(operatorStack.peek());
		int indexOfExpressioOperatorInOperatorSet = priority(expressionOperator);
		
		return(indexOfExpressioOperatorInOperatorSet -indexOfStackOperatorInOperatorSet);
	}
	private int priority(String targetOperator) {
		int priority=-1;
		for(int i=0;i<operatorset.length;i++) {
			if(targetOperator.equals(operatorset[i].getOperator())) {
				priority= operatorset[i].getPriority();
			}
		}
		return priority;
	}
	private void performSingleOperationOnStack(Stack numericStack, Stack operatorStack) {
		
		double operand2=Double.parseDouble(numericStack.pop());
		double operand1=Double.parseDouble(numericStack.pop());
		String operator= operatorStack.pop();
		double result = performOperation(operand1,operand2,operator);
		String numericString= String.valueOf(result);
		numericStack.push(numericString);
		/*test*/ System.out.println("numeric stack  is now: "+ numericStack.peek());
	}
	private String buildStringFromExpression(String expression,String command,int[] subPointer) {
		int startingIndex=subPointer[0];
		String expressionSubString="";
		if(command.equals("numeric")) {
		while( ( startingIndex < expression.length() ) && Character.isDigit(expression.charAt(startingIndex))) {
				expressionSubString+=expression.charAt(startingIndex);
				startingIndex++;
			}
		}
		else if(command.equals("operator")) {
			while(( startingIndex < expression.length() )&& !Character.isDigit(expression.charAt(startingIndex)) && expression.charAt(startingIndex)!='(' && expression.charAt(startingIndex)!=')' ) {
				expressionSubString+=expression.charAt(startingIndex);
				startingIndex++;
			}
		}
		subPointer[0]=startingIndex;
		return expressionSubString;
		
	}
	private boolean isFactorial(char operator,int index,String subExpression) {
		index++;
		if(index == subExpression.length()) {
			return true;
		}
		else{
			return ( operator == '!' && subExpression.charAt(index)!='=');
		}
	}
	public double performOperation(double operand1,double operand2,String operator) throws ArithmeticException {
		/*test*/ System.out.println("*test*" + "performing operation: " + operand1 + operator + operand2);
		double result=0.0;
		//division by zero exception
		if(operand2 ==0 && operator.equals("/")){ throw new ArithmeticException();}
		switch(operator) {
		case "+": result = operand1+operand2;
		break;
		case "-": result = operand1-operand2;
		break;
		case "*": result = operand1*operand2;
		break;
		case "/": result = operand1/operand2;
		break;
		case "^": result = Math.pow(operand1,operand2);
		break;
		case"--": result=operand1+operand2;
		break;
		case"+-": result=operand1-operand2;
		break;
		case"*-": result=operand1*(-1*operand2);
		break;
		case"/-": result=operand1/(-1*operand2);
		break;
		case"^-": result=1/(Math.pow(operand1,operand2));
		break;
		case"<": result=(operand1 < operand2)? 1.0:0.0;
		break;
		case"<=": result=(operand1 <= operand2)? 1.0:0.0;
		break;
		case">": result=(operand1 > operand2)? 1.0:0.0;
		break;
		case">=": result=(operand1 >= operand2)? 1.0:0.0;
		break;
		case"<-": result=(operand1 < -1*operand2)? 1.0:0.0;
		break;
		case"<=-": result=(operand1 <= -1*operand2)? 1.0:0.0;
		break;
		case">-": result=(operand1 > -1*operand2)? 1.0:0.0;
		break;
		case">=-": result=(operand1 >= -1*operand2)? 1.0:0.0;
		break;
		case"==": result=(operand1 == operand2)? 1.0:0.0;
		break;
		case"!=": result=(operand1 != operand2)? 1.0:0.0;
		break;
		case"==-": result=(operand1 == -1*operand2)? 1.0:0.0;
		break;
		case"!=-": result=(operand1 !=  -1*operand2)? 1.0:0.0;
		break;
		default: System.out.println("operator not found");
		}
		return result;
	}
	private long computeFactorial(int operand) {
		if(operand<=1)
			return 1;
		else
			return operand * computeFactorial(--operand);
	}
	public static void main(String[] args) throws IOException,InvalidCharacterException,ArithmeticException {
		ArithmeticCalculator calculator= new ArithmeticCalculator();
		calculator.compute(args[0]);
		
	}
	
}


