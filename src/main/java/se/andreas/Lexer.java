package se.andreas;

import java.util.ArrayList;

public class Lexer {
    public enum Type {
        CHAR,
        ANY,
        OR,
        STAR,
        L_PAREN,
        R_PAREN,
        L_BRACE,
        R_BRACE,
        BACKSLASH_I,
        BACKSLASH_O,
        NUMBER
    }

    public static class Token {
        public final Type type;
        public final String content;

        public Token(Type t, String content) {
            this.type = t;
            this.content = content;
        }

        public String toString() {
            if (type == Type.CHAR) {
                return "Char<" + content + ">";
            } else if (type == Type.NUMBER) {
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
                    tokens.add(new Token(Type.ANY, "."));
                    break;
                case '+':
                    tokens.add(new Token(Type.OR, "+"));
                    break;
                case '*':
                    tokens.add(new Token(Type.STAR, "*"));
                    break;
                case '(':
                    tokens.add(new Token(Type.L_PAREN, "("));
                    break;
                case ')':
                    tokens.add(new Token(Type.R_PAREN, ")"));
                    break;
                case '{':
                    tokens.add(new Token(Type.L_BRACE, "{"));
                    break;
                case '}':
                    tokens.add(new Token(Type.R_BRACE, "}"));
                    break;
                case '\\':
                    // kolla om det finns något bakom \
                    if (i + 1 >= chars.length) {
                        throw new RuntimeException("Invalid escape char");
                    }
                    char next = chars[i + 1];
                    switch (next) {
                        case 'I':
                            tokens.add(new Token(Type.BACKSLASH_I, "\\I"));
                            i++; // \I är två steg
                            break;
                        case 'O':
                            tokens.add(new Token(Type.BACKSLASH_O, "\\O"));
                            i++;
                            break;
                        default:
                            // escaped chars ex. \.
                            tokens.add(new Token(Type.CHAR, String.valueOf(next)));
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
                        tokens.add(new Token(Type.NUMBER, numberBuilder.toString()));
                    } else {
                        tokens.add(new Token(Type.CHAR, String.valueOf(c)));
                    }
                    break;
            }
        }
        return tokens;
    }
}
