package se.andreas;

public class RepeatNode extends ASTNode {
    public RepeatNode(ASTNode operand, ASTNode endAt) {
        addChild(operand);
        addChild(endAt);
    }

    @Override
    public String evaluate(String input) {
        StringBuilder result = new StringBuilder();
        String remainingInput = input;

        // försök matcha så långt det går
        while (!remainingInput.isEmpty()) {
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
            String endAts = children.get(1).evaluate(remainingInput);
            if (!endAts.isEmpty()) {
                return result.append(endAts).toString();
            }
        }

        return result.toString();
    }

    @Override
    protected String getNodeDetails() {
        return " (*, first child: repeat, second child: endAt)";
    }
}
