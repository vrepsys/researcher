package researcher.blast.cmd;

import java.util.List;

import researcher.blast.BlastTypes;
import researcher.blast.BlastTypes.HitDetails;


public class PsiBlastResult {

    private String errors;

    private List<HitDetails> hitDetails;

    public PsiBlastResult(List hitList, String errors) {
        this.errors = errors;
    }

    public PsiBlastResult(String errors) {
        this.errors = errors;
    }

    public PsiBlastResult(List<HitDetails> hitList) {
        hitDetails = hitList;
    }

    public List<HitDetails> getHitDetails() {
        return hitDetails;
    }

    public String getErrors() {
        if (hasErrors())
            return errors;
        else
            return null;
    }

    public boolean hasErrors() {
        return (errors != null && (errors.contains("error") || errors.contains("ERROR") || hitDetails == null));
    }

    public String getWarnings() {
        if (hasErrors())
            return null;
        if (hasWarnings())
            return errors;
        else
            return null;
    }

    public boolean hasWarnings() {
        if (hasErrors())
            return false;
        return (errors != null && !errors.trim().equals(""));
    }

}
