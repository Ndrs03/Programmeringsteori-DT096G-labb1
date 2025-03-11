package se.andreas;

public class CountNode extends ASTNode {
    private final int count;

    public CountNode(ASTNode operand, int count) {
        addChild(operand);
        this.count = count;
    }

    @Override
    public String evaluate(String input) {
        StringBuilder result = new StringBuilder();
        String remainingInput = input;


        for (int i = 0; i < count; i++) {
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
        return " (count=" + count + ")";
    }
}
