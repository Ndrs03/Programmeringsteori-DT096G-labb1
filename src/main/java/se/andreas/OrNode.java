package se.andreas;

public class OrNode extends ASTNode {
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
