package se.andreas;

public class RepeatNode extends ASTNode {
    public RepeatNode(ASTNode operand) {
        addChild(operand);
    }

    @Override
    public String evaluate(String input) {
        StringBuilder result = new StringBuilder();
        String remainingInput = input;

        // försök matcha så långt det går
        while (true) {
            // kolla resterande input mot barnet
            String operandResult = children.get(0).evaluate(remainingInput);
            if (operandResult.isEmpty()) {
                // om ingen match hittades sluta upprepa
                break;
            }
            // spara den matchade biten
            result.append(operandResult);
            // ta bort den matchade delen från återstående input
            remainingInput = remainingInput.substring(operandResult.length());
        }

        return result.toString();
    }

    @Override
    protected String getNodeDetails() {
        return " (*)";
    }
}
