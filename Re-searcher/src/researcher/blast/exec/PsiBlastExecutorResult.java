package researcher.blast.exec;

import java.util.ArrayList;
import java.util.List;

import researcher.blast.BlastTypes.HitDetails;

public class PsiBlastExecutorResult {

	private List<HitDetails> hits;

	private String errors;

	public PsiBlastExecutorResult(List<HitDetails> hits) {
		if (hits == null) throw new NullPointerException();
		this.hits = new ArrayList<HitDetails>(hits);
	}
	
	public PsiBlastExecutorResult(String error) {
		if (error == null) throw new NullPointerException();
		this.errors = error;
	}

	public String getErrors() {
		return errors;
	}

	public List<HitDetails> getHits() {
		return hits;
	}
	
	public boolean hasErrors(){
		if (errors != null) return true;
		return false;
	}

	
}
