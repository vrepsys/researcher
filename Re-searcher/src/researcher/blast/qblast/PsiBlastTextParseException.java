package researcher.blast.qblast;

public class PsiBlastTextParseException extends RuntimeException {

    private String text;

    public PsiBlastTextParseException() {
        super();
        // TODO Auto-generated constructor stub
    }

    public PsiBlastTextParseException(String message, String text) {
        super(message);
        this.text = text;
        // TODO Auto-generated constructor stub
    }

    public PsiBlastTextParseException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public PsiBlastTextParseException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public String getText() {
        return text;
    }

}
