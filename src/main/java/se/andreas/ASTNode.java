package se.andreas;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstrakt basklass för trädets noder
 */
public abstract class ASTNode {
    // Martin kallar denna för operands, men children låter roligare och funkar bättre i min hjärna
    protected List<ASTNode> children = new ArrayList<>();
    protected ASTNode parent;

    /**
     * Evaluerar parseträdet mot en input
     * @param input en sträng med texten som ska matchas mot
     * @return Returnerar en sträng med det första i input som matcher, om inget returneras en tom sträng
     */
    public abstract String evaluate(String input);

    /**
     * Lägg till ett barn till noden
     * @param node Barnet
     */
    protected void addChild(ASTNode node) {
        node.parent = this;
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
        // lägg till indentering
        stringBuilder.append("  ".repeat(Math.max(0, indentLevel)));
        // hämta klassens namn
        stringBuilder.append(this.getClass().getSimpleName());
        // hämta specifika detaljer
        stringBuilder.append(getNodeDetails());
        stringBuilder.append(" [\n");
        // lägg till barnen med en extra indentering
        for (ASTNode operand : children) {
            stringBuilder.append(operand.toStringHelper(indentLevel + 1)).append("\n");
        }
        // stäng noden i trädet
        stringBuilder.append("  ".repeat(Math.max(0, indentLevel)));
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

