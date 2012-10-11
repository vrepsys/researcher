package researcher.blast.qblast;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import researcher.beans.BlastQuery;
import researcher.beans.SecondaryBlastQuery;
import researcher.qblast.parameters.Params;
import researcher.qblast.parameters.QBlastFilter;
import researcher.utils.SeqUtil;


public class QBlastCommandFactory {
   
   static DecimalFormat evalueFormat = new DecimalFormat("#################.#################", new DecimalFormatSymbols(Locale.US));
   
   private static int getMaxNumberOfHits(BlastQuery query) {
      Integer maxNumberOfHits = query.getMaxNumberOfHits();
      return (maxNumberOfHits == null) ? 1000 : maxNumberOfHits;
   }

   public static QBlastCommand constructQBlastCommand(BlastQuery query, List<Parameter> params) {
        QBlastCommand cmd = new QBlastCommand(params);
        cmd.setParameterVNN(Params.COMPOSITION_BASED_STATISTICS, query
                .getCompositionalAdjustments().getPsiblastId().toString());
        cmd.setParameterVNN(Params.EXPECT, evalueFormat.format(query.getEvalue()));
        String gapCosts = query.getCostToOpenAGap() + " " + query.getCostToExtendAGap();
        cmd.setParameterVNN(Params.GAPCOSTS, gapCosts);
        String seq = SeqUtil.getFasta(query.getSequenceName(), query.getSequence());
        cmd.setParameterVNN(Params.QUERY, seq);
        
        cmd.setParameterVNN(Params.I_THRESH, evalueFormat.format(query.getEvalueMultipass()));
        cmd.setParameterVNN(Params.MATRIX_NAME, query.getMatrix().toString());
        cmd.setParameterVNN(Params.WORD_SIZE, String.valueOf(query.getWordSize()));
        cmd.setParameterVNN(Params.OTHER_ADVANCED, query.getCommandLineOptions());
        cmd.setParameterVNN(Params.DATABASE, query.getDatabasePath());
        
        cmd.removeParameter(Params.FILTER);        
        if (query.isLowComplexityFilter()) {
            cmd.addParameter(Params.FILTER, QBlastFilter.LOW_COMPLEXITY.getQBlastName());
        }
        if (query.isMaskForLookupTableOnlyFilter()) {
            cmd.addParameter(Params.FILTER, QBlastFilter.MASK_LOOKUP.getQBlastName());
        }
        if (query.getPssm() != null) {
            cmd.addParameter(Params.PSSM, query.getPssm());
        }
        if (query.getPhiPattern() != null) {
            cmd.setParameter(Params.PHI_PATTERN, query.getPhiPattern());
        }
        
        Integer maxNumberOfHits = getMaxNumberOfHits(query);
        
        cmd.setMaxNumberOfHits(maxNumberOfHits);
        cmd.setParameter(Params.ALIGNMENTS, String.valueOf(maxNumberOfHits));
        cmd.setParameter(Params.MAX_NUM_SEQ, String.valueOf(maxNumberOfHits));
        cmd.setParameter(Params.DESCRIPTIONS, "0");
        return cmd;
    }

    public static QBlastCommand constructQBlastCommandForSecondaryQuery(BlastQuery query,
            List<Parameter> params, String pssm) {
        SecondaryBlastQuery q2 = query.getSecondaryQuery();
        QBlastCommand cmd = new QBlastCommand(params);
        cmd.setParameterVNN(Params.COMPOSITION_BASED_STATISTICS, q2.getCompositionalAdjustments()
                .getPsiblastId().toString());
        cmd.setParameterVNN(Params.EXPECT, evalueFormat.format(q2.getEvalue()));
        String gapCosts = q2.getCostToOpenAGap() + " " + q2.getCostToExtendAGap();
        cmd.setParameterVNN(Params.GAPCOSTS, gapCosts);
        cmd.setParameterVNN(Params.MATRIX_NAME, q2.getMatrix().toString());
        cmd.setParameterVNN(Params.WORD_SIZE, String.valueOf(q2.getWordSize()));
        cmd.setParameterVNN(Params.OTHER_ADVANCED, q2.getCommandLineOptions());
        String seq = SeqUtil.getFasta(query.getSequenceName(), query.getSequence());
        cmd.setParameterVNN(Params.QUERY, seq);
        cmd.setParameterVNN(Params.DATABASE, q2.getDatabasePath());
        cmd.removeParameter(Params.FILTER);
        if (q2.isLowComplexityFilter()) {
            cmd.addParameter(Params.FILTER, QBlastFilter.LOW_COMPLEXITY.getQBlastName());
        }
        if (q2.isMaskForLookupTableOnlyFilter()) {
            cmd.addParameter(Params.FILTER, QBlastFilter.MASK_LOOKUP.getQBlastName());
        }
        cmd.addParameter(Params.PSSM, pssm);
        Integer maxNumberOfHits = getMaxNumberOfHits(query);
        
        cmd.setMaxNumberOfHits(maxNumberOfHits);
        cmd.setParameter(Params.ALIGNMENTS, String.valueOf(maxNumberOfHits));
        cmd.setParameter(Params.MAX_NUM_SEQ, String.valueOf(maxNumberOfHits));
        cmd.setParameter(Params.DESCRIPTIONS, "0");
        return cmd;
    }

}
