package se.andreas;

public class ExprNode extends ASTNode {
    public ExprNode(ASTNode term) {
        addChild(term);
    }

    @Override
    public String evaluate(String input) {
        StringBuilder result = new StringBuilder();
        String remainingInput;
        for (int current = 0; current <= input.length(); current++) {
//            System.out.println("DEBUG string pos: "+ current);

            remainingInput = input.substring(current);

            // kolla om varje faktor matchar vid den nuvarande positionen
            ASTNode child = children.get(0);
            String childResult = child.evaluate(remainingInput);
            if (!childResult.isEmpty()) {
                result.append(childResult);
                // om vi matchar kan vi returnera
                return result.toString();
            }
        }

        return "";


    }

    @Override
    protected String getNodeDetails() {
        return " (expr)";
    }
}
