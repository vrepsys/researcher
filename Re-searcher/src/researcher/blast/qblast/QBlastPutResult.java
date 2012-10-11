package researcher.blast.qblast;

public class QBlastPutResult {

    private String requestId;

    private int requestTimeOfExecution;

    private String resultsPageTarget;

    private String formatPageTarget;

    public String getFormatPageTarget() {
        return formatPageTarget;
    }

    public void setFormatPageTarget(String formatPageTarget) {
        this.formatPageTarget = formatPageTarget;
    }

    public String getResultsPageTarget() {
        return resultsPageTarget;
    }

    public void setResultsPageTarget(String resultsPageTarget) {
        this.resultsPageTarget = resultsPageTarget;
    }

    public QBlastPutResult(String requestId, int requestTimeOfExecution) {
        if (requestId == null)
            throw new NullPointerException();
        this.requestId = requestId;
        this.requestTimeOfExecution = requestTimeOfExecution;
    }

    public String getRequestId() {
        return requestId;
    }

    public int getRequestTimeOfExecution() {
        return requestTimeOfExecution;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[requestId=" + requestId + ", requestTimeOfExecution="
                + requestTimeOfExecution + ", formatPageTarget=" + formatPageTarget
                + ", resultPageTarget=" + resultsPageTarget + "]";
    }

}
