package researcher.blast;

public enum SearchLocation {

    NCBI("NCBI") {
        @Override
        public String toString() {
            // TODO Auto-generated method stub
            return "NCBI";
        }
    },
    LOCAL_SERVER("LOCAL SERVER") {
        @Override
        public String toString() {
            // TODO Auto-generated method stub
            return "LOCAL";
        }
    };

    private String label;

    private SearchLocation(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
