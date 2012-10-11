package researcher.qblast.parameters;

public enum QBlastFormatObject {

    ALIGNMENT("Alignment"), PSSM("PSSM"), TAXBLAST("TaxBlast"), BIOSEQ("Bioseq");

    private String qBlastName;

    private QBlastFormatObject(String qBlastName) {
        this.qBlastName = qBlastName;
    }

    public String getQBlastName() {
        return qBlastName;
    }

}
