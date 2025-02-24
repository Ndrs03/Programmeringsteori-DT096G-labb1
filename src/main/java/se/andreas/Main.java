package se.andreas;

import java.util.ArrayList;
import java.util.Vector;


public class Main {
    public static void main(String[] args) {
        ArrayList<Lexer.Token> tokens = Lexer.lex("Hej");
        System.out.println("Tokens: " + tokens);

        Parser parser = new Parser(tokens, "Hejsan hoppsan");

        Parser.ASTNode parseTree = parser.parse();
        String result = parseTree.evaluate();

        System.out.println("Result: " + result);



    }
}


// #expressions är en term, och ett obestämt antal 0-n termer efter skiljda med ett logiskt eller
// EXPR = TERM, ( OR, TERM )*
//
// #En term är minst en faktor
// TERM = FACTOR+
//
// # faktor är en komponent som kan vara bokstäver, valfritt tecken, grupp, räknare eller repetition.
// FACTOR = CHAR+ | ANY | GROUP | COUNT | REPETITION | ESCAPE | CASE_INSENSITIVE | CAPTURE
//
// #En grupp är en term inom parentes
// GROUP = "(", EXPR, ")"
//
// # repition är en faktor följt av stjärna
// REPETITION = FACTOR, "*"
//
// # räknare är en faktor följt av en siffra
// COUNT = FACTOR, "{", DIGIT+, "}"
//
// # case okänslighet
// CASE_INSENSITIVE = FACTOR, "\I"
//
// # capture group
// CAPTURE = FACTOR, "\O{", DIGIT+, "}"
//
// # Escapa en char om man ex vill matcha en . så \.
// ESCAPE = "\\", CHAR
//
// OR = "+"
// ANY = "."
// CHAR = [a-zA-Z]
// DIGIT = [0-9]