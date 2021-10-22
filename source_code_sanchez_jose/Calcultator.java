/*
Author: Jose Luis Sanchez
Date Modified: 11/4/2021
Purpose: Calculator that does basic arthmetic/trigonometric/logarithmic operations using the Shunting Yard Algorithm
*/

// Importing Libraries
import java.util.*;

public class Calcultator {

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
    final static Map<String, Operator> OPS = new HashMap<>();

    // Fill OPS with keys (operator symbol) and values (operator attributes)
    static {
        for(Operator operator : Operator.values()) {
            OPS.put(operator.symbol, operator);
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
                if(OPS.containsKey(token)) {
                    // While the operator stack is not empty AND the OPS map contains a key, continue loop
                    while(!stack.isEmpty() && OPS.containsKey(stack.peek())) {
                        // Create an instance of Operator
                        Operator currOp = OPS.get(token); // Gets the current operator
                        Operator topOp = OPS.get(stack.peek()); // Gets the top operator in the OPS map
                        
                        // If 
                        if((currOp.associativity == Associativity.LEFT && currOp.comparePrecedence(topOp) <= 0) || 
                            (currOp.associativity == Associativity.RIGHT && currOp.comparePrecedence(topOp) < 0)) {
                                output.add(stack.pop());
                                continue;
                            }
                            break;
                    }
                    stack.push(token);
                } 
                else if("(".equals(token)) {
                    stack.push(token);
                }

                else if(")".equals(token)) {
                    while(!stack.isEmpty() && !stack.peek().equals("(")) {
                        output.add(stack.pop());
                    }
                    stack.pop();
                }

                else {
                    output.add(token);
                }
            }

            while(!stack.isEmpty()) {
                output.add(stack.pop());
            }

            return output;
        }
        catch (NullPointerException e) {
            System.out.println("THERE WAS AN ERROR CONVERTING INFIX TO RPN. RETURNING NULL.");
        }
        return null;
    }

    // Main function for calculator
    public static void main(String[] args) {
        String given = "-5.78+-(4-2.23)+sin(0)*cos(1)/(1+tan(2*-ln(-3+2*(1.23+arcsin(1)))))";
        //String test = "1.23+arcsin(1)";
        //List<String> expected = List.of("1", "2", "+", "3", "4", "/", "5", "6", "+", "^", "*");
        List<String> computed = createExpression(given);
        List<String> rpnComputed = shuntingYard(computed);

        //System.out.println("Infix: " + given);
        //System.out.println("RPN (expected): " + expected);

        System.out.println("RPN (converted): " + computed);
        System.out.println("RPN (computed): " + rpnComputed);

        // Testing computeExpression
        double result = computeExpression(rpnComputed);
        System.out.println("RPN Solution: " + result);
    }


    // computeExpression function takes the rpn expression and calculates it
    public static double computeExpression(List<String> expression) {
        List<String> basicOps = Arrays.asList("+", "-", "*", "/", "^");
        List<String> trigOps = Arrays.asList("sin", "cos", "tan", "cot", "arcsin", "arccos", "arctan", "arcctg");
        List<String> logOps = Arrays.asList("ln", "log");

        List<String> copyExpression = new ArrayList<>();
        copyExpression.addAll(expression);
        double result = 0;
        int index = 0;

        while (copyExpression.size() > 1) {
            // Initialize a variable temp to hold values
            double temp = 0;
            // If the next element is an operator
            if (basicOps.contains(copyExpression.get(index))) {
                // Do the operation
                // If two numbers are present with an operator
                if (index >= 2) {
                    temp = basicComputation(Double.parseDouble(copyExpression.get(index-2)), Double.parseDouble(copyExpression.get(index-1)), copyExpression.get(index));
                    copyExpression.set(index-2, Double.toString(temp));
                    copyExpression.remove(index-1);
                    copyExpression.remove(index-1);
                    index = 0;
                }
                // If only one number is present with an operator
                else if (index == 1) {
                    temp = Double.parseDouble(copyExpression.get(index-1));
                    copyExpression.set(index-1, Double.toString(temp));
                    copyExpression.remove(index);
                    index = 0;
                }
            }

            else if (trigOps.contains(copyExpression.get(index))) {
                temp = trigComputation(Double.parseDouble(copyExpression.get(index-1)), copyExpression.get(index));
                copyExpression.set(index-1, Double.toString(temp));
                copyExpression.remove(index);
                index = 0;
            }
            else if (logOps.contains(copyExpression.get(index))) {
                temp = logComputations(Double.parseDouble(copyExpression.get(index-1)), copyExpression.get(index));
                copyExpression.set(index-1, Double.toString(temp));
                copyExpression.remove(index);
                index = 0;
            }

            index++;
        }

        return Double.parseDouble(copyExpression.get(0));
    }

    // basicComputation function
    public static double basicComputation(double x, double y, String op) {
        double result = 0;
        if (op.equals("+")) {
            result = x + y;
        }
        else if (op.equals("-")) {
            result = x - y;
        }
        else if (op.equals("/")) {
            if (y == 0) {
                System.out.println("ERROR! CANNOT DIVIDE BY ZERO!");
                System.exit(0);
            }
            else {
                result = x / y;
            }
        }
        else if (op.equals("*")) {
            result = x * y;
        }
        else if (op.equals("^")) {
            result = Math.pow(x, y);
        }

        return result;
    }

    // trigComputation function
    public static double trigComputation(double x, String op) {
        double result = 0;
        if (op.equals("sin")) {
            result = Math.sin(x);
        }
        else if (op.equals("cos")) {
            result = Math.cos(x);
        }
        else if (op.equals("tan")) {
            result = Math.tan(x);
        }
        else if (op.equals("cot")) {
            double rads = Math.toRadians(x);
            double tanA = Math.tan(rads);
            result = 1.0 / tanA;
        }
        else if (op.equals("arcsin")) {
            result = Math.asin(x);
        }
        else if (op.equals("arccos")) {
            result = Math.acos(x);
        }
        else if (op.equals("arctan")) {
            result = Math.atan(x);
        }
        else if (op.equals("arcctg")) {
            double coTanA = 1.0 / (Math.tan(x));
            result =  Math.atan(1/coTanA);
        }
        return result;
    }

    // logComputations functions
    public static double logComputations(double x, String op) {
        double result = 0;
        if (op.equals("ln")) {
            result = Math.log(x);
        }
        else {
            result = Math.log10(x);
        }
        return result;
    }

    // createExpression function will take a string expression created by the user and translate
    // it into a workable array list for the shunting yard algorithm
    public static List<String> createExpression(String origin) {
        char[] tempOrigin = origin.toCharArray();
        List<Character> operators = Arrays.asList('+', '-', '/', '*', '(', ')', '^');
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
                    // Check for unary subtraction symbol
                    if (tempOrigin[i] == '-' && i == 0 && Character.isDigit(tempOrigin[i+1])) {
                        number += "-"; 
                    }
                    // If unary negative symbol is not first, then try this one
                    else if (tempOrigin[i] == '-' && operators.contains(tempOrigin[i-1]) && Character.isDigit(tempOrigin[i+1])) {
                        number += "-"; 
                    }

                    // Check for duplicates
                    else if (tempOrigin[i] == tempOrigin[i-1]) {
                        // Check if subtraction (just in case)
                        if (tempOrigin[i] == '-') {
                            result.remove(result.size()-1);
                            result.add("+");
                        }
                        // Check for parenthasis
                        else if (tempOrigin[i] != '(' && tempOrigin[i] != ')'){
                            System.out.println("REMOVED DUPLICATE OPERATIONS: " + tempOrigin[i]);
                            continue;
                        }

                        // Check for the rest
                        else {
                            result.add(Character.toString(tempOrigin[i]));
                        }
                    }
                    else {
                        // If not, add that token to the result list
                        result.add(Character.toString(tempOrigin[i]));
                    }
                }

                // Checking for other operators that are longer than one character
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
                System.out.println("ERROR: " + tempOrigin[i]);
            }
        }
        return result;
    }
}