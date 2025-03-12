package se.andreas;

public class GroupNode extends ASTNode {
    public GroupNode(ASTNode expr) {
        addChild(expr);
    }

    @Override
    public String evaluate(String input) {
        // En grupnod innehåller alltid bara ett barn en exprnod
        return children.get(0).evaluate(input);
    }

    @Override
    protected String getNodeDetails() {
        return " (group)";
    }
}
