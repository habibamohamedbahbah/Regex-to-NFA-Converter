import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Scanner;
import java.util.TreeSet;

public class RegexToNFA {
    static class Transition {
        int from;
        int to;
        char symbol;

        Transition(int from, int to, char symbol) {
            this.from = from;
            this.to = to;
            this.symbol = symbol;
        }
    }

    static class State {
        private static int counter = 0;

        public static int newState() {
            return counter++;
        }

        public static void reset() {
            counter = 0;
        }
    }

    static class NFA {
        int startState;
        int finalState;
        List<Transition> transitions;

        NFA(int start, int end) {
            this.startState = start;
            this.finalState = end;
            this.transitions = new ArrayList<>();
        }

        void addTransition(int from, int to, char symbol) {
            transitions.add(new Transition(from, to, symbol));
        }
    }

    static int precedence(char c) {
        switch (c) {
            case '*': case '+': case '?': return 3;
            case '.': return 2;
            case '|': return 1;
            default:  return 0;
        }
    }

    static String addConcat(String regex) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < regex.length(); i++) {
            char current = regex.charAt(i);
            result.append(current);
            if (i + 1 < regex.length()) {
                char next = regex.charAt(i + 1);
                boolean leftOk  = Character.isLetterOrDigit(current)
                               || current == ')' || current == '*'
                               || current == '+' || current == '?';
                boolean rightOk = Character.isLetterOrDigit(next) || next == '(';
                if (leftOk && rightOk) result.append('.');
            }
        }
        return result.toString();
    }

    static String toPostfix(String regex) {
        StringBuilder output = new StringBuilder();
        Stack<Character> operators = new Stack<>();
        for (char c : regex.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                output.append(c);
            } else if (c == '(') {
                operators.push(c);
            } else if (c == ')') {
                while (!operators.isEmpty() && operators.peek() != '(')
                    output.append(operators.pop());
                if (!operators.isEmpty()) operators.pop();
            } else {
                while (!operators.isEmpty() && operators.peek() != '('
                        && precedence(operators.peek()) >= precedence(c))
                    output.append(operators.pop());
                operators.push(c);
            }
        }
        while (!operators.isEmpty()) output.append(operators.pop());
        return output.toString();
    }


    static NFA buildSingle(char c) {
        int s = State.newState();
        int e = State.newState();
        NFA nfa = new NFA(s, e);
        nfa.addTransition(s, e, c);
        return nfa;
    }

    static NFA buildConcat(NFA a, NFA b) {
        NFA result = new NFA(a.startState, b.finalState);
        result.transitions.addAll(a.transitions);
        result.addTransition(a.finalState, b.startState, 'e');
        result.transitions.addAll(b.transitions);
        return result;
    }

    static NFA buildUnion(NFA a, NFA b) {
        int s = State.newState();
        int e = State.newState();
        NFA result = new NFA(s, e);
        result.addTransition(s, a.startState, 'e');
        result.addTransition(s, b.startState, 'e');
        result.transitions.addAll(a.transitions);
        result.transitions.addAll(b.transitions);
        result.addTransition(a.finalState, e, 'e');
        result.addTransition(b.finalState, e, 'e');
        return result;
    }

    static NFA buildStar(NFA a) {
        int s = State.newState();
        int e = State.newState();
        NFA result = new NFA(s, e);
        result.addTransition(s, a.startState, 'e');
        result.addTransition(s, e, 'e');
        result.transitions.addAll(a.transitions);
        result.addTransition(a.finalState, a.startState, 'e');
        result.addTransition(a.finalState, e, 'e');
        return result;
    }

    static NFA buildPlus(NFA a) {
        int s = State.newState();
        int e = State.newState();
        NFA result = new NFA(s, e);
        result.addTransition(s, a.startState, 'e');
        result.transitions.addAll(a.transitions);
        result.addTransition(a.finalState, a.startState, 'e');
        result.addTransition(a.finalState, e, 'e');
        return result;
    }

    static NFA buildQuestion(NFA a) {
        int s = State.newState();
        int e = State.newState();
        NFA result = new NFA(s, e);
        result.addTransition(s, a.startState, 'e');
        result.addTransition(s, e, 'e');
        result.transitions.addAll(a.transitions);
        result.addTransition(a.finalState, e, 'e');
        return result;
    }

    static NFA buildNFA(String postfix) {
        Stack<NFA> stack = new Stack<>();
        for (char c : postfix.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                stack.push(buildSingle(c));
            } else if (c == '.') {
                NFA b = stack.pop();
                NFA a = stack.pop();
                stack.push(buildConcat(a, b));
            } else if (c == '|') {
                NFA b = stack.pop();
                NFA a = stack.pop();
                stack.push(buildUnion(a, b));
            } else if (c == '*') {
                stack.push(buildStar(stack.pop()));
            } else if (c == '+') {
                stack.push(buildPlus(stack.pop()));
            } else if (c == '?') {
                stack.push(buildQuestion(stack.pop()));
            }
        }
        return stack.pop();
    }

    static void printWelcome() {
        System.out.println("==========================================");
        System.out.println("        Regex to NFA Converter            ");
        System.out.println("==========================================");
        System.out.println("Supported operators:");
        System.out.println("  |   = union         e.g.  a|b");
        System.out.println("  *   = zero or more  e.g.  a*");
        System.out.println("  +   = one or more   e.g.  a+");
        System.out.println("  ?   = zero or one   e.g.  a?");
        System.out.println("  ()  = grouping      e.g.  (ab)*");
        System.out.println("==========================================");
    }

    static void printNFA(NFA nfa) {
        TreeSet<Integer> states = new TreeSet<>();
        states.add(nfa.startState);
        states.add(nfa.finalState);
        for (Transition t : nfa.transitions) {
            states.add(t.from);
            states.add(t.to);
        }
        System.out.println("\n==========================================");
        System.out.println("             NFA RESULT                   ");
        System.out.println("==========================================");
        System.out.print("States      : { ");
        for (int s : states) System.out.print("q" + s + " ");
        System.out.println("}");
        System.out.println("Start State : q" + nfa.startState);
        System.out.println("Accept State: q" + nfa.finalState);
        System.out.println("\n------------------------------------------");
        System.out.println("           Transition Table               ");
        System.out.println("------------------------------------------");
        System.out.printf("%-14s %-14s %-14s%n", "From State", "Input", "To State");
        System.out.println("------------------------------------------");
        for (Transition t : nfa.transitions) {
            String symbol = (t.symbol == 'e') ? "epsilon (e)" : String.valueOf(t.symbol);
            System.out.printf("%-14s %-14s %-14s%n",
                "q" + t.from, symbol, "q" + t.to);
        }
        System.out.println("==========================================\n");
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        printWelcome();

        while (true) {
            System.out.print("\nEnter regex (or 'exit' to quit): ");
            String regex = sc.next().trim();

            if (regex.equals("exit")) break;

            State.reset();

            String withConcat = addConcat(regex);
            String postfix    = toPostfix(withConcat);

            System.out.println("With concat : " + withConcat);
            System.out.println("Postfix     : " + postfix);

            NFA nfa = buildNFA(postfix);
            printNFA(nfa);
        }

        System.out.println("\nGoodbye!");
        sc.close();
    }
}
