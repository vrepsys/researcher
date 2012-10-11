package researcher.qblast.parameters;

public enum QBlastFormatType {

    XML("XML"), HTML("HTML"), TEXT("Text");

    private String qname;

    private QBlastFormatType(String qname) {
        this.qname = qname;
    }

    public String getQBlastName() {
        return qname;
    }
}
