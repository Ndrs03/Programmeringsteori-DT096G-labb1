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
        // försök att matcha mönstret mot input vid varje position i input
        for (int start = 0; start <= input.length(); start++) {
            StringBuilder result = new StringBuilder();
            String remainingInput = input.substring(start);

            boolean match = true;
            // kolla om varje barn i concat matchar vid den nuvarande positionen
            for (ASTNode child : children) {
                String childResult = child.evaluate(remainingInput);
                if (childResult.isEmpty()) {
                    // om ett barn inte matchar, börja på nästa position i input
                    match = false;
                    break;
                }
                // lägg till den matchande delen i result
                result.append(childResult);
                // ta bort den matchade delen från remaining
                remainingInput = remainingInput.substring(childResult.length());
            }

            if (match) {
                // om alla barnen matchar har concat matchat
                return result.toString();
            }
        }

        // om ingen match hittas returnera en tom string
        return "";
    }


    @Override
    protected String getNodeDetails() {
        return " (concat)";
    }
}
