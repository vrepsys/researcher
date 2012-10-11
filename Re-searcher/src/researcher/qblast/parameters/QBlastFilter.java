package researcher.qblast.parameters;

public enum QBlastFilter {

    LOW_COMPLEXITY("L"), MASK_LOOKUP("m");

    private String qBlastName;

    private QBlastFilter(String qBlastName) {
        this.qBlastName = qBlastName;
    }

    public String getQBlastName() {
        return qBlastName;
    }

}
