package se.andreas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/*
checklista för eval:
Concat ok
Char ok
Any ok
repeat tror den funkar? todo "(.*) the war" ger ingen matchning men ".* the war" funkar korrekt
count ok
case insensitive todo kanske fel syntax, vid ex Love+(Hate)\\I gör wrappar allt. Funkar ganska bra nu, men är annat syntax, blir ist Love+(Hate\\I)
group ok
return group todo funkar ej

 */


public class Main {
    public static void main(String[] args) {
/*
        // kolla om det finns ett möster
        if (args.length < 1) {
            System.err.println("Usage: match <pattern> < input.txt");
            System.exit(1);
        }
        String pattern = args[0];
        System.out.println("Pattern: " + pattern);

        // läs från input stream <
        StringBuilder inputBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                inputBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error reading input: " + e.getMessage());
            System.exit(1);
        }

        String input = inputBuilder.toString().trim(); // Remove trailing newline
        System.out.println("Input:\n" + input);
*/

        // todo group funkar ej med repeat och count
        String pattern = "(Waterloo)*";
        String input = "Waterloo Waterloo I was defeated, you won the war Waterloo promise to love you for ever more Waterloo couldn't escape if I wanted to Waterloo knowing my fate is to be with you Waterloo finally facing my Waterloo";
//        String pattern = "a*b";
//        String input = "aaab";

        ArrayList<Lexer.Token> tokens = Lexer.lex(pattern);
        System.out.println(tokens);
        Parser parser = new Parser(tokens);
        ASTNode parseTree = parser.parse();
        System.out.println(parseTree);
        String result = parseTree.evaluate(input);

//        System.out.println(parseTree);

        if (!result.isEmpty()){
            System.out.println("\nPattern: " + pattern + "\nInput: " + input + "\nResult: " + result);
            System.exit(0);
        }
        System.exit(1);
    }
}


// #expressions är en term, och ett obestämt antal 0-n termer efter skiljda med ett logiskt eller
// EXPR = TERM, ( OR, TERM )*
//
// #En term är minst en faktor
// TERM = FACTOR+
//
// # faktor är en komponent som kan vara bokstäver, valfritt tecken, grupp, räknare eller repetition.
// FACTOR = CHAR+ | ANY+ | GROUP+ | COUNT+ | REPETITION+ | ESCAPE+ | todo CAPTURE????
//
// #En grupp är en EXPR inom parentes
// GROUP = "(", EXPR, ")"
//
// # repition är en faktor följt av stjärna
// REPETITION = FACTOR, "*"
//
// # räknare är en faktor följt av en siffra
// COUNT = FACTOR, "{", DIGIT+, "}"
//
// # case okänslighet EXPR följt av \I
// CASE_INSENSITIVE = EXPR, "\I"
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