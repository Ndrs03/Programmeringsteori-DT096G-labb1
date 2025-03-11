package se.andreas;

import java.util.ArrayList;

public class CaseInsensitiveNode extends ASTNode {
    public CaseInsensitiveNode(ASTNode operand) {
        addChild(operand);
    }

    /**
     * gå genom trädet rekursivt och omvandla CharNodes till lower case.
     *
     * @param node Roten till trädet
     * @return Ett nytt träd med lower case chars
     */
    private ASTNode makeCaseInsensitive(ASTNode node) {
        if (node instanceof CharNode charNode) {
            return new CharNode(Character.toLowerCase(charNode.value));
        } else {
            // ny lista för de modifierade barnen
            ArrayList<ASTNode> newChildren = new ArrayList<>();

            // iterera över barnen
            for (ASTNode child : node.getChildren()) {
                newChildren.add(makeCaseInsensitive(child));
            }

            // byt ut barnen till de modifierade
            node.getChildren().clear();
            node.getChildren().addAll(newChildren);

            return node;
        }
    }

    @Override
    public String evaluate(String input) {
        // konvertera input till lower case
        String lowercaseInput = input.toLowerCase();

        for (ASTNode child : getChildren()) {
            makeCaseInsensitive(child);
        }

        return children.get(0).evaluate(lowercaseInput);
    }

    @Override
    protected String getNodeDetails() {
        return " (case-insensitive)";
    }
}
