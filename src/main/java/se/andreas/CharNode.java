package se.andreas;

/**
 * Nod för varje tecken
 */
public class CharNode extends ASTNode {
    public final char value;

    public CharNode(char value) {
        this.value = value;
    }

    @Override
    public String evaluate(String input) {
        // kolla om value matchar den första positionen i input
        if (!input.isEmpty() && input.charAt(0) == value) {
            return String.valueOf(value);
        }
        // ingen matchning
        return "";
    }

    @Override
    protected String getNodeDetails() {
        return " ('" + value + "')";
    }
}
