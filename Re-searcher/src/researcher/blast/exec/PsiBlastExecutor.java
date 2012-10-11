package researcher.blast.exec;

import java.util.List;

import researcher.beans.BlastQuery;


public interface PsiBlastExecutor {

    public PsiBlastExecutorResult execute(BlastQuery query);

    public List<String> getFastas(List<String> ids, String database);

}
