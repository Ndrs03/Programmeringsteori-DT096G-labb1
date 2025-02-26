package se.andreas;

public class CountNode extends ASTNode {
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
