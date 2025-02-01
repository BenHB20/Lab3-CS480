import javax.swing.*;
import java.util.Stack;

public class Mainpage {
    private JTextPane calculationPane;
    private JPanel MainPanel;
    private JPanel mainButtonPanel;
    private JButton eraseButton;
    private JButton zeroButton;
    private JButton enterButton;
    private JButton oneButton;
    private JButton twoButton;
    private JButton threeButton;
    private JButton fiveButton;
    private JButton sixButton;
    private JButton fourButton;
    private JButton nineButton;
    private JButton eightButton;
    private JButton sevenButton;
    private JPanel signPanel;
    private JButton addButton;
    private JButton divButton;
    private JButton multButton;
    private JButton subButton;
    private JButton exponentButton;
    private JButton lnButton;
    private JButton log10Button;
    private JButton parLeftButton;
    private JButton cotButton;
    private JButton tanButton;
    private JButton cosButton;
    private JButton sinButton;
    private JButton parRightButton;
    private JButton rightCurlButton;
    private JButton leftCurlButton;
    private JButton negativeButton;
    private JPanel modifierPanel;
    private JScrollPane scrollPane;
    private JTextPane errorPane;
    private JButton decimalButton;
    private  JFrame calcFrame= new JFrame("Calculator");

    public void buildGuiPanel() {
        calcFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        calcFrame.setSize(800, 300);
        calcFrame.setContentPane(MainPanel);
        calcFrame.setVisible(true);

        // Add action listeners for all number buttons
        zeroButton.addActionListener(e -> calculationPane.setText(calculationPane.getText() + "0" ));
        oneButton.addActionListener(e -> calculationPane.setText(calculationPane.getText() + "1"));
        twoButton.addActionListener(e -> calculationPane.setText(calculationPane.getText() + "2"));
        threeButton.addActionListener(e -> calculationPane.setText(calculationPane.getText() + "3"));
        fourButton.addActionListener(e -> calculationPane.setText(calculationPane.getText() + "4"));
        fiveButton.addActionListener(e -> calculationPane.setText(calculationPane.getText() + "5"));
        sixButton.addActionListener(e -> calculationPane.setText(calculationPane.getText() + "6"));
        sevenButton.addActionListener(e -> calculationPane.setText(calculationPane.getText() + "7"));
        eightButton.addActionListener(e -> calculationPane.setText(calculationPane.getText() + "8"));
        nineButton.addActionListener(e -> calculationPane.setText(calculationPane.getText() + "9"));

        // all action listeners for operation
        addButton.addActionListener(e -> calculationPane.setText(calculationPane.getText() + "+"));
        subButton.addActionListener(e -> calculationPane.setText(calculationPane.getText() + "-"));
        multButton.addActionListener(e -> calculationPane.setText(calculationPane.getText() + "*"));
        divButton.addActionListener(e -> calculationPane.setText(calculationPane.getText() + "/"));

        // all action listeners for modifiers
        exponentButton.addActionListener(e -> calculationPane.setText(calculationPane.getText() + "^"));
        lnButton.addActionListener(e -> calculationPane.setText(calculationPane.getText() + "ln()"));
        log10Button.addActionListener(e -> calculationPane.setText(calculationPane.getText() + "log(10)"));
        sinButton.addActionListener(e -> calculationPane.setText(calculationPane.getText() + "sin()"));
        cosButton.addActionListener(e -> calculationPane.setText(calculationPane.getText() + "cos()"));
        tanButton.addActionListener(e -> calculationPane.setText(calculationPane.getText() + "tan()"));
        cotButton.addActionListener(e -> calculationPane.setText(calculationPane.getText() + "cot()"));
        parLeftButton.addActionListener(e -> calculationPane.setText(calculationPane.getText() + "("));
        parRightButton.addActionListener(e -> calculationPane.setText(calculationPane.getText() + ")"));
        leftCurlButton.addActionListener(e -> calculationPane.setText(calculationPane.getText() + "{"));
        rightCurlButton.addActionListener(e -> calculationPane.setText(calculationPane.getText() + "}"));
        negativeButton.addActionListener(e -> {calculationPane.setText(calculationPane.getText() + "-");});

        // all action listeners for special buttons
        eraseButton.addActionListener(e -> {
            String currentText = calculationPane.getText();
            if (!currentText.isEmpty()) {
                // Remove the last character
                calculationPane.setText(currentText.substring(0, currentText.length() - 1));
            }
        });

        enterButton.addActionListener(e -> {

                String calculation = " = "+ calculate();
                calculationPane.setText(calculationPane.getText() + calculation + "\n");

        });

        // Action listener for the decimal button
        decimalButton.addActionListener(e -> {
            String currentText = calculationPane.getText();
            // Determine the "current token" by looking backwards from the end until we reach an operator or parenthesis.
            int index = currentText.length() - 1;
            boolean tokenContainsDecimal = false;
            while (index >= 0) {
                char ch = currentText.charAt(index);
                // If we hit an operator or parenthesis, stop checking.
                if (!Character.isDigit(ch) && ch != '.') {
                    break;
                }
                if (ch == '.') {
                    tokenContainsDecimal = true;
                    break;
                }
                index--;
            }
            // Only append a decimal point if the current token doesn't already contain one.
            if (!tokenContainsDecimal) {
                calculationPane.setText(currentText + ".");
            }
        });


    }

    private double calculate() {
        // Reset stacks for each calculation
        Stack<Double> values = new Stack<>();
        Stack<String> operators = new Stack<>();

        // Retrieve only the latest line of input
        String fullText = calculationPane.getText().trim();
        String[] lines = fullText.split("\n");
        if (lines.length == 0) return 0;

        String input = lines[lines.length - 1].trim();

        // Prevent duplicate results
        if (input.startsWith("=")) return 0;

        // Preprocess input to insert multiplication operators for implicit multiplication
        input = insertImplicitMultiplication(input);

        input = input.replaceAll("(?<=^|[+\\-*/^(])-\\(", "-1*(");

        System.out.println("Preprocessed Input: " + input); // Debug

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            // Check if the current character is a '-' and is being used as a unary operator.
            // It is unary if it is the first character, or if the previous character is an operator or '('.
            if (c == '-' && (i == 0 || isOperator(input.charAt(i - 1)) || input.charAt(i - 1) == '(')) {
                StringBuilder number = new StringBuilder();
                number.append('-');
                i++; // Move past the '-' sign

                // Make sure there is a number following the unary minus
                if (i < input.length() && (Character.isDigit(input.charAt(i)) || input.charAt(i) == '.')) {
                    // Parse the number (including decimals)
                    while (i < input.length() && (Character.isDigit(input.charAt(i)) || input.charAt(i) == '.')) {
                        number.append(input.charAt(i));
                        i++;
                    }
                    i--; // Adjust for the loop increment
                    try {
                        values.push(Double.parseDouble(number.toString()));
                    } catch (NumberFormatException ex) {
                        errorPane.setText("Invalid number format: " + number);
                        throw new IllegalArgumentException("Invalid number format: " + number);
                    }
                    System.out.println("Pushed negative number: " + number);
                    continue; // Move to the next character in the main loop
                } else {
                    // If there is no valid number after the '-', treat it as a subtraction operator.
                    // Step back so the operator logic can handle it.
                    i--;
                }
            }

            // Parse numbers (including decimals)
            if (Character.isDigit(c) || c == '.') {
                StringBuilder number = new StringBuilder();
                while (i < input.length() && (Character.isDigit(input.charAt(i)) || input.charAt(i) == '.')) {
                    number.append(input.charAt(i));
                    i++;
                }
                i--; // Adjust for loop increment
                try {
                    values.push(Double.parseDouble(number.toString()));
                } catch (NumberFormatException ex) {
                    errorPane.setText("Invalid number format: " + number);
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
                    errorPane.setText("Mismatched parentheses in expression.");
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
            errorPane.setText("Invalid expression.");
            throw new IllegalArgumentException("Invalid expression.");
        }

        double result = values.pop();
// Round the result to 2 decimal places
        result = Math.round(result * 100.0) / 100.0;
        System.out.println("Final result: " + result);
        return result;

    }

    /**
     * Inserts a multiplication operator (*) when implicit multiplication is detected.
     * For example, turns "12(-9-(-3))" into "12*(-9-(-3))".
     */
    private String insertImplicitMultiplication(String input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char curr = input.charAt(i);
            if (i > 0) {
                char prev = input.charAt(i - 1);
                // If the previous character is a digit or a closing parenthesis and the current one is an opening parenthesis,
                // insert an explicit multiplication operator.
                if ((Character.isDigit(prev) || prev == ')') && curr == '(') {
                    sb.append('*');
                }
                // Optionally, you can handle other cases like a closing parenthesis followed by a letter (e.g. ")sin(30)")
                // if needed.
            }
            sb.append(curr);
        }
        return sb.toString();
    }



    // Apply operations, including sin, cos, tan, log, ln, and exponentiation
    private void applyOperation(Stack<Double> values, String operator) {
        if (isFunction(operator)) {
            if (values.isEmpty()) {
                errorPane.setText("");
                errorPane.setText("Missing argument for function: " + operator);
                throw new IllegalArgumentException("Missing argument for function: " + operator);
            }
            double x = values.pop();
            double result;
            switch (operator) {
                case "sin":
                    result = Math.sin(Math.toRadians(x));
                    break;
                case "cos":
                    result = Math.cos(Math.toRadians(x));
                    break;
                case "tan":
                    result = Math.tan(Math.toRadians(x));
                    break;
                case "cot":
                    if (Math.tan(Math.toRadians(x)) == 0) {
                        throw new ArithmeticException("Undefined cotangent.");
                    }
                    result = 1 / Math.tan(Math.toRadians(x));
                    break;
                case "log":
                    if (x <= 0){

                        errorPane.setText("");
                        errorPane.setText("Logarithm undefined for x <= 0.");
                        throw new ArithmeticException("Logarithm undefined for x <= 0.");
                    }
                    result = Math.log10(x);
                    break;
                case "ln":
                    if (x <= 0) throw new ArithmeticException("Natural log undefined for x <= 0.");
                    result = Math.log(x);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown function: " + operator);
            }
            values.push(Math.round(result * 1000000.0) / 1000000.0); // Rounds to 6 decimal places
            System.out.println("Applied function " + operator + ": " + result);
        } else {
            if (values.size() < 2) {
                errorPane.setText("");
                errorPane.setText("Invalid expression: not enough operands.");
                throw new IllegalArgumentException("Invalid expression: not enough operands.");
            }
            double b = values.pop();
            double a = values.pop();

            double result;
            switch (operator) {
                case "+":
                    result = a + b;
                    break;
                case "-":
                    result = a - b;
                    break;
                case "*":
                    result = a * b;
                    break;
                case "/":
                    if (b == 0) throw new ArithmeticException("Division by zero.");
                    result = a / b;
                    break;
                case "^":
                    result = Math.pow(a, b);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown operator: " + operator);
            }
            values.push(result);
            System.out.println("Applied operator " + operator + ": " + result);
        }
    }

    // Checks if a string is an operator
    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
    }

    // Checks if a string is a function (sin, cos, log, etc.)
    private boolean isFunction(String s) {
        return s.equals("sin") || s.equals("cos") || s.equals("tan") ||
                s.equals("cot") || s.equals("log") || s.equals("ln");
    }

    // Operator precedence
    private int precedence(String op) {
        switch (op) {
            case "+": case "-":
                return 1;
            case "*": case "/":
                return 2;
            case "^":
                return 3; // Exponentiation has the highest precedence
            default:
                return 0;
        }
    }


}
