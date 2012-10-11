package researcher.blast.exec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import researcher.beans.BlastQuery;
import researcher.blast.BlastTypes.HitDetails;
import researcher.blast.periodical.NCBISearchJob;
import researcher.blast.qblast.PsiBlastTextParseException;
import researcher.blast.qblast.PsiBlastTextParser;
import researcher.blast.qblast.QBlast;
import researcher.blast.qblast.QBlastSearchException;
import researcher.utils.SeqUtil;
import researcher.utils.Utils;

public class QBlastExecutor implements PsiBlastExecutor {

   public static Logger log = NCBISearchJob.log;

   public QBlastExecutor() {
   }

   public PsiBlastExecutorResult execute(BlastQuery query) {
      try {
         String res;
         if (query.isDoubleSearch()) {
            String pssm = QBlast.executePrimarySearch(query);
            if (pssm == null) {
               log
                     .info("No hits to generate a pssm. Secondary search will not be executed.");
               return new PsiBlastExecutorResult(new ArrayList<HitDetails>());
            }
            pssm = QBlast.extractPssm(pssm);
            res = QBlast.executeSecondarySearch(query, pssm);
         } else {
            res = QBlast.executeSearch(query);
         }
         return getResult(res);
      } catch (QBlastSearchException e) {
         log.info(e.getMessage());
         return new PsiBlastExecutorResult(e.getMessage());
      }
   }

   private PsiBlastExecutorResult getResult(String xml) {
      try {
         List<HitDetails> hitDetails = PsiBlastTextParser.parse(xml);
         log.info("parsed hit details, hits.size=" + hitDetails.size());
         return new PsiBlastExecutorResult(hitDetails);
      } catch (PsiBlastTextParseException e) {
         log.error("could parse resulting web-siblast text", e);
         // TODO: remove the following line
         Utils.saveToDesktop("erroneous_document.txt", xml);
         return new PsiBlastExecutorResult(e.getMessage());
      }
   }

   @SuppressWarnings("deprecation")
   public List<String> getFastas(List<String> ids, String database) {
      List<String> result = new ArrayList<String>();
      int from = 0;
      int to = 0; 
      do {
         from = to;
         to = ids.size() > to + 100 ? to + 100 : ids.size();
         List<String> subids = ids.subList(from, to);
         List<String> fastas = null;
         int retries = 1;
         do {
           try {
              fastas = downloadFastas(subids);
           }
           catch(IOException e) {
              log.info("download fastas failed, retrying. Exc: /n" + e.getStackTrace());
            try {
               Thread.sleep(retries*4000);
            } catch (InterruptedException e1) {
               e1.printStackTrace();
            }
            retries++;
           }
         } while (fastas == null && retries < 6);
         if (fastas == null) throw new RuntimeException("getFastas Failed after 5 retries.");
         result.addAll(fastas);
      } while (to != ids.size());
      return result;
   }
   
   public List<String> downloadFastas(List<String> ids) throws IOException {
      try {
         if (ids.size() == 0)
            return new ArrayList<String>();
         StringBuffer idsBuffer = new StringBuffer();
         for (String id : ids) {
            if (id == null || id.length() == 0)
               throw new RuntimeException("id can not be null or empty!!");
            idsBuffer.append(id);
            idsBuffer.append(",");
         }
         idsBuffer.deleteCharAt(idsBuffer.length() - 1);
         URL url = new URL(
               "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi");
         // ?db=protein&id="+idsBuffer.toString()+"&rettype=fasta&retmode=text
         HttpURLConnection con = (HttpURLConnection) url.openConnection();
         con.setRequestMethod("POST");
         con.setDoOutput(true);
         con.setDoInput(true);
         String data = "db=protein&id=" + idsBuffer.toString()
               + "&rettype=fasta&retmode=text";
         OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
         wr.write(data);
         wr.close();
         con.connect();
         InputStream is = (InputStream) con.getContent();
         String fastas = QBlast.readToString(is);
         is.close();
         con.disconnect();
         BufferedReader reader = new BufferedReader(new StringReader(fastas));
         String line;
         StringBuffer fasta = new StringBuffer();
         List<String> result = new ArrayList<String>();
         while ((line = reader.readLine()) != null) {
            if (line.equals("")) {
               result.add(fasta.toString());
               fasta = new StringBuffer();
            } else {
               if (fasta.length() == 0) {
                  line += "\n";
               }
               fasta.append(line);
            }
         }
         return result;
       }
       catch(MalformedURLException e) {
          throw new RuntimeException("Error trying to downlaod fastas", e);
       }
   }

   public static void main(String[] args) throws IOException {

//      GlobalDAO dao = new GlobalDAO();
//
//      List<Hit> hits = dao.getAllHits();
      
      
      File file = new File("/home/posu/Desktop/get.txt");
      FileInputStream input = new FileInputStream(file);
      String txt = QBlast.readToString(input);
      List<HitDetails> hits = PsiBlastTextParser.parse(txt);
      
      List<String> ids = new ArrayList<String>();
      List<HitDetails> newHits = new ArrayList<HitDetails>();
      for (HitDetails hit : hits) {
         String id = SeqUtil.extractFastaId(hit.getHitId());
         if (id != null) {
            System.out.println(id +": " + hit.getHitId());
            ids.add(id);
            newHits.add(hit);
         }
         else {
            log.warn("no id for subjid= " + hit.getHitId());
         }
      }
      
//      for (String id : ids) {
//         List<String> tmp = new ArrayList<String>();
//         tmp.add("pdb|1RDR|");
//         System.out.println(id);
//         System.out.println(new QBlastExecutor().getFastas(tmp));
//         System.out.println();
//      }
      

      List<String> fastas = new QBlastExecutor().getFastas(ids, "");

      System.out.println("ids.size=" + ids.size());
      System.out.println("fastas.size=" + fastas.size());
      System.out.println("newhits.size=" + newHits.size());
      
      for (int i = 0; i < newHits.size(); i++) {
         System.out.println(ids.get(i));
         System.out.println(fastas.get(i));
         System.out.println();
      }

//      dao.close();


      // try {
      // String id = getEntrezId("gb|ABQ69937.1| RNA polymerase, sigma-24
      // subunit, ECF subfamily [Sphingomonas wittichii RW1]");
      // System.out.println(id);
      // URL url = new
      // URL("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=protein&id="+id+"&rettype=fasta&retmode=text");
      // HttpURLConnection con = (HttpURLConnection) url.openConnection();
      // con.connect();
      // System.out.println(con.getContentType());
      // InputStream is = (InputStream) con.getContent();
      // System.out.println(QBlast.readToString(is));
      // is.close();
      // con.disconnect();
      // } catch (Exception e) {
      // e.printStackTrace();
      // }

   }
}
