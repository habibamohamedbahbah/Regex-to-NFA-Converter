/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.regextonfa;

/**
 *
 * @author MSC
 */
import java.util.*;

public class Regextonfa {

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

   
    static void printNFA(NFA nfa) {
        System.out.println("Start State: " + nfa.startState);
        System.out.println("Final State: " + nfa.finalState);

        System.out.println("Transitions:");
        System.out.println("From\tSymbol\tTo");

        for (Transition t : nfa.transitions) {
            System.out.println(t.from + "\t  " + t.symbol + "\t  " + t.to);
        }
    }

    static void printWelcome() {
        System.out.println("========");
        System.out.println("   Regex → NFA (Thompson)");
        System.out.println("====");
    }

    
  public static void main(String[] args) {

    Scanner sc = new Scanner(System.in);

    printWelcome();

    System.out.print("Enter symbol: ");
    char symbol = sc.next().charAt(0);

    int s = State.newState();
    int e = State.newState();

    NFA nfa = new NFA(s, e);
    nfa.addTransition(s, e, symbol);

    printNFA(nfa);
}
}

