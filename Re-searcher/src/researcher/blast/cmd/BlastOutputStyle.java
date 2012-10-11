package researcher.blast.cmd;

public enum BlastOutputStyle {
    // 0 pairwise (default)
    // 1 query-anchored showing identities
    // 2 query-anchored, no identities
    // 3 flat query-anchored, show identities
    // 4 flat query-anchored, no identities
    // 5 query-anchored, no identities and blunt ends
    // 6 flat query-anchored, no identities and blunt ends
    // 7 XML Blast output (not available for impala)
    // 8 tabular (not available for impala)
    // 9 tabular with comment lines (not available for impala)
    // 10 ASN.1 text (not available for impala or rpsblast)
    // 11 ASN.1 binary (not available for impala or rpsblast)
    PAIRWISE(0), XML(7);

    private int number;

    BlastOutputStyle(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
