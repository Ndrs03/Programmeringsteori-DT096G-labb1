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
}
