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
        System.out.println("DEBUG " + endAtString);
        int endAtInt = input.indexOf(endAtString);
        int current = 0;
        // försök matcha så långt det går
        while (current < endAtInt) {
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
            current++;
        }


        return result.toString();
    }

    @Override
    protected String getNodeDetails() {
        return " (*, first child: repeat, second child: endAt)";
    }
}
