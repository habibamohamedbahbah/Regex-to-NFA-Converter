import java.util.Stack;

public class Person1_Parser {

    public static int precedence(char c) {
        switch (c) {
            case '*': return 3;  
            case '+': return 3;
            case '?': return 3;
            case '.': return 2; 
            case '|': return 1; 
            default:  return 0;
        }
    }

    public static String addConcat(String regex) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < regex.length(); i++) {
            char current = regex.charAt(i);
            result.append(current);

            if (i + 1 < regex.length()) {
                char next = regex.charAt(i + 1);
                boolean leftOk  = Character.isLetterOrDigit(current)
                                || current == ')'
                                || current == '*'
                                || current == '+'
                                || current == '?';

                boolean rightOk = Character.isLetterOrDigit(next)
                                || next == '(';

                if (leftOk && rightOk) {
                    result.append('.');
                }
            }
        }

        return result.toString();
    }

    public static String toPostfix(String regex) {
        StringBuilder output = new StringBuilder();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < regex.length(); i++) {
            char c = regex.charAt(i);

            if (Character.isLetterOrDigit(c)) {
        
                output.append(c);

            } else if (c == '(') {
                operators.push(c);

            } else if (c == ')') {
               
                while (!operators.isEmpty() && operators.peek() != '(') {
                    output.append(operators.pop());
                }
                if (!operators.isEmpty()) {
                    operators.pop(); }

            } else {
                  while (!operators.isEmpty()
                        && operators.peek() != '('
                        && precedence(operators.peek()) >= precedence(c)) {
                    output.append(operators.pop());
                }
                operators.push(c);
            }
        }

        while (!operators.isEmpty()) {
            output.append(operators.pop());
        }

        return output.toString();
    }
}