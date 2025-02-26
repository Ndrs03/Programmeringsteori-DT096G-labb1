package se.andreas;

import java.util.ArrayList;

public class ConcatNode extends ASTNode {
    public ConcatNode(ArrayList<ASTNode> children) {
        for (ASTNode child : children) {
            addChild(child);
        }
    }

    @Override
    public String evaluate(String input) {
        StringBuilder result = new StringBuilder();
        String remainingInput = input;

        for (ASTNode child : children) {
            String childResult = child.evaluate(remainingInput);
            if (childResult.isEmpty()) {
                return ""; // If any child fails to match, the entire concatenation fails
            }
            result.append(childResult); // Append the matched part
            remainingInput = remainingInput.substring(childResult.length()); // Consume the matched part
        }

        return result.toString(); // Return the concatenated result
    }

    @Override
    protected String getNodeDetails() {
        return " (concat)";
    }
}
