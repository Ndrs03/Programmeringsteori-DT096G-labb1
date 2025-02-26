package se.andreas;

public class CaseInsensitiveNode extends ASTNode {
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
