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
        String endAtString = children.get(1).evaluate(input);
//        System.out.println("DEBUG " + endAtString);

        // ful lösning men den funkar
        double endAtIndex = Double.POSITIVE_INFINITY;
        if (!endAtString.isEmpty()) {
            endAtIndex = input.indexOf(endAtString);
        }
        // försök matcha så långt det går
        for (int current = 0; current < endAtIndex; current++) {
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
        result.append(endAtString);

        return result.toString();
    }

    @Override
    protected String getNodeDetails() {
        return " (*, first child: repeat, second child: endAt)";
    }
}
