package researcher.beans;

import java.sql.Blob;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Index;

import researcher.blast.CompositionalAdjustments;
import researcher.blast.SearchLocation;



@Entity
@Table(name = "queries")
public class BlastQuery {

    private long id;

    /**
     * where to perform a search (at ncbi or local server)
     */
    private SearchLocation searchLocation;

    /**
     * if a query has a double search set to true, it has to perform double
     * search and secondaryQuery must be not null
     */
    private boolean doubleSearch;

    /**
     * query used to perform the second search in a double query
     */
    private SecondaryBlastQuery secondaryQuery;

    private boolean notifyByMail;

    private String sequenceName;

    private String sequence;

    private CompositionalAdjustments compositionalAdjustments;

    private boolean lowComplexityFilter;

    private boolean maskForLookupTableOnlyFilter;

    private String pssm;

    private String phiPattern;

    private int costToOpenAGap;

    private int costToExtendAGap;

    private String matrix;

    private int wordSize;

    private double evalueMultipass;

    private int period;

    private int iterations;

    private Timestamp enterDate;

    private Timestamp lastSearchDate;

    private Timestamp nextSearchDate;

    private double evalue;

    private Timestamp recentHitsDate;

    private User user;

    private Blob inputAlignmentFile;

    private String inputAlignmentFileName;

    private Set<Hit> hits = new HashSet<Hit>();

    private String databasePath;

    private String databaseName;

    private String checkpointFileInput;

    private String checkpointFileOutput;

    private String commandLineOptions;

    private String blastErrors;

    private boolean askingForSearch;
    
    public int numberOfHits;
    
    public int numberOfNewHits;
    
    public Integer maxNumberOfHits;
    
    public Integer getMaxNumberOfHits() {
      return maxNumberOfHits;
   }

   public void setMaxNumberOfHits(Integer maxNumberOfHits) {
      this.maxNumberOfHits = maxNumberOfHits;
   }

   public int getNumberOfHits() {
        return numberOfHits;
    }

    public void setNumberOfHits(int numberOfHits) {
        this.numberOfHits = numberOfHits;
    }

    public int getNumberOfNewHits() {
        return numberOfNewHits;
    }

    public void setNumberOfNewHits(int numberOfNewHits) {
        this.numberOfNewHits = numberOfNewHits;
    }

    public String getCommandLineOptions() {
        return commandLineOptions;
    }

    public void setCommandLineOptions(String commandLineOptions) {
        this.commandLineOptions = commandLineOptions;
    }

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Timestamp getEnterDate() {
        return enterDate;
    }

    public void setEnterDate(Timestamp enterDate) {
        this.enterDate = enterDate;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    @Column(length = 32672)
    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public Timestamp getLastSearchDate() {
        return lastSearchDate;
    }

    public void setLastSearchDate(Timestamp lastSearch) {
        this.lastSearchDate = lastSearch;
    }

    public Timestamp getRecentHitsDate() {
        return recentHitsDate;
    }

    public void setRecentHitsDate(Timestamp recentHitsDate) {
        this.recentHitsDate = recentHitsDate;
    }

    @Transient
    public boolean getHasNewResults() {
        return numberOfNewHits > 0;
    }

    public double getEvalue() {
        return evalue;
    }

    public void setEvalue(double evalue) {
        this.evalue = evalue;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @Index(name = "query_user_ind")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @OneToMany(mappedBy = "query", cascade = { CascadeType.ALL })
    public Set<Hit> getHits() {
        return hits;
    }

    public void setHits(Set<Hit> hits) {
        this.hits = hits;
    }

    public int getCostToExtendAGap() {
        return costToExtendAGap;
    }

    public void setCostToExtendAGap(int costToExtendAGap) {
        this.costToExtendAGap = costToExtendAGap;
    }

    public int getCostToOpenAGap() {
        return costToOpenAGap;
    }

    public void setCostToOpenAGap(int costToOpenAGap) {
        this.costToOpenAGap = costToOpenAGap;
    }

    public double getEvalueMultipass() {
        return evalueMultipass;
    }

    public void setEvalueMultipass(double evalueMultipass) {
        this.evalueMultipass = evalueMultipass;
    }

    public String getMatrix() {
        return matrix;
    }

    public void setMatrix(String matrix) {
        this.matrix = matrix;
    }

    public int getWordSize() {
        return wordSize;
    }

    public void setWordSize(int wordSize) {
        this.wordSize = wordSize;
    }

    @Lob
    @Column(length = 32000000)
    public Blob getInputAlignmentFile() {
        return inputAlignmentFile;
    }

    public void setInputAlignmentFile(Blob fileForRestart) {
        this.inputAlignmentFile = fileForRestart;
    }

    public String getInputAlignmentFileName() {
        return inputAlignmentFileName;
    }

    public void setInputAlignmentFileName(String fileForRestartName) {
        this.inputAlignmentFileName = fileForRestartName;
    }

    public String getDatabasePath() {
        return databasePath;
    }

    public void setDatabasePath(String database) {
        this.databasePath = database;
    }

    @Column(length = 1024)
    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public String getCheckpointFileInput() {
        return checkpointFileInput;
    }

    public void setCheckpointFileInput(String checkpointFileInput) {
        this.checkpointFileInput = checkpointFileInput;
    }

    public String getCheckpointFileOutput() {
        return checkpointFileOutput;
    }

    public void setCheckpointFileOutput(String checkpointFileOutput) {
        this.checkpointFileOutput = checkpointFileOutput;
    }

    @Column(length = 5120)
    public String getBlastErrors() {
        return blastErrors;
    }

    public void setBlastErrors(String blastErrors) {
        this.blastErrors = blastErrors;
    }

    public boolean getAskingForSearch() {
        return askingForSearch;
    }

    public void setAskingForSearch(boolean askingForSearch) {
        this.askingForSearch = askingForSearch;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public Timestamp getNextSearchDate() {
        return nextSearchDate;
    }

    public void setNextSearchDate(Timestamp nextSearchDate) {
        this.nextSearchDate = nextSearchDate;
    }

    @Transient
    public String getShortDetails() {
        StringBuffer b = new StringBuffer();
        b.append("@");
        b.append(searchLocation);
        b.append(", ");
        b.append(getDatabaseName());
        b.append(", j=");
        b.append(getIterations());
        if (doubleSearch) {
            b.append(", ");
            b.append(getSecondaryQuery().getDatabaseName());
        }
        return b.toString();
    }

    public CompositionalAdjustments getCompositionalAdjustments() {
        return compositionalAdjustments;
    }

    public void setCompositionalAdjustments(CompositionalAdjustments compositionalAdjustments) {
        this.compositionalAdjustments = compositionalAdjustments;
    }

    public boolean isDoubleSearch() {
        return doubleSearch;
    }

    public void setDoubleSearch(boolean doubleSearch) {
        this.doubleSearch = doubleSearch;
    }

    public boolean isLowComplexityFilter() {
        return lowComplexityFilter;
    }

    public void setLowComplexityFilter(boolean lowComplexityFilter) {
        this.lowComplexityFilter = lowComplexityFilter;
    }

    public boolean isMaskForLookupTableOnlyFilter() {
        return maskForLookupTableOnlyFilter;
    }

    public void setMaskForLookupTableOnlyFilter(boolean maskForLookupTableOnlyFilter) {
        this.maskForLookupTableOnlyFilter = maskForLookupTableOnlyFilter;
    }

    public String getPhiPattern() {
        return phiPattern;
    }

    public void setPhiPattern(String phiPattern) {
        this.phiPattern = phiPattern;
    }

    @Column(length = 32672)
    public String getPssm() {
        return pssm;
    }

    public void setPssm(String pssm) {
        this.pssm = pssm;
    }

    public SearchLocation getSearchLocation() {
        return searchLocation;
    }

    public void setSearchLocation(SearchLocation searchLocation) {
        this.searchLocation = searchLocation;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "secondary_query_fk")
    public SecondaryBlastQuery getSecondaryQuery() {
        return secondaryQuery;
    }

    public void setSecondaryQuery(SecondaryBlastQuery secondaryQuery) {
        this.secondaryQuery = secondaryQuery;
    }

    public boolean getNotifyByMail() {
        return notifyByMail;
    }

    public void setNotifyByMail(boolean notifyByMail) {
        this.notifyByMail = notifyByMail;
    }
    
    @Transient
    public String getDeepestDatabasePath(){
        if (getSecondaryQuery() == null)
            return databasePath;
        else
            return getSecondaryQuery().getDatabasePath();
    }

    @Override
    public String toString() {
        return getShortDetails();
    }
    
    @Transient
    public Set<Hit> getNewHits() {
        Set<Hit> hits = getHits();
        Set<Hit> result = new HashSet<Hit>();
        for (Hit hit : hits)
            if (hit.isUnseen())
                result.add(hit);
        return result;
    }
}
