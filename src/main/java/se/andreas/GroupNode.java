package se.andreas;

public class GroupNode extends ASTNode {
    public GroupNode(ASTNode expr) {
        addChild(expr);
    }

    @Override
    public String evaluate(String input) {
        // Enn gruppnod innehåller alltid bara ett barn en concatnod
        return children.get(0).evaluate(input);
    }

    @Override
    protected String getNodeDetails() {
        return " (group)";
    }
}
