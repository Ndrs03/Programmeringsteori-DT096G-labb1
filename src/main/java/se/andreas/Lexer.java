package se.andreas;

import java.util.ArrayList;
import java.util.Vector;

public class Lexer {
    public enum Type {
        Char,
        Any,
        Or,
        Star,
        LParen,
        RParen,
        LBrace,
        RBrace,
        BackslashI,
        BackslashO,
        Number
    }

    public static class Token {
        public final Type type;
        public final String content;

        public Token(Type t, String content) {
            this.type = t;
            this.content = content;
        }

        public String toString() {
            if (type == Type.Char) {
                return "Char<" + content + ">";
            } else if (type == Type.Number) {
                return "Number<" + content + ">";
            }
            return type.toString();
        }
    }


    public static ArrayList<Token> lex(String input) {
        ArrayList<Token> tokens = new ArrayList<>();
        char[] chars = input.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            switch (c) {
                case '.':
                    tokens.add(new Token(Type.Any, "."));
                    break;
                case '+':
                    tokens.add(new Token(Type.Or, "+"));
                    break;
                case '*':
                    tokens.add(new Token(Type.Star, "*"));
                    break;
                case '(':
                    tokens.add(new Token(Type.LParen, "("));
                    break;
                case ')':
                    tokens.add(new Token(Type.RParen, ")"));
                    break;
                case '{':
                    tokens.add(new Token(Type.LBrace, "{"));
                    break;
                case '}':
                    tokens.add(new Token(Type.RBrace, "}"));
                    break;
                case '\\':
                    // kolla om det finns något bakom \
                    if (i + 1 >= chars.length) {
                        throw new RuntimeException("Invalid escape char");
                    }
                    char next = chars[i + 1];
                    switch (next) {
                        case 'I':
                            tokens.add(new Token(Type.BackslashI, "\\I"));
                            i++; // \I är två steg
                            break;
                        case 'O':
                            tokens.add(new Token(Type.BackslashO, "\\O"));
                            i++;
                            break;
                        default:
                            // escaped chars ex. \.
                            tokens.add(new Token(Type.Char, String.valueOf(next)));
                            i++;
                            break;
                    }
                    break;
                default:
                    if (Character.isDigit(c)) {
                        StringBuilder numberBuilder = new StringBuilder();
                        while (i < chars.length && Character.isDigit(chars[i])) {
                            numberBuilder.append(chars[i]);
                            i++;
                        }
                        i--; // gå tillbaka ett steg, lite av ett fuskbygge
                        tokens.add(new Token(Type.Number, numberBuilder.toString()));
                    } else {
                        tokens.add(new Token(Type.Char, String.valueOf(c)));
                    }
                    break;
            }
        }
        return tokens;
    }
}
