package se.andreas;

public class AnyNode extends ASTNode {
    @Override
    public String evaluate(String input) {
        // returnera det som står först i input
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
