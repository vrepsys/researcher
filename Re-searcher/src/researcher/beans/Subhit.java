package researcher.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

@Entity
@Table(name = "subhits")
public class Subhit {

    private long id;

    private Hit hit;

    private double score;

    private double expectValue;

    private double bitScore;

    private int numberOfIdentities;

    private int alignmentSize;

    private int queryFrame;

    private int hitFrame;

    private int numberOfPositives;

    private String querySeq;

    private int querySeqFrom;

    private int querySeqTo;

    private String hitSeq;

    private int hitSeqFrom;

    private int hitSeqTo;

    private String consensus;

    private int hspNum;

    private int numberOfGaps;

    public int getNumberOfGaps() {
        return numberOfGaps;
    }

    public void setNumberOfGaps(int numberOfGaps) {
        this.numberOfGaps = numberOfGaps;
    }

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @Index(name = "subhit_hit_ind")
    public Hit getHit() {
        return hit;
    }

    public void setHit(Hit hit) {
        this.hit = hit;
    }

    public int getAlignmentSize() {
        return alignmentSize;
    }

    public void setAlignmentSize(int alignmentSize) {
        this.alignmentSize = alignmentSize;
    }

    public double getBitScore() {
        return bitScore;
    }

    public void setBitScore(double bitScore) {
        this.bitScore = bitScore;
    }

    @Lob
    @Column(length = 32672)
    public String getConsensus() {
        return consensus;
    }

    public void setConsensus(String consensus) {
        this.consensus = consensus;
    }

    public double getExpectValue() {
        return expectValue;
    }

    public void setExpectValue(double expectValue) {
        this.expectValue = expectValue;
    }

    public int getHitFrame() {
        return hitFrame;
    }

    public void setHitFrame(int hitFrame) {
        this.hitFrame = hitFrame;
    }

    @Lob
    @Column(length = 32672)
    public String getHitSeq() {
        return hitSeq;
    }

    public void setHitSeq(String hitSeq) {
        this.hitSeq = hitSeq;
    }

    public int getHitSeqFrom() {
        return hitSeqFrom;
    }

    public void setHitSeqFrom(int hitSeqFrom) {
        this.hitSeqFrom = hitSeqFrom;
    }

    public int getHitSeqTo() {
        return hitSeqTo;
    }

    public void setHitSeqTo(int hitSeqTo) {
        this.hitSeqTo = hitSeqTo;
    }

    public int getHspNum() {
        return hspNum;
    }

    public void setHspNum(int hspNum) {
        this.hspNum = hspNum;
    }

    public int getNumberOfIdentities() {
        return numberOfIdentities;
    }

    public void setNumberOfIdentities(int numberOfIdentities) {
        this.numberOfIdentities = numberOfIdentities;
    }

    public int getNumberOfPositives() {
        return numberOfPositives;
    }

    public void setNumberOfPositives(int numberOfPositives) {
        this.numberOfPositives = numberOfPositives;
    }

    public int getQueryFrame() {
        return queryFrame;
    }

    public void setQueryFrame(int queryFrame) {
        this.queryFrame = queryFrame;
    }

    @Lob
    @Column(length = 32672)
    public String getQuerySeq() {
        return querySeq;
    }

    public void setQuerySeq(String querySeq) {
        this.querySeq = querySeq;
    }

    public int getQuerySeqFrom() {
        return querySeqFrom;
    }

    public void setQuerySeqFrom(int querySeqFrom) {
        this.querySeqFrom = querySeqFrom;
    }

    public int getQuerySeqTo() {
        return querySeqTo;
    }

    public void setQuerySeqTo(int querySeqTo) {
        this.querySeqTo = querySeqTo;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

}
