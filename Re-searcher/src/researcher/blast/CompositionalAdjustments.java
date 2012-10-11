package researcher.blast;

public enum CompositionalAdjustments {
    NO_AJUSTMENT("F", "No adjustment"), 
    COMPOSITION_BASED_STATISTICS("1", "Composition-based statistics"), 
    CONDITIONAL_COMPOSITIONAL("2", "Conditional compositional score matrix adjustment"),
    UNIVERSAL_COMPOSITIONAL("3", "Universal compositional score matrix adjustment");

    private String psiblastId;

    private String stringRepresentation;

    private CompositionalAdjustments(String psiblastId, String stringRepresentation) {
        this.psiblastId = psiblastId;
        this.stringRepresentation = stringRepresentation;
    }

    public String getPsiblastId() {
        return psiblastId;
    }

    public String getPsiblastName() {
        return stringRepresentation;
    }

}
