package researcher.qblast.parameters;

public enum Params {
   ALIGNMENTS, 
   ALIGNMENT_VIEW, 
   AUTO_FORMAT, 
   CDD_SEARCH,
   CLIENT, 
   COMPOSITION_BASED_STATISTICS, 
   ENTREZ_QUERY, 
   FORMAT_BLOCK_ON_RESPAGE, 
   FORMAT_ENTREZ_QUERY, 
   FORMAT_OBJECT, 
   FORMAT_TYPE, 
   DESCRIPTIONS,
   EXPECT,
   I_THRESH, 
   GAPCOSTS, 
   GET_SEQUENCE,
   HITLIST_SIZE, 
   MASK_CHAR,
   MASK_COLOR, 
   NCBI_GI,
   NUM_OVERVIEW, 
   PAGE, 
   PROGRAM, RUN_PSIBLAST, 
   SERVICE, 
   MATRIX_NAME, 
   SHOW_LINKOUT, 
   SHOW_OVERVIEW,
   WORD_SIZE, 
   PSSM, NEXT_I,
   CMD, 
   LCASE_MASK, 
   DATABASE,
   QUERY,
   RID,
   PREV_RID, 
   STEP_NUMBER,
   LAYOUT, 
   FILTER, 
   FORMAT_PAGE_TARGET, 
   RESULTS_PAGE_TARGET,
   MAX_NUM_SEQ, 
   SELECTED_PROG_TYPE,  
   BLAST_PROGRAMS,
   GOOD_GI {
      @Override
      public String toString() {
         return "good_GI";
      }
   },
   CHECKED_GI {
      @Override
      public String toString() {
         return "checked_GI";
      }
   },
   OTHER_ADVANCED, 
   PHI_PATTERN, 
   USER_DEFAULT_PROG_TYPE;

   @Override
   public String toString() {
      return name();
   }
}
