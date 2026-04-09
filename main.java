import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

class NFA {
    int start;
    int accept;
    List<Transition> transitions = new ArrayList<>();

    // Non-static inner class
    class Transition {
        int from;
        char symbol;
        int to;

        Transition(int from, char symbol, int to) {
            this.from = from;
            this.symbol = symbol;
            this.to = to;
        }
    }

    static class Thompson {
        static int stateCount = 0;

        static int newState() {
            return stateCount++;
        }

        static NFA buildSingle(char c) {
            NFA nfa = new NFA();
            nfa.start = newState();
            nfa.accept = newState();
            // Use outer instance to create inner class object
            nfa.transitions.add(nfa.new Transition(nfa.start, c, nfa.accept));
            return nfa;
        }

        static NFA buildConcat(NFA nfa1, NFA nfa2) {
            NFA result = new NFA();
            result.start = nfa1.start;
            result.accept = nfa2.accept;

            // Add epsilon transition from nfa1.accept to nfa2.start
            nfa1.transitions.add(nfa1.new Transition(nfa1.accept, 'e', nfa2.start));

            result.transitions.addAll(nfa1.transitions);
            result.transitions.addAll(nfa2.transitions);

            return result;
        }

        static NFA buildUnion(NFA nfa1, NFA nfa2) {
            NFA result = new NFA();
            int start = newState();
            int accept = newState();

            result.start = start;
            result.accept = accept;

            result.transitions.add(result.new Transition(start, 'e', nfa1.start));
            result.transitions.add(result.new Transition(start, 'e', nfa2.start));
            result.transitions.add(result.new Transition(nfa1.accept, 'e', accept));
            result.transitions.add(result.new Transition(nfa2.accept, 'e', accept));

            result.transitions.addAll(nfa1.transitions);
            result.transitions.addAll(nfa2.transitions);

            return result;
        }

        static NFA buildStar(NFA nfa) {
            NFA result = new NFA();
            int start = newState();
            int accept = newState();

            result.start = start;
            result.accept = accept;

            result.transitions.add(result.new Transition(start, 'e', nfa.start));
            result.transitions.add(result.new Transition(start, 'e', accept));
            result.transitions.add(result.new Transition(nfa.accept, 'e', nfa.start));
            result.transitions.add(result.new Transition(nfa.accept, 'e', accept));

            result.transitions.addAll(nfa.transitions);

            return result;
        }

        static NFA buildNFA(String regex) {
            Stack<NFA> stack = new Stack<>();

            for (char c : regex.toCharArray()) {
                if (Character.isLetterOrDigit(c)) {
                    stack.push(buildSingle(c));
                } else if (c == '.') {
                    NFA nfa2 = stack.pop();
                    NFA nfa1 = stack.pop();
                    stack.push(buildConcat(nfa1, nfa2));
                } else if (c == '|') {
                    NFA nfa2 = stack.pop();
                    NFA nfa1 = stack.pop();
                    stack.push(buildUnion(nfa1, nfa2));
                } else if (c == '*') {
                    NFA nfa = stack.pop();
                    stack.push(buildStar(nfa));
                }
            }

            if (stack.size() != 1) {
                throw new IllegalArgumentException("Invalid regex expression");
            }

            return stack.pop();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        NFA var1 = NFA.Thompson.buildSingle('a');
        NFA var2 = NFA.Thompson.buildConcat(
                NFA.Thompson.buildSingle('a'),
                NFA.Thompson.buildSingle('b'));
        NFA var3 = NFA.Thompson.buildUnion(
                NFA.Thompson.buildSingle('a'),
                NFA.Thompson.buildSingle('b'));
        NFA var4 = NFA.Thompson.buildStar(NFA.Thompson.buildSingle('a'));

        System.out.println("SINGLE");
        print(var1);
        System.out.println("CONCAT");
        print(var2);
        System.out.println("UNION");
        print(var3);
        System.out.println("STAR");
        print(var4);
    }

    static void print(NFA nfa) {
        System.out.println("Start: " + nfa.start);
        System.out.println("Accept: " + nfa.accept);

        for (NFA.Transition t : nfa.transitions) {
            System.out.println(t.from + " --" + t.symbol + "--> " + t.to);
        }
        System.out.println();
    }
}