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
        // Try matching the pattern at every possible starting position in the input
        for (int start = 0; start <= input.length(); start++) {
            StringBuilder result = new StringBuilder();
            String remainingInput = input.substring(start);

            boolean match = true;
            for (ASTNode child : children) {
                String childResult = child.evaluate(remainingInput);
                if (childResult.isEmpty()) {
                    // If any child fails to match, move to the next starting position
                    match = false;
                    break;
                }
                // Append the matched part to the result
                result.append(childResult);
                // Consume the matched part from the remaining input
                remainingInput = remainingInput.substring(childResult.length());
            }

            if (match) {
                // If all children matched, return the result
                return result.toString();
            }
        }

        // If no match is found, return an empty string
        return "";
    }


    @Override
    protected String getNodeDetails() {
        return " (concat)";
    }
}
