import java.util.Scanner;
import java.util.Stack;

public class TerminalCalc {

    public static void main(String[] args) {

        //scanner to receive user input from terminal
        Scanner scanner = new Scanner(System.in);

        System.out.println("Terminal Calculator");
        System.out.println("Enter an expression to calculate, or 'exit' to quit.");

        while (true) {
            System.out.print("Enter expression: ");
            String input = scanner.nextLine().trim();

            // Exit condition
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting calculator...");
                break;
            }

            try {

                // Preprocess the input
                input = insertImplicitMultiplication(input);
                System.out.println("Processed Expression: " + input);  // Debugging statement

                // Perform the calculation and print the result
                double result = calculate(input);
                System.out.println("Result: " + result);

            } catch (Exception e) {

                e.printStackTrace(); // Print the full stack trace for better debugging
                System.out.println("Error: " + e.getMessage());

            }
        }

        scanner.close();
    }

    //credit to geeks for geeks. Post fix notation code from their website was used as a base and edited to make this method.
    private static double calculate(String input) {

        //stacks to hold operators and numbers
        Stack<Double> values = new Stack<>();
        Stack<String> operators = new Stack<>();

        //cycle through the input
        for (int i = 0; i < input.length(); i++) {

            char c = input.charAt(i);

            // Handle unary minus before parentheses or at the start
            if (c == '-' && (i == 0 || isOperator(input.charAt(i - 1)) || input.charAt(i - 1) == '(')) {

                StringBuilder number = new StringBuilder();
                number.append('-');
                i++; // Move past the '-' sign

                // If next character is '(', treat it as multiplication by -1
                if (i < input.length() && input.charAt(i) == '(') {

                    number.append('1');
                    operators.push("*");  // Handle multiplication

                }

                // Parse the number (including decimals)
                while (i < input.length() && (Character.isDigit(input.charAt(i)) || input.charAt(i) == '.')) {

                    number.append(input.charAt(i));
                    i++;

                }

                i--; // Adjust for loop increment

                try {

                    values.push(Double.parseDouble(number.toString()));

                } catch (NumberFormatException ex) {

                    throw new IllegalArgumentException("Invalid number format: " + number);

                }

                System.out.println("Pushed negative number: " + number);
                continue; // Move to the next character in the main loop
            }

            // Parse numbers (including decimals)
            else if (Character.isDigit(c) || c == '.') {

                StringBuilder number = new StringBuilder();

                while (i < input.length() && (Character.isDigit(input.charAt(i)) || input.charAt(i) == '.')) {

                    number.append(input.charAt(i));
                    i++;

                }

                i--; // Adjust for loop increment

                try {

                    values.push(Double.parseDouble(number.toString()));

                } catch (NumberFormatException ex) {

                    throw new IllegalArgumentException("Invalid number format: " + number);

                }

                System.out.println("Pushed number: " + number);
            }

            // Parse functions (sin, cos, tan, log, etc.)
            else if (Character.isLetter(c)) {

                StringBuilder function = new StringBuilder();

                while (i < input.length() && Character.isLetter(input.charAt(i))) {

                    function.append(input.charAt(i));
                    i++;

                }

                i--; // Adjust index

                operators.push(function.toString());
                System.out.println("Pushed function: " + function);
            }

            // Parentheses handling
            else if (c == '(') {

                operators.push(String.valueOf(c));

            } else if (c == ')') {

                while (!operators.isEmpty() && !operators.peek().equals("(")) {

                    applyOperation(values, operators.pop());

                }

                if (!operators.isEmpty() && operators.peek().equals("(")) {

                    operators.pop();

                } else {

                    throw new IllegalArgumentException("Mismatched parentheses in expression.");

                }

                // Handle function calls after closing parentheses
                if (!operators.isEmpty() && isFunction(operators.peek())) {
                    applyOperation(values, operators.pop());
                }
            }

            // Handle operators
            else if (isOperator(c)) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(String.valueOf(c))) {
                    applyOperation(values, operators.pop());
                }

                operators.push(String.valueOf(c));
                System.out.println("Pushed operator: " + c);
            }
        }

        // Process remaining operators
        while (!operators.isEmpty()) {
            applyOperation(values, operators.pop());
        }

        if (values.size() != 1) {
            throw new IllegalArgumentException("Invalid expression.");
        }

        double result = values.pop();

        // Round the result to 2 decimal places
        result = Math.round(result * 100.0) / 100.0;
        System.out.println("Final result: " + result);
        return result;
    }

    // Applies the given operator to the values stack
    private static void applyOperation(Stack<Double> values, String operator) {

        if (values.size() < 2 && !operator.equals("sin") && !operator.equals("cos") && !operator.equals("tan") && !operator.equals("ln")) {

            throw new IllegalStateException("Not enough operands for the operation.");

        }

        double result;

        if (operator.equals("sin") || operator.equals("cos") || operator.equals("tan") || operator.equals("ln")) {

            double b = values.pop();
            result = performFunction(operator, b);

        } else {

            double b = values.pop();
            double a = values.pop();
            result = performOperation(a, b, operator);

        }

        values.push(result);

    }

    private static double performOperation(double a, double b, String operator) {

        switch (operator) {

            case "+":
                return a + b;
            case "-":
                return a - b;
            case "*":
                return a * b;
            case "/":
                return a / b;
            case "^":
                return Math.pow(a, b);

            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }

    private static double performFunction(String operator, double b) {

        switch (operator) {

            case "sin":
                return Math.sin(Math.toRadians(b));
            case "cos":
                return Math.cos(Math.toRadians(b));
            case "tan":
                return Math.tan(Math.toRadians(b));
            case "ln":
                if (b <= 0) {
                    throw new IllegalArgumentException("Logarithm undefined for non-positive values.");
                }
                return Math.log(b);

            default:
                throw new IllegalArgumentException("Unknown function: " + operator);

        }
    }

    // Helper method to determine if a character is an operator
    private static boolean isOperator(char c) {

        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';

    }

    // Determines the precedence of operators
    private static int precedence(String op) {

        switch (op) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            case "^":
                return 3;

            default:
                return 0;
        }
    }

    // Inserts multiplication operators where implicit multiplication is detected
    private static String insertImplicitMultiplication(String input) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {

            char curr = input.charAt(i);

            if (i > 0) {

                char prev = input.charAt(i - 1);

                //checks where to add multiplication.
                if ((Character.isDigit(prev) || prev == ')') && curr == '(') {

                    sb.append('*');

                }

            }

            sb.append(curr);

        }

        return sb.toString();
    }

    // Checks if a string is a function (sin, cos, log, etc.)
    private static boolean isFunction(String s) {

        return s.equals("sin") || s.equals("cos") || s.equals("tan") || s.equals("ln");


    }
}
