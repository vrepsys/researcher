package researcher.blast.periodical;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;

import org.apache.log4j.Logger;

import researcher.beans.BlastQuery;
import researcher.beans.Configuration;
import researcher.beans.FastaSequence;
import researcher.beans.Hit;
import researcher.beans.Subhit;
import researcher.blast.BlastTypes.HitDetails;
import researcher.blast.BlastTypes.SubHitDetails;
import researcher.blast.exec.PsiBlastExecutor;
import researcher.blast.exec.PsiBlastExecutorResult;
import researcher.db.GlobalDAO;
import researcher.utils.MailSender;
import researcher.utils.SeqUtil;
import researcher.utils.Utils;

public class JobsManager {

   private static final Logger jobsManagerLog = Logger.getLogger("jobsManager");

   private static final List<Timer> timers = new ArrayList<Timer>();

   public static void startPeriodical() {
      jobsManagerLog.info("Starting periodical BlastJob..");

      Timer timer = new Timer();
      final LocalSearchJob local = new LocalSearchJob();
      timer.schedule(local, 0, 30 * 1000);
      timers.add(timer);

      timer = new Timer();
      final NCBISearchJob ncbi = new NCBISearchJob();
      timer.schedule(ncbi, 0, 30 * 1000);
      timers.add(timer);

   }

   public static void stopPeriodical() {
      jobsManagerLog.info("Stopping periodical BlastJob..");
      for (final Timer timer : timers) {
         timer.cancel();
      }
   }

   public static void saveError(long queryId, String msg) {
      GlobalDAO dao = null;
      try {
         dao = new GlobalDAO();
         BlastQuery query = dao.getQuery(queryId);
         if (msg.length() > 5000) {
            msg = msg.substring(0, 5000) + "...";
         }
         query.setLastSearchDate(new Timestamp(new Date().getTime()));
         query.setBlastErrors(msg);
         query.setAskingForSearch(false);
         query.setNextSearchDate(null);
      } finally {
         if (dao != null)
            dao.close();
      }
   }

   public static void executeQueriesAndSaveResults(
         final PsiBlastExecutor executor, final List<BlastQuery> queries,
         final Configuration conf, final Logger log) {
      for (BlastQuery query : queries) {
         log.info("executing query: " + query);
         final PsiBlastExecutorResult result = executor.execute(query);
         if (result.hasErrors()) {
            saveError(query.getId(), result.getErrors());
            if (query.getNotifyByMail() && conf != null
                  && conf.emailConfigured()) {
               MailSender.sendNotifyOnError(query.getId());
            }
            return;
         }
         List<HitDetails> hitDetails = result.getHits();
         if (hitDetails == null) {
            saveError(query.getId(),
                  "hitDetails is null, this is a software bug, please report.");
            return;
         }
         Set<Hit> foundHits = getHits(query, hitDetails);

         GlobalDAO dao = null;
         try {
            dao = new GlobalDAO();
            log.info("attaching fastas..");
            attachSequenceString(executor, foundHits, query
                  .getDeepestDatabasePath(), dao, log);
            log.info("fastas attached.");
         } finally {
            dao.close();
         }
         try {
            dao = new GlobalDAO();
            log.info("extracting new hits..");
            Set<Hit> existingHits = query.getHits();
            foundHits = getNewHits(existingHits, foundHits, log);
            log.info("saving hits..");
            saveHits(query, foundHits, dao, log);
            log.info("query finished executing.. (" + query + ")");
         } finally {
            if (dao != null)
               dao.close();
         }
         if (foundHits.size() != 0) {
            if (query.getNotifyByMail() && conf != null
                  && conf.emailConfigured()) {
               log.info("sending email");
               MailSender.sendNotifyOnNewHits(query.getId());
            }
         }
      }
   }

   public static void attachSequenceString(final PsiBlastExecutor executor,
         final Set<Hit> hits, final String database, GlobalDAO dao,
         final Logger log) {
      
      List<Hit> hitswoseq = new ArrayList<Hit>();
      
      // first, we try to find fastas in the database 
      for (Hit hit : hits) {
         String id = SeqUtil.extractFastaId(hit.getSubjectId());
         FastaSequence fastaseq = null;
         if (id != null) {
            fastaseq = dao.getFastaSequenceBySeqId(id);
            if (fastaseq != null) {
//             log.info("Fasta for hit " + hit.getAccession() + " was in the db");
               hit.setFastaSequence(fastaseq);
               fastaseq.getHits().add(hit);
            }
            else {
               hitswoseq.add(hit);
            }            
         }
         
      }
      
      // second, for hits we did not find sequences for we search somewhere else 
      List<Hit> hitsFastaSearch = new ArrayList<Hit>();
      List<String> ids = new ArrayList<String>();
      for (Hit hit : hitswoseq) {
         String id = SeqUtil.extractFastaId(hit.getSubjectId());
         if (id != null) {
            hitsFastaSearch.add(hit);
            ids.add(id);
         }
         else {
            log.warn("id is null for " + hit.getSubjectId());
         }
      }
      
      List<String> fastas = executor.getFastas(ids, database);
      
      for (int i = 0; i < hitsFastaSearch.size(); i++){
         Hit hit = hitsFastaSearch.get(i);
         String id = ids.get(i);
         String fasta = fastas.get(i);
         if (fasta != null) {
           FastaSequence fastaseq = new FastaSequence();
           fastaseq.setFasta(fasta);
           fastaseq.setSequenceId(id);
           fastaseq.getHits().add(hit);
           hit.setFastaSequence(fastaseq);
         }
      }
      
      
//      for (final Hit hit : hits) {
//         String id = SeqUtil.extractFastaId(hit.getSubjectId());
//         log.info("attach sequence, id = " + id);
//         FastaSequence fastaseq = null;
//         if (id != null) {
//            fastaseq = dao.getFastaSequenceBySeqId(id);
//         }
//         if (fastaseq == null) {
//            final String fasta = executor.getFasta(hit, database);
//            if (fasta == null) {
//               log.warn("Fasta for hit " + hit.getAccession() + " is null");
//            } else {
//               log.info("Found fasta for hit " + hit.getAccession());
//            }
//            fastaseq = new FastaSequence();
//            fastaseq.setFasta(fasta);
//            fastaseq.setSequenceId(id);
//            fastaseq.getHits().add(hit);
//            hit.setFastaSequence(fastaseq);
//         } else {
//            log.info("Fasta for hit " + hit.getAccession() + " was in the db");
//            hit.setFastaSequence(fastaseq);
//            fastaseq.getHits().add(hit);
//         }
//      }
      
   }

   /**
    * 
    * @param query
    * @param hits
    * @param log
    * @return true if new hits found, else false
    */
   public static void saveHits(BlastQuery query, final Set<Hit> hits,
         GlobalDAO dao, final Logger log) {
      long st = System.currentTimeMillis();
      if (hits == null)
         throw new NullPointerException();
      Date lastSearch;
      Timestamp recentHitDate = null;
      if (hits.size() == 0) {
         lastSearch = new Date();
      } else {
         int i = 0;
         int size = hits.size();
         for (final Hit hit : hits) {
            long start = System.currentTimeMillis();
            recentHitDate = hit.getHitDate();
            dao.addHit(hit);
            i++;
            if (i % 20 == 0) { // 20, same as the JDBC batch size
               // flush a batch of inserts and release memory:
               dao.flush();
               dao.clear();
               long end = System.currentTimeMillis();
//               log.info("inserted: " + i + "/" + size + " flush time: "
//                     + (end - start));
               start = System.currentTimeMillis();
            }
         }
         lastSearch = hits.iterator().next().getHitDate();
      }
      dao.flush();
      dao.clear();
      query = dao.getQuery(query.getId());
      int numberOfHits = query.getHits().size();
      int numberOfNewHits = query.getNewHits().size();
      query.setNumberOfHits(numberOfHits);
      query.setNumberOfNewHits(numberOfNewHits);
      if (recentHitDate == null) {
         log.info("NO NEW HITS FOUND");
      } else {
         query.setRecentHitsDate(recentHitDate);
      }
      query.setLastSearchDate(new Timestamp(lastSearch.getTime()));
      query.setBlastErrors(null);
      final int period = query.getPeriod();
      final Date nextSearch = Utils.addDays(lastSearch, period);
      query.setNextSearchDate(new Timestamp(nextSearch.getTime()));
      query.setAskingForSearch(false);
      long end = System.currentTimeMillis();
      log.info("overall saveHits time: " + (end - st));
   }

   public static Set<Hit> getNewHits(final Set<Hit> existantHits,
         final Set<Hit> foundHits, final Logger log) {
      if (foundHits == null)
         throw new NullPointerException();
      final Set<Hit> newHits = new HashSet<Hit>();
      if (foundHits.size() == 0)
         return newHits;
      if (existantHits.size() == 0) {
         log.info("no existant hits, all hits are new");
         return new HashSet<Hit>(foundHits);
      }
      // int size = foundHits.size();
      // int cnt = 0;
      // long end;
      // long start = System.currentTimeMillis();
      HitsCollection oldHits = new HitsCollection(existantHits);
      for (final Hit hit : foundHits) {
         if (!oldHits.contains(hit)) {
            newHits.add(hit);
         }
         // cnt++;
         // end = System.currentTimeMillis();
         // long avg = (end - start) / cnt;
         // System.out.println("extracting new hits: " + cnt + "/" + size + "
         // avg=" + avg);
      }
      return newHits;
   }

   public static Set<Hit> getHits(final BlastQuery query,
         final List<HitDetails> hitDetails) {
      final Date recentHitsDate = new Date();
      int maxIter = 0;
      for (final HitDetails hit : hitDetails) {
         maxIter = Math.max(maxIter, hit.getIteration());
      }
      final Set<Hit> hits = new HashSet<Hit>();
      for (final HitDetails hit : hitDetails) {
         if (maxIter == hit.getIteration()) {
            final double eValue = hit.getEValue();
            final Hit hitdb = new Hit();
            hitdb.setQuery(query);
            hitdb.setSubjectId(hit.getHitId());
            hitdb.setSubjectDef(hit.getHitDescription());
            hitdb.setEvalue(eValue);
            hitdb.setUnseen(true);
            hitdb.setHitDate(new Timestamp(recentHitsDate.getTime()));
            hitdb.setSequenceLength(hit.getSequenceLength());
            hitdb.setAccession(hit.getAccession());
            final List<SubHitDetails> subhits = hit.getSubhits();
            for (final SubHitDetails subhit : subhits) {
               final Subhit subhitdb = new Subhit();
               subhitdb.setHitFrame(subhit.getHitFrame());
               subhitdb.setQueryFrame(subhit.getQueryFrame());
               subhitdb.setAlignmentSize(subhit.getAlignmentSize());
               subhitdb.setExpectValue(subhit.getExpectValue());
               subhitdb.setBitScore(subhit.getBitScore());
               subhitdb.setScore(subhit.getScore());
               subhitdb.setNumberOfIdentities(subhit.getNumberOfIdentities());
               subhitdb.setNumberOfPositives(subhit.getNumberOfPositives());
               subhitdb.setNumberOfGaps(subhit.getNumberOfGaps());
               subhitdb.setHspNum(subhit.getHspNum());
               subhitdb.setHitSeqFrom(subhit.getAlignment().getHitSeqFrom());
               subhitdb.setHitSeqTo(subhit.getAlignment().getHitSeqTo());
               subhitdb.setHitSeq(subhit.getAlignment().getHitSeq());
               subhitdb
                     .setQuerySeqFrom(subhit.getAlignment().getQuerySeqFrom());
               subhitdb.setQuerySeqTo(subhit.getAlignment().getQuerySeqTo());
               subhitdb.setQuerySeq(subhit.getAlignment().getQuerySeq());
               subhitdb.setConsensus(subhit.getAlignment().getConsensus());
               subhitdb.setHit(hitdb);
               hitdb.getSubhits().add(subhitdb);
            }
            hits.add(hitdb);
         }
      }
      return hits;
   }

   public static void main(String[] args) {
      // Hit hit1 = new Hit();
      // hit1
      // .setFasta(">gi|161332|gb|AAA30007.1| troponin \n"
      // +
      // "MAKFKAGMPKDAIEALKQEFKDNYDTNKDGTVSCAELVKLMNWTEEMAQNIIARLDVNSDGHMQFDEFILYMEGSTKERLYSSDEIKQMFDDLDKDGNGRISPDELNKGVREIYTKVVDGMANKLIQEADKDGDGHVNMEEFFDTLVVKLPIGMGPCKDEEYREYYKNEFEKFDKNGDGSLTTAEMSEFMSKSTKYSDKEIEYLISRVDLNDDGRVQFNEFFMHLDGVSKDHIKQQFMAIDKDKNGKISPEEMVFGITKIYRQMVDFEVAKLIKESSFEDDDGYINFNEFVNRFFSNCPYKINSLYWPIYLGCAVSI");
      // Hit hit2 = new Hit();
      // hit2
      // .setFasta(">gi|23510644|emb|CAD49029.1| centrin, putative [Plasmodium
      // falciparum 3D7]\n"
      // +
      // "MSRKNQTMIRNPNPRSKRNELNEEQKLEIKEAFDLFDTNGTGRIDAKELKVAMRALGFEPKKEDIRKIISDVDKDGSGTIDFNDFLDIMTIKMSERDPKEEILKAFRLFDDDETGKISFKNLKRVAKELGENITDEEIQEMIDEADRDGDGEINEEEFMRIMKKTNLF");
      //        
      // long start = System.currentTimeMillis();
      // equals(hit1, hit2, null);
      // long end = System.currentTimeMillis();
      // System.out.println("time="+(end - start));
   }

}
