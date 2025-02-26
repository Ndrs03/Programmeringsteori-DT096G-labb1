package se.andreas;

public class RepeatNode extends ASTNode {
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
