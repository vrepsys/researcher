package researcher.beans;

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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Index;
import org.hibernate.engine.CascadeStyle;

@Entity
@Table(name = "hits")
public class Hit {

    private long id;

    private String subjectId;

    private String subjectDef;

    private double evalue;

    private Timestamp hitDate;

    private boolean unseen;

    private BlastQuery query;

    private String sequenceLength;

    private FastaSequence fastaSequence;

    private String accession;

    private Set<Subhit> subhits = new HashSet<Subhit>();

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Timestamp getHitDate() {
        return hitDate;
    }

    public void setHitDate(Timestamp hitDate) {
        this.hitDate = hitDate;
    }

    @Column(length = 32672)
    public String getSubjectDef() {
        return subjectDef;
    }

    public void setSubjectDef(String subjectDef) {
        this.subjectDef = subjectDef;
    }

    @Column(length = 1024)
    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public double getEvalue() {
        return evalue;
    }

    public void setEvalue(double evalue) {
        this.evalue = evalue;
    }

    public boolean isUnseen() {
        return unseen;
    }

    public void setUnseen(boolean isNew) {
        this.unseen = isNew;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "queryId")
    @Index(name = "hit_query_ind")
    public BlastQuery getQuery() {
        return query;
    }

    public void setQuery(BlastQuery query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return "Hit [subjId=" + subjectId + ", subjectDef=" + subjectDef + "]";
    }

    @OneToMany(mappedBy = "hit", cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    public Set<Subhit> getSubhits() {
        return subhits;
    }

    public void setSubhits(Set<Subhit> subhits) {
        this.subhits = subhits;
    }

    public String getSequenceLength() {
        return sequenceLength;
    }

    public void setSequenceLength(String sequenceLength) {
        this.sequenceLength = sequenceLength;
    }

    @ManyToOne()
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "fasta_sequence_id")
    @Index(name = "hit_fasta_ind")
    public FastaSequence getFastaSequence() {
        return fastaSequence;
    }

    public void setFastaSequence(FastaSequence fasta) {
        this.fastaSequence = fasta;
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accessionNumber) {
        this.accession = accessionNumber;
    }
    
    @Transient
    public String getFasta(){
        if (getFastaSequence() != null) return getFastaSequence().getFasta();
        return null;
    }

}
