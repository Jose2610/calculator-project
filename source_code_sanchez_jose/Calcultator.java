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
        //String given = "-5.78+-(4-2.23)+sin(0)*cos(1)/(1+tan(2*-ln(-3+2*(1.23+arcsin(1)))))";
        //String test = "1.23+arcsin(1)";
        //List<String> expected = List.of("1", "2", "+", "3", "4", "/", "5", "6", "+", "^", "*");
        //List<String> computed = createExpression(given);
        //List<String> rpnComputed = shuntingYard(computed);

        //System.out.println("Infix: " + given);
        //System.out.println("RPN (expected): " + expected);

        //System.out.println("RPN (converted): " + computed);
        //System.out.println("RPN (computed): " + rpnComputed);

        // Testing computeExpression
        //double result = computeExpression(rpnComputed);
        //System.out.println("RPN Solution: " + result);
		System.exit(0);
    }