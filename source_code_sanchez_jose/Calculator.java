/*
Author: Jose Luis Sanchez
Date Modified: 11/4/2021
Purpose: Calculator that does basic arthmetic/trigonometric/logarithmic operations using the Shunting Yard Algorithm
*/

// Importing Libraries
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class Calculator {

    // This section will compute the shunting yard algorithm
    // This algorithm was created by Andrei Ciobanu
    // His website is https://www.andreinc.net/2010/10/05/converting-infix-to-rpn-shunting-yard-algorithm
    // Some modifications were made to accommodate this assignment
    public enum Associativity {LEFT, RIGHT}

    public enum Operator implements Comparable<Operator> {
        // List the operators with their symbol, associativity, and their precedence level
        ADDITION("+", Associativity.LEFT, 0),
        SUBTRACTION("-", Associativity.LEFT, 0),
        DIVISION("/", Associativity.LEFT, 5),
        MULTIPLICATION("*", Associativity.LEFT, 5),
        POWER("^", Associativity.RIGHT, 10),
        SQUAREROOT("sqrt", Associativity.RIGHT, 10),
        SINE("sin", Associativity.RIGHT, 10),
        COSINE("cos", Associativity.RIGHT, 10),
        TANGENT("tan", Associativity.RIGHT, 10),
        COTANGENT("cot", Associativity.RIGHT, 10),
        ARCSINE("arcsin", Associativity.RIGHT, 10),
        ARCCOSINE("arccos", Associativity.RIGHT, 10),
        ARCTANGENT("arctan", Associativity.RIGHT, 10),
        ARCCOTANGENT("arcctg", Associativity.RIGHT, 10),
        LOGN("ln", Associativity.RIGHT, 10),
        LOG10("log", Associativity.RIGHT, 10);

        // Create an instance of each attribute of the operator
        final Associativity associativity;
        final int precedence;
        final String symbol;

        // Create a operator constructor to fill in the operator's attributes
        Operator(String symbol, Associativity associativity, int precedence) {
            this.symbol = symbol;
            this.associativity = associativity;
            this.precedence = precedence;
        }

        // Function to compare the precedence level of two difference operators
        public int comparePrecedence(Operator operator) {
            return this.precedence - operator.precedence;
        }
    }

    // Create a Hash Map that stores a string as the key and a operator as its value 
    final static Map<String, Operator> opList = new HashMap<>();

    // Fill OPS with keys (operator symbol) and values (operator attributes)
    static {
        for(Operator operator : Operator.values()) {
            opList.put(operator.symbol, operator);
        }
    }

    // shuntingYard algorithm takes a list of infixed tokens and rearranges them to rpn
    public static List<String> shuntingYard(List<String> tokens) {
        try {
            // Create a list to store the output buffer
            List<String> output = new LinkedList<>();

            // Create a stack to hold operators
            Stack<String> stack = new Stack<>();

            // Loop throught the expressions one token/number/operator at a time
            for(String token : tokens) {
                // If OPS contains a token, then go into the loop
                if(opList.containsKey(token)) {
                    // While the operator stack is not empty AND the OPS map contains a key, continue loop
                    while(!stack.isEmpty() && opList.containsKey(stack.peek())) {
                        // Create an instance of Operator
                        Operator currOp = opList.get(token); // Gets the current operator
                        Operator topOp = opList.get(stack.peek()); // Gets the top operator in the opsList
                        
                        // If the current operator's associativity is left AND the current operator's precedence is less than or equal to the top operator in the opsList
                        // OR the current operator's associativity is right AND the current operator's precedence is less than the top operator in the opsList
                        if((currOp.associativity == Associativity.LEFT && currOp.comparePrecedence(topOp) <= 0) || 
                            (currOp.associativity == Associativity.RIGHT && currOp.comparePrecedence(topOp) < 0)) {
                                // Pop the operator stack and add it into the output
                                output.add(stack.pop());
                                continue;
                            }
                            // Exit while loop
                            break;
                    }

                    // Push the token into the operator stack
                    stack.push(token);
                } 

                // Check for left parenthesis
                else if("(".equals(token)) {
                    // Add the left parenthesis into the operator stack
                    stack.push(token);
                }

                // Check for right parenthesis
                else if(")".equals(token)) {
                    // Loop while the operator stack isn't empty
                    // AND the top element in the operator stack doesn't equal a left parenthesis
                    while(!stack.isEmpty() && !stack.peek().equals("(")) {
                        // Pop the operator stack and add it into the output 
                        output.add(stack.pop());
                    }

                    // Pop the operator stack
                    stack.pop();
                }

                // Check for left braces
                else if("{".equals(token)) {
                    stack.push(token);
                }

                // Check for right braces
                else if("}".equals(token)) {
                    while(!stack.isEmpty() && !stack.peek().equals("{")) {
                        output.add(stack.pop());
                    }
                    stack.pop();
                }

                // Check for left brackets
                else if("[".equals(token)) {
                    stack.push(token);
                }

                // Check for right brackets
                else if("]".equals(token)) {
                    while(!stack.isEmpty() && !stack.peek().equals("[")) {
                        output.add(stack.pop());
                    }
                    stack.pop();
                }

                // If the token is not an operator, then it is a number
                else {
                    // Add number into output
                    output.add(token);
                }
            }

            // Loop while the operator stack isn't empty
            while(!stack.isEmpty()) {
                // Pop the operator stack and add it into the output
                output.add(stack.pop());
            }

            // Return converted RPN expression
            return output;
        }

        // Catch any NullPointerExceptions and return an error
        catch (NullPointerException e) {
            System.out.println("THERE WAS AN ERROR CONVERTING INFIX TO RPN. RETURNING NULL.");
        }
        return null;
    }

    // confirmExpression checks for several issues with rpn expression given
    public static void confirmExpression(List<String> exp) {
        // Variables to test
        List<String> ops = Arrays.asList("(", ")", "[", "]", "{", "}");
        try {
            // Checking for any parenthasis, braces, or brackets
            for (int i = 0; i < exp.size(); i++) {
                if (ops.contains(exp.get(i))) {
                    System.out.println("THERE WAS AN ERROR USING THE RPN EXPRESSION: Leftover parenthesis/brace/bracket.");
                    System.exit(0);
                }
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    // Main function for calculator
    public static void main(String[] args) throws FileNotFoundException {
        // User Inputs
        Scanner scan = new Scanner(System.in);
        boolean continuation = false;

        // Menu
        do {
            System.out.println("----------RPN CALCULATOR----------");
            System.out.println("--SELECT AN OPTION FROM THE MENU--");
            System.out.println("1. Compute");
            System.out.println("2. Help");
            System.out.println("3. Exit");
            System.out.print("Selection: ");
            
            // Selecting option
            String selection = scan.nextLine();
            // Remove whitespace/non-characters and make selection lowercase
            selection = selection.toLowerCase().replaceAll("\\s+","");

            // Compute
            if (selection.equals("1") || selection.equals("compute")) {
                boolean repeat = false;
                do {
                    System.out.println("\n\nCOMPUTATION");
                    System.out.print("Please enter your expression here: ");
                    String expression = scan.nextLine();
                    expression = expression.toLowerCase().replaceAll("\\s+","");

                    // Convert
                    List<String> listExpression = createExpression(expression);
                    List<String> rpnExpression = shuntingYard(listExpression);
                    confirmExpression(rpnExpression);

                    // Calculate
                    double result = computeExpression(rpnExpression);

                    // Return result
                    System.out.println("Result: " + result);

                    // Do another expression
                    System.out.println("Would you like to do another expression? (Yes to repeat / Anything else for no)");

                    String choice = scan.nextLine();
                    choice = choice.toLowerCase().replaceAll("\\s+","");

                    // Check if choice is no or yes
                    if (choice.equals("yes")) {
                        repeat = true;
                    }
                    else {
                        repeat = false;
                    }
                } while (repeat == true);
            }
            
            // For help
            else if (selection.equals("2") || selection.equals("help")) {
                // Read from help file
                Scanner read = new Scanner(new BufferedReader(new FileReader("help.txt")));
                while (read.hasNextLine()) {
                    System.out.println(read.nextLine());
                }
                read.close();
            }

            // Exit program
            else if (selection.equals("3") || selection.equals("exit")) {
                System.out.println("CLOSING PROGRAM. THANK YOU FOR USING RPN CALCULATOR!");
                continuation = true;
            }

            // Else if no selection was made or made incorrectly
            else {
                System.out.println("Sorry, but we didn't understand your request.");
            }
        } while (continuation == false);

        scan.close();
    }


    // computeExpression function takes the rpn expression and calculates it
    public static double computeExpression(List<String> expression) {
        // Create a list of strings to hold various operators
        List<String> basicOps = Arrays.asList("+", "-", "*", "/", "^");
        List<String> trigOps = Arrays.asList("sin", "cos", "tan", "cot", "arcsin", "arccos", "arctan", "arcctg");
        List<String> logOps = Arrays.asList("ln", "log", "sqrt");

        // Create copyExpression from the original expression
        List<String> copyExpression = new ArrayList<>();

        // Try and catch errors
        try {
            // Copy the original expression
            copyExpression.addAll(expression);

            // Initialize the index variable
            int index = 0;

            // Loop while the copied expresion has more than one element
            while (copyExpression.size() > 1) {
                // Initialize a variable temp to hold values
                double temp = 0;

                // If the next element is an basic operator
                if (basicOps.contains(copyExpression.get(index))) {
                    // Do the operation
                    // If two numbers are present with an operator
                    if (index >= 2) {
                        // Compute the basic operation
                        temp = basicComputation(Double.parseDouble(copyExpression.get(index-2)), Double.parseDouble(copyExpression.get(index-1)), copyExpression.get(index));
                        // Index at -2 will have the result, while the two elements in front will be removed from the list
                        copyExpression.set(index-2, Double.toString(temp));
                        copyExpression.remove(index-1);
                        copyExpression.remove(index-1);
                        // Reset the index
                        index = 0;
                    }
                    // If only one number is present followed by an operator
                    else if (index == 1) {
                        // For negative numbers
                        if (copyExpression.get(index).equals("-")) {
                            // Ignore the operator and keep the number and make it negative
                            temp = Double.parseDouble(copyExpression.get(index-1));
                            temp = 0 - temp;
                            // Index at -1 will have the result, while the element in front will be removed from the list
                            copyExpression.set(index-1, Double.toString(temp));
                            copyExpression.remove(index);
                            // Reset the index
                            index = 0;
                        }
                        // For positive numbers
                        else if (copyExpression.get(index).equals("+")) {
                            // Ignore the operator and keep the number
                            temp = Double.parseDouble(copyExpression.get(index-1));
                            // Index at -1 will have the result, while the element in front will be removed from the list
                            copyExpression.set(index-1, Double.toString(temp));
                            copyExpression.remove(index);
                            // Reset the index
                            index = 0;
                        }
                        else {
                            System.out.println("ERROR, CANNOT PROCESS EXPRESSION AS THERE IS AN INVALID OPERATOR USED WITH ONLY ONE NUMBER.");
                            System.exit(0);
                        }
                    }
                }

                // If their is a trig operator
                else if (trigOps.contains(copyExpression.get(index))) {
                    // Compute the trig operation
                    temp = trigComputation(Double.parseDouble(copyExpression.get(index-1)), copyExpression.get(index));
                    // Index at -1 will have the result, while the element in front will be removed from the list
                    copyExpression.set(index-1, Double.toString(temp));
                    copyExpression.remove(index);
                    // Reset index
                    index = 0;
                }

                // If their is a log operator
                else if (logOps.contains(copyExpression.get(index))) {
                    // Compute the log operation
                    temp = logComputations(Double.parseDouble(copyExpression.get(index-1)), copyExpression.get(index));
                    // Index at -1 will have the result, while the element in front will be removed from the list
                    copyExpression.set(index-1, Double.toString(temp));
                    copyExpression.remove(index);
                    // Reset index
                    index = 0;
                }

                index++;
            }
        }

        // Catch for null expressions
        catch (NullPointerException n) {
            System.out.println("THERE WAS AN ERROR FROM RPN EXPRESSION: RECEIVED A NULL EXPRESSION.");
            System.exit(0);
        }

        // Catch for number formatting issues
        catch (NumberFormatException n) {
            System.out.println("THERE WAS AN ERROR FROM THE RESULT NUMBER: " + n);
            System.exit(0);
        }

        // Initialize result as 0 for now
        double result = 0;
        try {
            // Return the result of the expression
            result = Double.parseDouble(copyExpression.get(0));
        }

        // Catch any issues such as more than one decimal used for number
        catch (NumberFormatException n) {
            System.out.println("THERE WAS AN ERROR FROM THE RESULT NUMBER: " + n);
            System.exit(0);
        }
        
        // Check if the result is not a number
        if (Double.isNaN(result)) {
            System.out.println("EXPRESSION IS NOT POSSIBLE, ENDED UP AS NOT A NUMBER.");
            return result;
        }
        return result;
    }

    // basicComputation function
    public static double basicComputation(double x, double y, String op) {
        // Result will store the final computation
        double result = 0;

        // Addition
        if (op.equals("+")) {
            result = x + y;
        }

        // Subtraction
        else if (op.equals("-")) {
            result = x - y;
        }

        // Division
        else if (op.equals("/")) {
            // Check if the denominator is zero
            if (y == 0) {
                System.out.println("ERROR! CANNOT DIVIDE BY ZERO!");
                System.exit(0);
            }
            else {
                result = x / y;
            }
        }

        // Multiplication
        else if (op.equals("*")) {
            result = x * y;
        }

        // Power
        else if (op.equals("^")) {
            result = Math.pow(x, y);
        }

        return result;
    }

    // trigComputation function
    public static double trigComputation(double x, String op) {
        // Result will store the final computation
        double result = 0;

        // All trig functions will return in radians
        // Sine
        if (op.equals("sin")) {
            result = Math.sin(x);
        }

        // Cosine
        else if (op.equals("cos")) {
            result = Math.cos(x);
        }

        // Tangent
        else if (op.equals("tan")) {
            result = Math.tan(x);
        }

        // Cotangent
        else if (op.equals("cot")) {
            result = 1 / Math.tan(x);
        }

        // Arcsine
        else if (op.equals("arcsin")) {
            result = Math.asin(x);
        }

        // Arccosine
        else if (op.equals("arccos")) {
            result = Math.acos(x);
        }

        // Arctangent
        else if (op.equals("arctan")) {
            result = Math.atan(x);
        }

        // Arccotangent
        else if (op.equals("arcctg")) {
            result = Math.PI / 2 - Math.atan(x);
        }

        return result;
    }

    // logComputations functions
    public static double logComputations(double x, String op) {
        // Result will store the final computation
        double result = 0;

        // Natural Log
        if (op.equals("ln")) {
            result = Math.log(x);
        }

        // Log Base 10
        else if (op.equals("log")) {
            result = Math.log10(x);
        }

        // Square root, even though it isnt a log function...
        else if (op.equals("sqrt")) {
            result = Math.sqrt(x);
        }

        return result;
    }

    // createExpression function will take a string expression created by the user and translate
    // it into a workable array list for the shunting yard algorithm
    public static List<String> createExpression(String origin) {
        char[] tempOrigin = origin.toCharArray();
        List<Character> operators = Arrays.asList('+', '-', '/', '*', '(', ')', '{', '}', '[', ']', '^');
        List<String> result = new ArrayList<>();

        // Temparary variable to hold numbers
        String number = "";

        for (int i = 0; i < tempOrigin.length; i++) {
            try {
                // Checking for digits
                if (Character.isDigit(tempOrigin[i]) || tempOrigin[i] == '.') {
                    number += tempOrigin[i];
                }

                // Add digits into expression
                else if (number.length() > 0) {
                    result.add(number);
                    number = "";
                    i--;
                }

                // Check if token is contained in the operators list
                else if (operators.contains(tempOrigin[i])) {
                    // Check if i-1 isn't out of the array
                    if (i-1 >= 0 && i-1 < tempOrigin.length) {
                        // Check for duplicates
                        if (tempOrigin[i] == tempOrigin[i-1]) {
                            // Check if subtraction (just in case)
                            if (tempOrigin[i] == '-') {
                                result.remove(result.size()-1);
                                result.add("+");
                            }
                            else {
                                result.add(Character.toString(tempOrigin[i]));
                            }
                        }

                        else if (tempOrigin[i] == '-' && operators.contains(tempOrigin[i-1]) && Character.isDigit(tempOrigin[i+1])) {
                            number += "-";
                        }

                        // Check for the rest
                        else {
                            result.add(Character.toString(tempOrigin[i]));
                        }
                    }

                    // Else that the operator is either first
                    else {
                        // Check for unary subtraction symbol
                        if (tempOrigin[i] == '-' && i == 0 && Character.isDigit(tempOrigin[i+1])) {
                            number += "-"; 
                        }

                        // Check for double negative to positive
                        else if (tempOrigin[i] == tempOrigin[i+1]) {
                            result.add("+");
                        }

                        // Check if proceeded by a parenthasis, etc.
                        else if (tempOrigin[i] == '-' && tempOrigin[i+1] == '(') {
                            result.add("-");
                        }

                        // Invalid operator
                        else {
                            System.out.println("ERROR, CANNOT ADD OPERATOR FIRST UNLESS A NEGATIVE OPERATOR");
                            System.exit(0);
                        }
                    }
                }

                // Checking for other operators that are longer than one character
                // Checking for square root
                else if (tempOrigin[i] == 's' && tempOrigin[i+1] == 'q' 
                    && tempOrigin[i+2] == 'r' && tempOrigin[i+3] == 't') {
                    result.add("sqrt");
                    i += 3;
                }

                // Checking for sine
                else if (tempOrigin[i] == 's' && tempOrigin[i+1] == 'i' && tempOrigin[i+2] == 'n') {
                    result.add("sin");
                    i += 2;
                }

                // Checking for cosine
                else if (tempOrigin[i] == 'c' && tempOrigin[i+1] == 'o' && tempOrigin[i+2] == 's') {
                    result.add("cos");
                    i += 2;
                }

                // Checking for tangent
                else if (tempOrigin[i] == 't' && tempOrigin[i+1] == 'a' && tempOrigin[i+2] == 'n') {
                    result.add("tan");
                    i += 2;
                }

                // Checking for cotangent
                else if (tempOrigin[i] == 'c' && tempOrigin[i+1] == 'o' && tempOrigin[i+2] == 't') {
                    result.add("cot");
                    i += 2;
                }

                // Checking for arcsine
                else if (tempOrigin[i] == 'a' && tempOrigin[i+1] == 'r' && tempOrigin[i+2] == 'c'
                    && tempOrigin[i+3] == 's' && tempOrigin[i+4] == 'i' && tempOrigin[i+5] == 'n') {
                    result.add("arcsin");
                    i += 5;
                }

                // Checking for arccosine
                else if (tempOrigin[i] == 'a' && tempOrigin[i+1] == 'r' && tempOrigin[i+2] == 'c'
                    && tempOrigin[i+3] == 'c' && tempOrigin[i+4] == 'o' && tempOrigin[i+5] == 's') {
                    result.add("arccos");
                    i += 5;
                }

                // Checking for arctangent
                else if (tempOrigin[i] == 'a' && tempOrigin[i+1] == 'r' && tempOrigin[i+2] == 'c'
                    && tempOrigin[i+3] == 't' && tempOrigin[i+4] == 'a' && tempOrigin[i+5] == 'n') {
                    result.add("arctan");
                    i += 5;
                }

                // Checking for arccotangent
                else if (tempOrigin[i] == 'a' && tempOrigin[i+1] == 'r' && tempOrigin[i+2] == 'c'
                    && tempOrigin[i+3] == 'c' && tempOrigin[i+4] == 't' && tempOrigin[i+5] == 'g') {
                    result.add("arcctg");
                    i += 5;
                }

                // Checking for natural log
                else if (tempOrigin[i] == 'l' && tempOrigin[i+1] == 'n') {
                    result.add("ln");
                    i++;
                }

                // Checking for log base 10
                else if (tempOrigin[i] == 'l' && tempOrigin[i+1] == 'o' && tempOrigin[i+2] == 'g') {
                    result.add("log");
                    i += 2;
                }

                // If char does not apply to the expression rules, then show error
                else {
                    System.out.println("THERE WAS AN ERROR PROCESSING YOUR EQUATION HERE: " + tempOrigin[i] + " is not a valid expression.");
                    return null;
                }
            }
            // Catch any errors in expression
            catch (Exception e) {
                System.out.println("ERROR! REMOVED OPERATION: " + tempOrigin[i]);
            }
        }
        // Check for leftover numbers
        if (number.length() > 0) {
            result.add(number);
        }
        return result;
    }
}