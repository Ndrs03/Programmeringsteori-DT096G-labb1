package se.andreas;

public class OrNode extends ASTNode {
    public OrNode(ASTNode left, ASTNode right) {
        addChild(left);
        addChild(right);
    }

    @Override
    public String evaluate(String input) {
        // evaluera höger och vänster, ornoden borde kanske kunna ha fler barn
        String leftResult = children.get(0).evaluate(input);
        String rightResult = children.get(1).evaluate(input);
        // returnera ett av resultaten,
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
