package researcher.blast.qblast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import researcher.beans.BlastQuery;
import researcher.blast.periodical.NCBISearchJob;
import researcher.blast.qblast.QBlastCommand.OutputType;
import researcher.db.GlobalDAO;
import researcher.qblast.parameters.Params;
import researcher.utils.Utils;

public class QBlast {

   private static Logger log = NCBISearchJob.log;

   private static void wait(int seconds) {
      try {
         log.info("waiting " + seconds + "s");
         Thread.sleep(seconds * 1000);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }

   static String invoke(QBlastCommand cmd) {
      return invoke(cmd, 10);
   }

   private static final Pattern noNewSeqPattern = Pattern
         .compile("No new sequences were found above the");

   private static boolean noNewSequencesFound(String html) {
      Matcher m = noNewSeqPattern.matcher(html);
      if (m.find())
         return true;
      return false;
   }

   static String invoke(QBlastCommand cmd, int initialWaitTime) throws QBlastException {
      log.info("Enter invoke, cmd=" + cmd);
      PostMethod pm = null;
      try {
         HttpClient client = new HttpClient();
         pm = new PostMethod(cmd.getUrl());
        
         List<Parameter> params = cmd.getParameters();
                  
         for (Parameter p : params) {
            pm.addParameter(p.getName(), p.getValue());
//            log.info(p.getName() + " : " + p.getValue());
         }
         boolean gotResults = false;
         String str = null;
         while (!gotResults) {
            client.executeMethod(pm);
            InputStream stream = pm.getResponseBodyAsStream();
            str = readToString(stream);
            if (!str.contains("Status=WAITING")) {
               gotResults = true;
            } else {
               log.info("no results");
               initialWaitTime += 2;
               wait(initialWaitTime);
            }
         }
         pm.releaseConnection();
         return str;
      } catch (MalformedURLException e) {
         throw new QBlastException(e);
      } catch (ProtocolException e) {
         throw new QBlastException(e);
      } catch (IOException e) {
         throw new QBlastException(e);
      } catch (NumberFormatException e) {
         throw new QBlastException(e);
      } finally {
         if (pm != null) {
            pm.releaseConnection();
         }
         log.info("Exit invoke");
      }
   }

   public static String readText(String url) {
      try {
         PostMethod pm = null;
         HttpClient client = new HttpClient();
         pm = new PostMethod(url);
         client.executeMethod(pm);
         InputStream stream = pm.getResponseBodyAsStream();
         String str = readToString(stream);
         pm.releaseConnection();
         return str;
      } catch (IOException e) {
         e.printStackTrace();
      }
      return null;
   }

   public static String readToString(InputStream stream) throws IOException {
      Reader reader = new InputStreamReader(stream, "UTF-8");
      StringBuffer strBuf = new StringBuffer();
      char buf[] = new char[4096];
      int n;
      while ((n = reader.read(buf)) > 0) {
         if (n != 0) {
            strBuf.append(buf, 0, n);
         } else {
            try {
               Thread.sleep(1000);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }
      }
      reader.close();
      String str = strBuf.toString();
      return str;
   }

   static final String mainPsiblastUrl = "http://www.ncbi.nlm.nih.gov/BLAST/Blast.cgi?PAGE=Proteins&PROGRAM=blastp&BLAST_PROGRAMS=blastp&PAGE_TYPE=BlastSearch&SHOW_DEFAULTS=on";


   static void replaceParameter(List<Parameter> params, String name, String newValue) {
      Parameter paramToDel = null;
      for (Parameter p : params) {
         if (p.getName().equals(name)) {
            paramToDel = p;
            break;
         }
      }
      if ( paramToDel != null) {
         params.remove(paramToDel);
      }
      params.add(new Parameter(name, newValue));
   }
   
   static List<Parameter> getStartParameters() {
      String html = readText(mainPsiblastUrl);
      List<Parameter> params = FormParser.getFirstFormElements(html);
      if (params != null) {
         replaceParameter(params, Params.BLAST_PROGRAMS.toString(), "psiBlast");
         replaceParameter(params, Params.SELECTED_PROG_TYPE.toString(), "psiBlast");  
//         replaceParameter(params, Params.USER_DEFAULT_PROG_TYPE.toString(), "psiBlast");
//         replaceParameter(params, Params.RUN_PSIBLAST.toString(), "on");
//         replaceParameter(params, Params.SERVICE.toString(), "psi");
      }
      return params;
   }

   /**
    * 
    * @param firstCommand
    * @param iterations
    * @param resultType
    *           must be PSSM or XML
    * @return
    * @throws QBlastSearchException
    */
   private static String executeSearch(QBlastCommand firstCommand, int iterations, double evalue,
         QBlastCommand.OutputType outputType) throws QBlastSearchException {

      String html = QBlast.invoke(firstCommand);
      checkError(html);

      Utils.saveToDesktop("invokeFirstPut.html", html);

      Integer waitTime = QBlastParser.parseRtoe(html);
      if (waitTime == null)
         waitTime = 10;

      List<Parameter> params = FormParser.getFormatFormElements(html);

      // log.info("params after first invoke: "+ params);

      int maxNumberOfHits = firstCommand.getMaxNumberOfHits();
      
      QBlastCommand getCmd = new QBlastCommand(params);
      getCmd.setParameter(Params.ALIGNMENTS, String.valueOf(maxNumberOfHits));

      if (iterations == 1)
         getCmd.setOutput(outputType);

      html = QBlast.invoke(getCmd, 2);
      checkError(html);

      if (PsiBlastTextParser.noSignificantSimilarytiFound(html)) {
         return html;
      }

      Utils.saveToDesktop("invokeFirstGet.html", html);

      if (iterations == 1)
         return html;

      for (int i = 1; i < iterations; i++) {

         params = FormParser.getNextIterFormElements(html);
         QBlastCommand iterPut = new QBlastCommand(params);
         log.info("iteration: " + i);
         html = QBlast.invoke(iterPut);
         checkError(html);

         waitTime = QBlastParser.parseRtoe(html);
         if (waitTime == null)
            waitTime = 10;

         Utils.saveToDesktop("put_" + i + ".html", html);

         params = FormParser.getFormatFormElements(html);
         if (params == null) {
            if (html.contains("Error 502")) {
               // TODO: this is not a very elegant way to retry
               throw new RuntimeException("NCBI busy, will retry");
            }
            else {
               throw new QBlastSearchException("Error while parsing result from ncbi psiblast");
            }
         }
         QBlastCommand iterGet = new QBlastCommand(params);
       iterGet.setParameter(Params.ALIGNMENTS, String.valueOf(maxNumberOfHits));

         if ((i + 1) == iterations) {
            iterGet.setOutput(outputType);
         }

         html = QBlast.invoke(iterGet, waitTime);
         checkError(html);
         Utils.saveToDesktop("get_" + i + ".html", html);

         if (noNewSequencesFound(html)) {
            log.info("No new hits, returning");
            if (outputType == OutputType.HTML) {
               return html;
            } else {
               iterGet.setOutput(outputType);
               String output = QBlast.invoke(iterGet, 2);
               checkError(output);
               return output;
            }
         }

      }
      Utils.saveToDesktop("final_stuff.html", html);
      return html;
   }

   public static String extractPssm(String text) throws QBlastSearchException {
      int i = text.indexOf("PSSM:");
      if (i == -1) {
         throw new QBlastSearchException("PSSM could not be extracted from blast result: " + text);
      }
      return text.substring(i);
   }

   static void checkError(String text) throws QBlastSearchException {
      Pattern p = Pattern.compile("<font color=\"red\">(ERROR:[^<]+)</font>");
      Matcher m = p.matcher(text);
      if (m.find()) {
         throw new QBlastSearchException(Utils.htmlToText(m.group(1)));
      }
   }

   public static String executeSearch(BlastQuery query) throws QBlastSearchException {
      List<Parameter> params = QBlast.getStartParameters();
      log.info("initial qblast parameters: " + params);
      QBlastCommand putCmd = QBlastCommandFactory.constructQBlastCommand(query, params);
      log.info("parameters after modification: " + putCmd.getParameters());
      return executeSearch(putCmd, query.getIterations(), query.getEvalue(), OutputType.TEXT);
   }

   /**
    * @param query
    * @return PSSM or null if no significant similarity found
    * @throws QBlastSearchException
    */
   public static String executePrimarySearch(BlastQuery query) throws QBlastSearchException {
      List<Parameter> params = QBlast.getStartParameters();
      QBlastCommand putCmd = QBlastCommandFactory.constructQBlastCommand(query, params);
      String result = executeSearch(putCmd, query.getIterations(), query.getEvalue(),
            OutputType.PSSM);
      if (PsiBlastTextParser.noSignificantSimilarytiFound(result))
         return null;
      return result;
   }

   public static String executeSecondarySearch(BlastQuery query, String pssm)
         throws QBlastSearchException {
      List<Parameter> params = QBlast.getStartParameters();
      QBlastCommand putCmd = QBlastCommandFactory.constructQBlastCommandForSecondaryQuery(query,
            params, pssm);
      return executeSearch(putCmd, 1, query.getSecondaryQuery().getEvalue(), OutputType.TEXT);
   }

   public static void main(String[] args) throws IOException {

      
      
      GlobalDAO dao = new GlobalDAO();
      BlastQuery query = dao.getQuery(65536L);

      try {
         executeSearch(query);
      } catch (QBlastSearchException e) {
         e.printStackTrace();
      }
      dao.close();

   }

}
