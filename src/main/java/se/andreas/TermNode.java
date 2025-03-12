package se.andreas;

import java.util.ArrayList;

public class TermNode extends ASTNode {
    public TermNode(ArrayList<ASTNode> children) {
        for (ASTNode child : children) {
            addChild(child);
        }
    }

    @Override
    public String evaluate(String input) {
        // försök matcha alla barn mot input
        String remainingInput = input;
        boolean allMatched = true;
        StringBuilder result = new StringBuilder();
        for (ASTNode child : children){
            String childResult = child.evaluate(remainingInput);

            if (childResult.isEmpty()){
                allMatched = false;
                break;
            }
            result.append(childResult);
            remainingInput = remainingInput.substring(childResult.length());
        }
        if (allMatched){
            return result.toString();
        }


        // om ingen match hittas returnera en tom string
        return "";
    }


    @Override
    protected String getNodeDetails() {
        return " (term)";
    }
}
