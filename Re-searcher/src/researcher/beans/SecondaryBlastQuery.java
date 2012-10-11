package researcher.beans;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import researcher.blast.CompositionalAdjustments;


@Entity
@Table(name = "secondary_queries")
public class SecondaryBlastQuery {

    private long id;

    private CompositionalAdjustments compositionalAdjustments;

    private boolean lowComplexityFilter;

    private boolean maskForLookupTableOnlyFilter;

    private int costToOpenAGap;

    private int costToExtendAGap;

    private String matrix;

    private int wordSize;

    private double evalue;

    private String databasePath;

    private String databaseName;

    private String commandLineOptions;

    public String getCommandLineOptions() {
        return commandLineOptions;
    }

    public void setCommandLineOptions(String commandLineOptions) {
        this.commandLineOptions = commandLineOptions;
    }

    public CompositionalAdjustments getCompositionalAdjustments() {
        return compositionalAdjustments;
    }

    public void setCompositionalAdjustments(CompositionalAdjustments compositionalAdjustments) {
        this.compositionalAdjustments = compositionalAdjustments;
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

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabasePath() {
        return databasePath;
    }

    public void setDatabasePath(String databasePath) {
        this.databasePath = databasePath;
    }

    public double getEvalue() {
        return evalue;
    }

    public void setEvalue(double evalue) {
        this.evalue = evalue;
    }

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

}
