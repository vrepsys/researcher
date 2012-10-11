package researcher.blast.cmd;

import researcher.beans.BlastQuery;
import researcher.beans.Configuration;
import researcher.beans.SecondaryBlastQuery;

public class PsiBlastCommandFactory {

   private static int getMaxNumberOfHits(BlastQuery query) {
      Integer maxNumberOfHits = query.getMaxNumberOfHits();
      return (maxNumberOfHits == null) ? 10000 : maxNumberOfHits;
   }
   
    public static PsiBlastCommand construct(BlastQuery query, String databasePath,
            Configuration conf) {
        PsiBlastCommand cmd = new PsiBlastCommand(conf.getPsiBlastCommand(), query
                .getSequenceName(), query.getSequence(), databasePath);
        
        cmd.setCompositionBasedStatistics(query.getCompositionalAdjustments().getPsiblastId());
        cmd.setEvalue(query.getEvalue());
        cmd.setNumberOfIterations(query.getIterations());
        cmd.setNumberOfProcessorsToUse(conf.getProcessorsToUse());
        cmd.setOutputStyle(BlastOutputStyle.XML);
        cmd.setCostToExtendAGap(query.getCostToExtendAGap());
        cmd.setCostToOpenAGap(query.getCostToOpenAGap());
        cmd.setEvalueMultipass(query.getEvalueMultipass());
        cmd.setMatrix(query.getMatrix());
        cmd.setWordSize(query.getWordSize());
        cmd.setOtherCommandLineOptions(query.getCommandLineOptions());
        cmd.setLowComplexity(query.isLowComplexityFilter());
        cmd.setMaskForLookup(query.isMaskForLookupTableOnlyFilter());
        
        Integer maxNumberOfHits = getMaxNumberOfHits(query);
        
        cmd.setDescriptionsToShow(maxNumberOfHits);
        cmd.setAlignmentsToShow(maxNumberOfHits);
        return cmd;
    }

    public static PsiBlastCommand construct(BlastQuery query, Configuration conf) {
        return construct(query, query.getDatabasePath(), conf);
    }

    public static PsiBlastCommand constructPrimary(BlastQuery query, Configuration conf) {
        PsiBlastCommand cmd = construct(query, query.getDatabasePath(), conf);
        // conf.getCheckPointFileName()
        // if one psiblast server is used for multiple Re-searcher's
        // unique temporary files are required
        cmd.setCheckpointFileOutput(conf.generateCheckPointFilePathName());
        return cmd;
    }

    public static PsiBlastCommand constructSecondary(BlastQuery query, Configuration conf, String checkpointFile) {
        SecondaryBlastQuery q2 = query.getSecondaryQuery();
        PsiBlastCommand cmd = new PsiBlastCommand(conf.getPsiBlastCommand(), query
                .getSequenceName(), query.getSequence(), q2.getDatabasePath());
        cmd.setCompositionBasedStatistics(q2.getCompositionalAdjustments().getPsiblastId());
        cmd.setEvalue(q2.getEvalue());
        cmd.setNumberOfIterations(1);
        cmd.setNumberOfProcessorsToUse(conf.getProcessorsToUse());
        cmd.setOutputStyle(BlastOutputStyle.XML);
        cmd.setCostToExtendAGap(q2.getCostToExtendAGap());
        cmd.setCostToOpenAGap(q2.getCostToOpenAGap());
        cmd.setMatrix(q2.getMatrix());
        cmd.setWordSize(q2.getWordSize());
        cmd.setLowComplexity(q2.isLowComplexityFilter());
        cmd.setMaskForLookup(q2.isMaskForLookupTableOnlyFilter());
        cmd.setOtherCommandLineOptions(q2.getCommandLineOptions());
        
        Integer maxNumberOfHits = getMaxNumberOfHits(query);
        
        cmd.setDescriptionsToShow(maxNumberOfHits);
        cmd.setAlignmentsToShow(maxNumberOfHits);
        
        cmd.setCheckpointFileInput(checkpointFile);
        return cmd;
    }
    
}
