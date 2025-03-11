package se.andreas;

public class CaptureNode extends ASTNode {
    private final int captureGroup;

    public CaptureNode(ASTNode operand, int captureGroup) {
        addChild(operand);
        this.captureGroup = captureGroup;
    }

    @Override
    public String evaluate(String input) {
        // todo implementera
        return "";
    }

    @Override
    protected String getNodeDetails() {
        return " (capture=" + captureGroup + ")";
    }
}
