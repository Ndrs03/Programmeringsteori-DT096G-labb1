package se.andreas;

import java.util.ArrayList;

/*
checklista för eval:
Concat ok?
Char ok
Any ok
repeat ok?
count ok
case insensitive ok todo returnera med orginalveralisiring
group ok

 */


public class Main {
    public static void main(String[] args) {
        // TODO: input funkar ej med siffermönster
        String pattern = ".*";
        String input = "Waterloo I was defeated, you won the war Waterloo promise to love you for ever more Waterloo couldn't escape if I wanted to Waterloo knowing my fate is to be with you Waterloo finally facing my Waterloo";

        ArrayList<Lexer.Token> tokens = Lexer.lex(pattern);
        Parser parser = new Parser(tokens);
        ASTNode parseTree = parser.parse();
        System.out.println(parseTree);
        String result = parseTree.evaluate(input);

//        System.out.println(parseTree);

        System.out.println("\nPattern: " + pattern + "\nInput: " + input + "\nResult: " + result);



    }
}


// #expressions är en term, och ett obestämt antal 0-n termer efter skiljda med ett logiskt eller
// EXPR = TERM, ( OR, TERM )*
//
// #En term är minst en faktor
// TERM = FACTOR+
//
// # faktor är en komponent som kan vara bokstäver, valfritt tecken, grupp, räknare eller repetition.
// FACTOR = CHAR+ | ANY | GROUP | COUNT | REPETITION | ESCAPE | CAPTURE
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
// CASE_INSENSITIVE = EXPR, "\I" | GROUP, "\I"
//
// # capture group
// CAPTURE = EXPR, "\O{", DIGIT+, "}"
//
// # Escapa en char om man ex vill matcha en . så \.
// ESCAPE = "\\", CHAR
//
// OR = "+"
// ANY = "."
// CHAR = [a-zA-Z]
// DIGIT = [0-9]