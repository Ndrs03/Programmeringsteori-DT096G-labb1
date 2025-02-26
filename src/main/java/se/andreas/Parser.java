package se.andreas;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    // tokens genererade av lexeraren
    public final ArrayList<Lexer.Token> tokens;
    // nuvarande position i tokenslistan
    private int current = 0;

    /**
     * Konstruktor som tar listan med tokens
     *
     * @param tokens lista av tokens som kommer från klassen Lexer
     */
    public Parser(ArrayList<Lexer.Token> tokens) {
        this.tokens = tokens;
    }

    // #######################################################################
    // # Hjälpfunktioner #####################################################
    // #######################################################################

    /**
     * Kollar vi är i slutet av tokenlistan
     *
     * @return En bool för svaret
     */
    private boolean canPeek() {
        return current < tokens.size();
    }

    /**
     * Hämta nuvarande token
     *
     * @return Tokenen
     */
    private Lexer.Token peek() {
        if (canPeek()) {
            return tokens.get(current);
        }
        throw new RuntimeException("Unexpected end of input");
    }

    /**
     * Gå framåt i listan och returnera den token som vi gick från
     *
     * @return Token
     */
    private Lexer.Token advance() {
        if (canPeek()) {
            return tokens.get(current++);
        }
        throw new RuntimeException("Unexpected end of input");
    }

    /**
     * Consume token om den matchar parametern
     *
     * @param type typen som jämförs med
     * @return Token som kosumerades eller null om inget konsumerades
     */
    private Lexer.Token consumeIfMatchesType(Lexer.Type type) {
//        System.out.println("About to consume: " + peek());
        if (canPeek() && peek().type == type) {
//            System.out.println("Returning: " + tokens.get(current-1));
            Lexer.Token currentToken = tokens.get(current);
            advance();
            return currentToken;
        }
        return null;
    }

    private boolean currentTypeIs(Lexer.Type type){
        return canPeek() && peek().type == type;
    }

    // #######################################################################
    // # Bygg parseträd ######################################################
    // #######################################################################

    /**
     * Startpunkten i parsningen
     *
     * @return Den översta noden i parseträdet
     */
    public ASTNode parse() {
        try {
            // starta parsningen
            return expression();
        } catch (RuntimeException e) {
            // logga fel
            System.err.println("Parse error: " + e.getMessage());
            return null;
        }
    }

    // består av en eller flera termenr skiljda med or

    /**
     * En expression är en eller flera termer skiljda med 'Or'
     *
     * @return trädet i detta skede
     */
    private ASTNode expression() {
        // parsa första termen
        ASTNode node = term();

        // kolla om det finns ett \I
        if (consumeIfMatchesType(Lexer.Type.BackslashI) != null) {
            node = new CaseInsensitiveNode(node);
        }

        if (consumeIfMatchesType(Lexer.Type.Or) != null) {
            ASTNode right = term();
            node = new OrNode(node, right);
        }

        if (consumeIfMatchesType(Lexer.Type.BackslashO) != null) {
            // Consume the capture group token and parse the capture group number
            consumeIfMatchesType(Lexer.Type.BackslashO);
            consumeIfMatchesType(Lexer.Type.LBrace);
            Lexer.Token captureGroupToken = consumeIfMatchesType(Lexer.Type.Number);
            consumeIfMatchesType(Lexer.Type.RBrace);
            assert captureGroupToken != null;
            int captureGroup = Integer.parseInt(captureGroupToken.content);
            node = new CaptureNode(node, captureGroup);
        }

        return node;
    }

    // Parse a term, which is a sequence of factors

    /**
     * En term är en eller flera faktorer
     *
     * @return trädet i detta skede
     */
    private ASTNode term() {
        // skapa concatnode
        ArrayList<ASTNode> factors = new ArrayList<>();
        // While there are more factors (characters, any, or groups), parse them
        while (canPeek() && (peek().type == Lexer.Type.Char || peek().type == Lexer.Type.Any || peek().type == Lexer.Type.LParen)) {
            // lägg till barn i concat
            ASTNode factor = factor();
            // kolla tecknet efter för att hitta * eller {}
            if (currentTypeIs(Lexer.Type.Star)) {
                consumeIfMatchesType(Lexer.Type.Star);
                // Wrap the node in a RepeatNode
                factor = new RepeatNode(factor);
            } else if (currentTypeIs(Lexer.Type.LBrace)) {
                consumeIfMatchesType(Lexer.Type.LBrace);
                Lexer.Token numberToken = consumeIfMatchesType(Lexer.Type.Number);
                consumeIfMatchesType(Lexer.Type.RBrace);
                assert numberToken != null;
                int count = Integer.parseInt(numberToken.content);
                factor = new CountNode(factor, count);
            }
            // Create a ConcatNode to represent concatenation
            factors.add(factor);
        }
        return new ConcatNode(factors);
    }

    // Parse a factor, which can be a character, any character, group, etc.

    /**
     * Parsa en faktor vilket är en char, grupp, stjärna etc.
     * @return trädet i detta skede
     */
    private ASTNode factor() {
        // Get the current token
        Lexer.Token token = peek();
        // Determine the type of factor based on the token type
        switch (token.type) {
            case Char:
                // behöver egentligen inte checka om den matchar typen för det vet vi redan
                // men det är ibland bra att vara övertydlig
                consumeIfMatchesType(Lexer.Type.Char);
                return new CharNode(token.content.charAt(0));
            case Any:
                consumeIfMatchesType(Lexer.Type.Any);
                return new AnyNode();
            case LParen:
                consumeIfMatchesType(Lexer.Type.LParen);
                ASTNode expr = expression();
                consumeIfMatchesType(Lexer.Type.RParen);
                return new GroupNode(expr);
            default:
                // har vi kommet hit har något gått fel... eller så är tokensarna fel
                throw new RuntimeException("Unexpected token: " + token);
        }
    }


    // #######################################################################
    // # Klasser för trädbygge ###############################################
    // #######################################################################
    // ((KANSKE LÄGGA I SEPARAT FIL?))

    /**
     * Abstrakt basklass för trädets noder
     */
    abstract static class ASTNode {
        // Martin kallar denna för operands, men children låter roligare och funkar bättre i min hjärna
        protected List<ASTNode> children = new ArrayList<>();

        /**
         * Evaluerar parseträdet mot en input
         * @param input en sträng med texten som ska matchas mot
         * @return Returnerar en sträng med det som matcher, om inget returneras en tom sträng
         */
        public abstract String evaluate(String input);

        /**
         * Lägg till ett barn till noden
         * @param node Barnet
         */
        public void addChild(ASTNode node) {
            children.add(node);
        }

        /**
         * Hämta alla barn
         * @return Barnen
         */
        public List<ASTNode> getChildren() {
            return children;
        }


        /**
         * Hjälpfunktion för att debugga och visualisera parseträdet
         * @return en strängrepresentation av trädet
         */
        @Override
        public String toString() {
            return toStringHelper(0); // starta utan indentering
        }

        /**
         * Hjälpfunktion för att indentera och skapa toString returen
         * @param indentLevel hur många indenteringar noden i klassen ska ha
         * @return Den färdiga strängen
         */
        private String toStringHelper(int indentLevel) {
            StringBuilder stringBuilder = new StringBuilder();
            // Add indentation
            for (int i = 0; i < indentLevel; i++) {
                stringBuilder.append("  ");
            }
            // Add node type
            stringBuilder.append(this.getClass().getSimpleName());
            // Add node-specific details
            stringBuilder.append(getNodeDetails());
            stringBuilder.append(" [\n");
            // Add child nodes with increased indentation
            for (ASTNode operand : children) {
                stringBuilder.append(operand.toStringHelper(indentLevel + 1)).append("\n");
            }
            // Close the node
            for (int i = 0; i < indentLevel; i++) {
                stringBuilder.append("  ");
            }
            stringBuilder.append("]");
            return stringBuilder.toString();
        }

        /**
         * Metod för att hämta info om varje ASTNode, denna bör överlagras med nodnamnet
         * @return nodens namn
         */
        protected String getNodeDetails() {
            return "";
        }
    }

    // Node representing concatenation
    class ConcatNode extends ASTNode {
        public ConcatNode(ArrayList<ASTNode> children) {
            for (ASTNode child : children) {
                addChild(child);
            }
        }

        @Override
        public String evaluate(String input) {
            StringBuilder result = new StringBuilder();
            String remainingInput = input;

            for (ASTNode child : children) {
                String childResult = child.evaluate(remainingInput);
                if (childResult.isEmpty()) {
                    return ""; // If any child fails to match, the entire concatenation fails
                }
                result.append(childResult); // Append the matched part
                remainingInput = remainingInput.substring(childResult.length()); // Consume the matched part
            }

            return result.toString(); // Return the concatenated result
        }

        @Override
        protected String getNodeDetails() {
            return " (concat)";
        }
    }

    /**
     * Nod för varje tecken
     */
    class CharNode extends ASTNode {
        public final char value;

        public CharNode(char value) {
            this.value = value;
        }

        @Override
        public String evaluate(String input) {
            if (!input.isEmpty() && input.charAt(0) == value) {
                return String.valueOf(value); // Return the matched character
            }
            return ""; // No match
        }

        @Override
        protected String getNodeDetails() {
            return " ('" + value + "')";
        }
    }

    // Node representing any character
    class AnyNode extends ASTNode {
        @Override
        public String evaluate(String input) {
            // Evaluate any character
            if (!input.isEmpty()) {
                return String.valueOf(input.charAt(0));
            }
            return "";
        }

        @Override
        protected String getNodeDetails() {
            return " (.)";
        }
    }

    // Node representing an 'or' operation
    class OrNode extends ASTNode {
        public OrNode(ASTNode left, ASTNode right) {
            addChild(left);
            addChild(right);
        }

        @Override
        public String evaluate(String input) {
            // Evaluate the left and right operands
            String leftResult = children.get(0).evaluate(input);
            String rightResult = children.get(1).evaluate(input);
            // Return success if either operand succeeds
            if (!leftResult.isEmpty()) {
                return leftResult;
            } else if (!rightResult.isEmpty()) {
                return rightResult;
            }
            return "";
        }

        @Override
        protected String getNodeDetails() {
            return " (+)";
        }
    }


    // Node representing a group (expression in parentheses)
    class GroupNode extends ASTNode {
        public GroupNode(ASTNode expr) {
            addChild(expr);
        }

        @Override
        public String evaluate(String input) {
            // Evaluate the expression inside the group
            return children.get(0).evaluate(input);
        }

        @Override
        protected String getNodeDetails() {
            return " (group)";
        }
    }

    // Node representing zero or more occurrences
    class RepeatNode extends ASTNode {
        public RepeatNode(ASTNode operand) {
            addChild(operand);
        }

        @Override
        public String evaluate(String input) {
            // Evaluate the operand zero or more times
            StringBuilder result = new StringBuilder();
            String operandResult = children.get(0).evaluate(input);
            while (!operandResult.isEmpty()) {
                result.append(operandResult);
                operandResult = children.get(0).evaluate(input);
            }
            return result.toString();
        }

        @Override
        protected String getNodeDetails() {
            return " (*)";
        }
    }

    // Node representing a specific number of occurrences
    class CountNode extends ASTNode {
        private final int count;

        public CountNode(ASTNode operand, int count) {
            addChild(operand);
            this.count = count;
        }

        @Override
        public String evaluate(String input) {
            // Evaluate the operand a specific number of times
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < count; i++) {
                String operandResult = children.get(0).evaluate(input);
                if (operandResult.isEmpty()) {
                    return "";
                }
                result.append(operandResult);
            }
            return result.toString();
        }

        @Override
        protected String getNodeDetails() {
            return " (count=" + count + ")";
        }
    }

    // Node representing case-insensitive matching
    class CaseInsensitiveNode extends ASTNode {
        public CaseInsensitiveNode(ASTNode operand) {
            addChild(operand);
        }

        @Override
        public String evaluate(String input) {
            // Evaluate the operand and convert the result to lowercase
            String operandResult = children.get(0).evaluate(input);
            if (operandResult.isEmpty()) {
                return "";
            }
            return operandResult.toLowerCase();
        }

        @Override
        protected String getNodeDetails() {
            return " (case-insensitive)";
        }
    }

    // Node representing a capture group
    class CaptureNode extends ASTNode {
        private final int captureGroup;

        public CaptureNode(ASTNode operand, int captureGroup) {
            addChild(operand);
            this.captureGroup = captureGroup;
        }

        @Override
        public String evaluate(String input) {
            // Evaluate the operand and handle capture group logic
            String operandResult = children.get(0).evaluate(input);
            if (operandResult.isEmpty()) {
                return "";
            }
            // Implement capture group logic here
            return operandResult;
        }

        @Override
        protected String getNodeDetails() {
            return " (capture=" + captureGroup + ")";
        }
    }
}
