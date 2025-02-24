package se.andreas;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    public final ArrayList<Lexer.Token> tokens;
    private int current = 0;
    public final String input;

    public Parser(ArrayList<Lexer.Token> tokens, String input) {
        this.tokens = tokens;
        this.input = input;
    }

    public ASTNode parse() {
        return expression();
    }

    private ASTNode expression() {
        ASTNode node = term();
        while (match(Lexer.Type.Or)) {
            ASTNode right = term();
            node = new OrNode(node, right);
        }
        return node;
    }

    private ASTNode term() {
        ASTNode node = factor();
        while (canPeek() && (peek().type == Lexer.Type.Char || peek().type == Lexer.Type.Any || peek().type == Lexer.Type.LParen)) {
            ASTNode nextFactor = factor();
            node = new ConcatNode(node, nextFactor);
        }
        return node;
    }

    private ASTNode factor() {
        if (!canPeek()) {
            throw new RuntimeException("Unexpected end of input");
        }
        Lexer.Token token = peek();
        switch (token.type) {
            case Char:
                consume(Lexer.Type.Char);
                return new CharNode(token.content.charAt(0));
            case Any:
                consume(Lexer.Type.Any);
                return new AnyNode();
            case LParen:
                consume(Lexer.Type.LParen);
                ASTNode expr = expression();
                consume(Lexer.Type.RParen);
                return new GroupNode(expr);
            case Star:
                ASTNode operand = factor();
                consume(Lexer.Type.Star);
                return new StarNode(operand);
            case LBrace:
                consume(Lexer.Type.LBrace);
                Lexer.Token numberToken = consume(Lexer.Type.Number);
                consume(Lexer.Type.RBrace);
                int count = Integer.parseInt(numberToken.content);
                return new CountNode(factor(), count);
            case BackslashI:
                consume(Lexer.Type.BackslashI);
                return new CaseInsensitiveNode(factor());
            case BackslashO:
                consume(Lexer.Type.BackslashO);
                consume(Lexer.Type.LBrace);
                Lexer.Token captureGroupToken = consume(Lexer.Type.Number);
                consume(Lexer.Type.RBrace);
                int captureGroup = Integer.parseInt(captureGroupToken.content);
                return new CaptureNode(factor(), captureGroup);
            default:
                throw new RuntimeException("Unexpected token: " + token);
        }
    }

    private boolean canPeek() {
        return current < tokens.size();
    }

    private Lexer.Token peek() {
        if (canPeek()) {
            return tokens.get(current);
        }
        throw new RuntimeException("Unexpected end of input");
    }

    private Lexer.Token advance() {
        if (canPeek()) {
            return tokens.get(current++);
        }
        throw new RuntimeException("Unexpected end of input");
    }

    private boolean match(Lexer.Type type) {
        if (canPeek() && peek().type == type) {
            advance();
            return true;
        }
        return false;
    }

    private Lexer.Token consume(Lexer.Type type) {
        if (match(type)) {
            return peek();
        }
        throw new RuntimeException("Expected token: " + type);
    }

    abstract static class ASTNode {
        protected List<ASTNode> operands = new ArrayList<>();

        public abstract String evaluate();

        public void addOperand(ASTNode node) {
            operands.add(node);
        }

        public List<ASTNode> getOperands() {
            return operands;
        }
    }

    class CharNode extends ASTNode {
        public final char value;

        public CharNode(char value) {
            this.value = value;
        }

        @Override
        public String evaluate() {
            if (!input.isEmpty() && input.charAt(0) == value) {
                return String.valueOf(value);
            }
            return "";
        }
    }

    class AnyNode extends ASTNode {
        @Override
        public String evaluate() {
            if (!input.isEmpty()) {
                return String.valueOf(input.charAt(0));
            }
            return "";
        }
    }

    class OrNode extends ASTNode {
        public OrNode(ASTNode left, ASTNode right) {
            addOperand(left);
            addOperand(right);
        }

        @Override
        public String evaluate() {
            String leftResult = operands.get(0).evaluate();
            String rightResult = operands.get(1).evaluate();
            if (!leftResult.isEmpty()) {
                return leftResult;
            } else if (!rightResult.isEmpty()) {
                return rightResult;
            }
            return "";
        }
    }

    class ConcatNode extends ASTNode {
        public ConcatNode(ASTNode left, ASTNode right) {
            addOperand(left);
            addOperand(right);
        }

        @Override
        public String evaluate() {
            String leftResult = operands.get(0).evaluate();
            if (leftResult.isEmpty()) {
                return "";
            }
            String rightResult = operands.get(1).evaluate();
            if (rightResult.isEmpty()) {
                return "";
            }
            return leftResult + rightResult;
        }
    }

    class GroupNode extends ASTNode {
        public GroupNode(ASTNode expr) {
            addOperand(expr);
        }

        @Override
        public String evaluate() {
            return operands.get(0).evaluate();
        }
    }

    class StarNode extends ASTNode {
        public StarNode(ASTNode operand) {
            addOperand(operand);
        }

        @Override
        public String evaluate() {
            StringBuilder result = new StringBuilder();
            String operandResult = operands.get(0).evaluate();
            while (!operandResult.isEmpty()) {
                result.append(operandResult);
                operandResult = operands.get(0).evaluate();
            }
            return result.toString();
        }
    }

    class CountNode extends ASTNode {
        private final int count;

        public CountNode(ASTNode operand, int count) {
            addOperand(operand);
            this.count = count;
        }

        @Override
        public String evaluate() {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < count; i++) {
                String operandResult = operands.get(0).evaluate();
                if (operandResult.isEmpty()) {
                    return "";
                }
                result.append(operandResult);
            }
            return result.toString();
        }
    }

    class CaseInsensitiveNode extends ASTNode {
        public CaseInsensitiveNode(ASTNode operand) {
            addOperand(operand);
        }

        @Override
        public String evaluate() {
            String operandResult = operands.get(0).evaluate();
            if (operandResult.isEmpty()) {
                return "";
            }
            return operandResult.toLowerCase();
        }
    }

    class CaptureNode extends ASTNode {
        private final int captureGroup;

        public CaptureNode(ASTNode operand, int captureGroup) {
            addOperand(operand);
            this.captureGroup = captureGroup;
        }

        @Override
        public String evaluate() {
            String operandResult = operands.get(0).evaluate();
            if (operandResult.isEmpty()) {
                return "";
            }
            // Implement capture group logic here
            return operandResult;
        }
    }
}
