package se.andreas;

public class CaptureNode extends ASTNode {
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
