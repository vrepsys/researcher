package researcher.blast;

import java.util.List;

public class BlastTypes {

    public static class HitDetails {

        private String sequenceLength;

        private String hitId;

        private String hitDescription;

        private double eValue;

        private double bitScore;

        private double score;

        private int iteration;

        private String accession;

        private List<SubHitDetails> subhits;

        public HitDetails(String hitId, String hitDescription, int iteration) {
            this.hitDescription = hitDescription;
            this.hitId = hitId;
            this.iteration = iteration;
        }

        public String getSequenceLength() {
            return sequenceLength;
        }

        public void setSequenceLength(String sequenceLength) {
            this.sequenceLength = sequenceLength;
        }

        public String getHitDescription() {
            return hitDescription;
        }

        public String getHitId() {
            return hitId;
        }

        public double getEValue() {
            return eValue;
        }

        public void setEValue(double value) {
            eValue = value;
        }

        public int getIteration() {
            return iteration;
        }

        public double getBitScore() {
            return bitScore;
        }

        public void setBitScore(double bitScore) {
            this.bitScore = bitScore;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public List<SubHitDetails> getSubhits() {
            return subhits;
        }

        public void setSubhits(List<SubHitDetails> subhits) {
            this.subhits = subhits;
        }

        public String getAccession() {
            return accession;
        }

        public void setAccession(String accessionNumber) {
            this.accession = accessionNumber;
        }

    }

    public static class SubHitDetails {

        private double score;

        private double expectValue;

        private double bitScore;

        private int numberOfIdentities;

        private int alignmentSize;

        private int queryFrame;

        private int hitFrame;

        private int numberOfPositives;

        private int numberOfGaps;

        private AlignmentDetails alignment;

        private int hspNum;

        public SubHitDetails() {
        }

        public SubHitDetails(double score, double expectValue, double bitScore) {
            this.score = score;
            this.expectValue = expectValue;
            this.bitScore = bitScore;
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

        public AlignmentDetails getAlignment() {
            return alignment;
        }

        public void setAlignment(AlignmentDetails alignment) {
            this.alignment = alignment;
        }

        public int getQueryFrame() {
            return queryFrame;
        }

        public void setQueryFrame(int queryFrame) {
            this.queryFrame = queryFrame;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public int getNumberOfGaps() {
            return numberOfGaps;
        }

        public void setNumberOfGaps(int numberOfGaps) {
            this.numberOfGaps = numberOfGaps;
        }

        @Override
        public String toString() {
            return "expect=" + expectValue + " hspnum=" + hspNum;
        }
    }

    public static class AlignmentDetails {

        private String querySeq;

        private int querySeqFrom;

        private int querySeqTo;

        private String hitSeq;

        private int hitSeqFrom;

        private int hitSeqTo;

        private String consensus;

        public AlignmentDetails(String querySeq, int querySeqFrom, int querySeqTo, String hitSeq,
                int hitSeqFrom, int hitSeqTo, String consensus) {
            this.querySeq = querySeq;
            this.querySeqFrom = querySeqFrom;
            this.querySeqTo = querySeqTo;
            this.hitSeq = hitSeq;
            this.hitSeqFrom = hitSeqFrom;
            this.hitSeqTo = hitSeqTo;
            this.consensus = consensus;
        }

        public String getConsensus() {
            return consensus;
        }

        public String getHitSeq() {
            return hitSeq;
        }

        public int getHitSeqFrom() {
            return hitSeqFrom;
        }

        public int getHitSeqTo() {
            return hitSeqTo;
        }

        public String getQuerySeq() {
            return querySeq;
        }

        public int getQuerySeqFrom() {
            return querySeqFrom;
        }

        public int getQuerySeqTo() {
            return querySeqTo;
        }

    }

    public static class SequenceDetails {

        public SequenceDetails(String accessionNum, String seq) {
            this.accessionNumber = accessionNum;
            this.sequence = seq;
        }

        private String accessionNumber;

        private String sequence;

        public String getAccessionNumber() {
            return accessionNumber;
        }

        public String getSequence() {
            return sequence;
        }
    }

}
