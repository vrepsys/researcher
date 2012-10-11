package researcher.blast.qblast;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import researcher.qblast.parameters.Params;
import researcher.utils.Utils;

/**
 * Pastabos: 1. kazkodel formu parseris nepagauna selectu default reiksmes tuo
 * atveju jeigu default reiksme nenurodyta, juk turetu tada paimti pirma reiksme
 * 
 * 
 * @author posu
 * 
 */

public class Tests {

   static String seq = "MAKLIPTIALVSVLLFIIANASFAYRTTITTIEIDESKGEREGSS"
         + "SQQCRQEVQRKDLSSCERYLRQSSS RRSPGEEVLRMPGDENQQ"
         + "QESQQLQQCCNQVKQVRDECQCEAIKYIAEDQIQQGQLHGEESER"
         + "VAQRAGE IVSSCGVRCMRQTRTNPSQQGCRGQIQEQQNLRQCQ"
         + "EYIKQQVSGQGPRRSDNQERSLRGCCDHLKQMQS QCRCEGLRQ"
         + "AIEQQQSQGQLQGQDVFEAFRTAANLPSMCGVSPTECRF";

   public static void main(String[] args) throws IOException, QBlastSearchException {
      // Extract the parameters from two documents
      // (one from ncbi web and another from researcher)
      // and compare the parameters to see if they match.
      // The two documents are the result of the first put (parameters of get)

      // Re-searcher document

      // PUT 1
//      List<Parameter> params = QBlast.getStartParameters();
//      QBlastCommand cmd = new QBlastCommand(params);
//      cmd.setParameter(Params.QUERY, seq);
//      cmd.setParameter(Params.DATABASE, "nr");
//      cmd.setParameter(Params.I_THRESH, "0.005");
//      cmd.setParameter(Params.SELECTED_PROG_TYPE, "psiBlast");
//      String html = QBlast.invoke(cmd);
      String html = Utils.readTextFile("/home/posu/Desktop/invokeFirstPut.html");
      List<Parameter> researcherParams = FormParser.getFormatFormElements(html);
      Collections.sort(researcherParams);
      // GET 1
//      QBlastCommand getCmd = new QBlastCommand(researcherParams);
      // getCmd.setParameter(Params.ALIGNMENTS,
      // QBlastCommandFactory.MAX_NUMBER_OF_HITS);

//      html = QBlast.invoke(getCmd, 2);
//      QBlast.checkError(html);

//      researcherParams = FormParser.getNextIterFormElements(html);
//      Collections.sort(researcherParams);
//
//      Utils.saveToDesktop("researcher.html", html);

      // Ncbi document
      html = Utils.readTextFile("/home/posu/Desktop/ncbi.html");
      List<Parameter> ncbiParams = FormParser.getFormatFormElements(html);
      Collections.sort(ncbiParams);

      compareParameters(ncbiParams, researcherParams);
      
      


      

   }
   
   
   static void compareParameters(List<Parameter> ncbiParams, List<Parameter> researcherParams) {
      System.out.println("Ncbi params:       size=" + ncbiParams.size());
      System.out.println("Researcher params: size=" + researcherParams.size());

      int ncbiSize = ncbiParams.size();
      int resSize = researcherParams.size();

      int r = 0;
      int n = 0;

      System.out.println("ncbiParams : researcherParams");
      while (n < ncbiSize && r < resSize) {

         String nparam = ncbiParams.get(n).getName();
         String rparam = researcherParams.get(r).getName();
         String nvalue = ncbiParams.get(n).getValue();
         String rvalue = researcherParams.get(r).getValue();

         if (nparam.equals(rparam)) {
            if (!nvalue.equals(rvalue)) {
               System.out.println(nparam + "  : " + rparam + "  N");
               System.out.println("++++++++++++++++++++++");
               System.out.println("___ncbivalue=" + nvalue);
               System.out.println("___resvalue=" + rvalue);
               System.out.println("++++++++++++++++++++++");
            } else {
               System.out.println(nparam + "  : " + rparam);
            }

            n++;
            r++;
         } else if (String.CASE_INSENSITIVE_ORDER.compare(nparam, rparam) < 0) {
            System.out.println(nparam + " : ---");
            n++;
         } else {
            System.out.println("--- : " + rparam);
            r++;
         }
      }
      if (n == ncbiSize && r < resSize) {
         while (r < resSize) {
           String rparam = researcherParams.get(r).getName();
           System.out.println("--- : " + rparam);
           r++;
         }
      }
      else if (r == resSize && n < ncbiSize) {
         while (n < ncbiSize) {
            String nparam = ncbiParams.get(n).getName();
            System.out.println(nparam + " : ---");
            n++;
          }
       }
   }

}
