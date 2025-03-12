package se.andreas;

import java.util.ArrayList;

public class Parser {
    // tokens genererade av lexeraren
    private final ArrayList<Lexer.Token> tokens;
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
     * Kollar vi är i slutet av tokenlistan borde ha använt EOF token ist
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
     * Consume token om den matchar parametern, bör kanske bytas ut eller använda ngt annat i resterande kod
     *
     * @param type typen som jämförs med
     * @return Token som kosumerades eller null om inget konsumerades
     */
    private Lexer.Token consumeIfMatchesType(Lexer.Type type) {
        if (canPeek() && peek().type == type) {
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
            return new ExprNode(expression());
        } catch (RuntimeException e) {
            // logga fel
            System.err.println("Parse error: " + e.getMessage());
            return null;
        }
    }


    /**
     * En expression är en eller flera termer skiljda med 'Or'
     *
     * @return trädet i detta skede
     */
    private ASTNode expression() {
        // parsa första termen
        ASTNode node = term();

        // kapsla in termer

        // medans det finns ornoder att hämta
        while (consumeIfMatchesType(Lexer.Type.OR) != null) {
            ASTNode right = term();
            node = new OrNode(node, right);
        }

        // kolla om det finns ett \I
        if (consumeIfMatchesType(Lexer.Type.BACKSLASH_I) != null) {
            node = new CaseInsensitiveNode(node);
        }

        if (consumeIfMatchesType(Lexer.Type.BACKSLASH_O) != null) {
            consumeIfMatchesType(Lexer.Type.L_BRACE);
            Lexer.Token captureGroupToken = consumeIfMatchesType(Lexer.Type.NUMBER);
            consumeIfMatchesType(Lexer.Type.R_BRACE);
            assert captureGroupToken != null;
            int captureGroup = Integer.parseInt(captureGroupToken.content);
            node = new CaptureNode(node, captureGroup);
        }

        return node;
    }

    /**
     * En term är en eller flera faktorer
     *
     * @return trädet i detta skede
     */
    private ASTNode term() {
        // skapa concatnode
        ArrayList<ASTNode> factors = new ArrayList<>();
        // medans det finns faktorer
        while (canPeek() && (peek().type == Lexer.Type.CHAR || peek().type == Lexer.Type.ANY || peek().type == Lexer.Type.L_PAREN)) {

            ASTNode factor = factor();
            // kapsla in faktorer
            if (currentTypeIs(Lexer.Type.STAR)) {
                consumeIfMatchesType(Lexer.Type.STAR);
                ASTNode endAt = term();
                factor = new RepeatNode(factor, endAt);
            } else if (currentTypeIs(Lexer.Type.L_BRACE)) {
                consumeIfMatchesType(Lexer.Type.L_BRACE);
                Lexer.Token numberToken = consumeIfMatchesType(Lexer.Type.NUMBER);
                consumeIfMatchesType(Lexer.Type.R_BRACE);
                assert numberToken != null;
                int count = Integer.parseInt(numberToken.content);
                factor = new CountNode(factor, count);
            }
            factors.add(factor);
        }
        return new TermNode(factors);
    }

    /**
     * Parsa en faktor vilket är en char, grupp, stjärna etc.
     * @return trädet i detta skede
     */
    private ASTNode factor() {
        // Get the current token
        Lexer.Token token = peek();
        switch (token.type) {
            case CHAR:
                // behöver egentligen inte checka om den matchar typen för det vet vi redan
                // men det är ibland bra att vara övertydlig
                consumeIfMatchesType(Lexer.Type.CHAR);
                return new CharNode(token.content.charAt(0));
            case ANY:
                consumeIfMatchesType(Lexer.Type.ANY);
                return new AnyNode();
            case L_PAREN:
                consumeIfMatchesType(Lexer.Type.L_PAREN);
                ASTNode expr = expression();
                consumeIfMatchesType(Lexer.Type.R_PAREN);
                return new GroupNode(expr);
            default:
                // har vi kommet hit har något gått fel... eller så är tokensarna fel
                throw new RuntimeException("Unexpected token: " + token);
        }
    }
}
