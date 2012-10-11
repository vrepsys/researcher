package researcher.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.hibernate.Session;

import researcher.beans.BlastQuery;
import researcher.beans.FastaSequence;
import researcher.beans.Hit;
import researcher.beans.User;
import researcher.blast.CompositionalAdjustments;
import researcher.blast.SearchLocation;
import researcher.blast.BlastTypes.HitDetails;
import researcher.blast.periodical.JobsManager;
import researcher.blast.qblast.PsiBlastTextParser;
import researcher.db.GlobalDAO;
import researcher.exceptions.SystemUnavailableException;
import researcher.test.utils.TestHibUtil;
import researcher.utils.Passwords;
import researcher.utils.SeqUtil;
import researcher.utils.Utils;

public class Init {
    
    private static User user;
    
    private static BlastQuery query1;
    
    private static BlastQuery query2;
    
    static {
        System.out.println("Creating user and queries..");
        Session s = TestHibUtil.getSession();
        s.beginTransaction();        
        GlobalDAO dao = new GlobalDAO(s);
        
        user = Init.createUser();        
        dao.saveOrUpdatetUser(user);
        query1 = Init.createBlastQuery1();
        query1.setUser(user);
        query2 = Init.createBlastQuery2();
        user.getQueries().add(query1);
        user.getQueries().add(query2);
        
        s.getTransaction().commit();
        s.close();
    }

    private static User createUser() {
        User user = new User();
        user.setUsername("test");
        String passwd;
        try {
            passwd = Passwords.constructEncryptedPassword("test", "test");
        } catch (SystemUnavailableException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        user.setPassword(passwd);
        user.setAdmin(false);
        return user;
    }

    /*
     * Result for the query resides in query_result1.html
     */
    private static BlastQuery createBlastQuery1() {
        BlastQuery query = new BlastQuery();
        query.setDatabasePath("nr");
        query.setEvalue(2.0E-6);
        query.setEnterDate(new Timestamp(System.currentTimeMillis()));
        query.setIterations(3);
        query.setPeriod(7);
        query.setCostToExtendAGap(1);
        query.setCostToOpenAGap(11);
        query.setEvalueMultipass(2.0E-6);
        query.setWordSize(3);
        // >gi|68380764|ref|XP_690311.1| PREDICTED: similar to Calpain small
        // subunit 1 (CSS1) (Calcium-dependent protease small subunit 1)
        // (Calcium-dependent protease small subunit) (CDPS) (Calpain regulatory
        // subunit) (Calcium-activated neutral proteinase small subunit) (CANP
        // small subunit)... [Danio rerio]
        query.setSequenceName("query1");
        query.setAskingForSearch(true);
        query.setDatabaseName("nr");
        query.setNextSearchDate(new Timestamp(System.currentTimeMillis()));
        query.setCompositionalAdjustments(CompositionalAdjustments.COMPOSITION_BASED_STATISTICS);
        query.setDoubleSearch(false);
        query.setLowComplexityFilter(false);
        query.setMaskForLookupTableOnlyFilter(false);
        query.setSearchLocation(SearchLocation.NCBI);
        query.setNotifyByMail(false);
        query.setMatrix("BLOSUM62");
        query.setSequence("MEVSPKELMTILNKIISKHGDLKTDGFSIESCRSMVAVMDSDSTGKLGFEEFNYLWNNIKRWQAIYKKYD"+
        "ADQSGVIGSDELPGAFKAAGFPLNDQLFQLIVRRYSDEMGNMDSDNDIGCLVRLDAMCRAFKTLDKDNNG"+
        "TIKVDIQEWLQLTIEGFLEGGRMSEFMPYPYRRPGGSLMRFKKRKSRFTFQEVELLLSEVQKKRHILVGK"+
        "FNRGISKDLKNRTWLSVTERINEVSECHREVIEVIKKWADLKCDTKRRVAAMRASGATSAQISQELSPIE"+
        "TMVHQILQLSNSNRNMSSFAIDDQMDDDDEDMAGLSEISQSSSYMANGRPHSFALPMPLPIPMPMPVPPP"+
        "FHTSVPGQVDMELPSHMMFSDSTVPLTSLHPNTSGKEGGNVQFIEPLSQPNQPPVSMPAFLSRPITEKTQ"+
        "VQEKANQSTFNGSSAFIVPSGAPSSRSAPASLPPQPAAPEPRSLQERLSQSASLSLHEQQATTQLIGSLS"+
        "RSLESLAESVQKLVQIQRQFSHDTLQMQRDTLHVLRNFSSNTLTLLQDKMNGHP");
        return query;
    }
    
    /*
     * Result for the query resides in query_result2.html
     */
    private static BlastQuery createBlastQuery2() {
        BlastQuery query = new BlastQuery();
        query.setDatabasePath("nr");
        query.setEvalue(2.0E-6);
        query.setEnterDate(new Timestamp(System.currentTimeMillis()));
        query.setIterations(3);
        query.setPeriod(7);
        query.setCostToExtendAGap(1);
        query.setCostToOpenAGap(11);
        query.setEvalueMultipass(2.0E-6);
        query.setWordSize(3);        
        query.setSequenceName("query2");
        query.setAskingForSearch(true);
        query.setDatabaseName("nr");
        query.setNextSearchDate(new Timestamp(System.currentTimeMillis()));
        query.setCompositionalAdjustments(CompositionalAdjustments.COMPOSITION_BASED_STATISTICS);
        query.setDoubleSearch(false);
        query.setLowComplexityFilter(false);
        query.setMaskForLookupTableOnlyFilter(false);
        query.setSearchLocation(SearchLocation.NCBI);
        query.setNotifyByMail(false);
        query.setMatrix("BLOSUM62");
        query.setSequence("SDTTGKLGFEEFKYLWNNIKRWQAIYKQFDTDRSGTICSSELPGA"+
                          "FEAAGFHLNEHLYNMIIRRYSDESGNMDFDNFISCLVRLDAMF");
        return query;
    }

    public static BlastQuery getQuery1() {
        return query1;
    }

    public static BlastQuery getQuery2() {
        return query2;
    }

    public static User getUser() {
        return user;
    }

    public static Set<Hit> getQuery2Hits() {
        try {
            return getHits(getQuery1(), "data_tests/query2_result.html", "data_tests/query2_fastas.xml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static Set<Hit> getQuery1Hits() {
        try {
            return getHits(getQuery1(), "data_tests/query1_result.html", "data_tests/query1_fastas.xml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    
    private static void attachFasta(Set<Hit> hits, String fastasXml) {
        Properties fastas = new Properties();

        try {
            fastas.loadFromXML(new FileInputStream(fastasXml));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        int i = 0;
        for (Hit hit : hits) {
//            String fasta = fastas.getProperty(SeqUtil.getGiId(hit.getSubjectId()));
//            if (fasta != null) {
//                FastaSequence fastaseq = new FastaSequence();
//                fastaseq.setFasta(fasta);
//                String id = SeqUtil.getGiId(fasta);
//                fastaseq.setSequenceId(id);
//                hit.setFastaSequence(fastaseq);
//            }
//            else
//                i++;
        }
        if (i > 0)
            System.out.println("fasta was not found for " + i + " hits of " + hits.size());
    }
    
    private static Set<Hit> getHits(BlastQuery query, String resultsFile) throws IOException{
        String resultsText = Utils.readTextFile(resultsFile);
        List<HitDetails> hitDetails = PsiBlastTextParser.parse(resultsText);
        Set<Hit> hits = JobsManager.getHits(query, hitDetails);
        return hits;
    }
    
    private static Set<Hit> getHits(BlastQuery query, String resultsFile, String fastasFile) throws IOException {
        Set<Hit> hits = getHits(query, resultsFile);
        attachFasta(hits, fastasFile);
        return hits;
    }
    
}
