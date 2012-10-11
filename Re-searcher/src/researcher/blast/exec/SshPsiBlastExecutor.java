package researcher.blast.exec;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import researcher.beans.BlastQuery;
import researcher.beans.Configuration;
import researcher.beans.Hit;
import researcher.blast.BlastTypes.HitDetails;
import researcher.blast.cmd.PsiBlastCommand;
import researcher.blast.cmd.PsiBlastCommandFactory;
import researcher.blast.cmd.PsiBlastResult;
import researcher.blast.cmd.PsiblastExecutionException;
import researcher.blast.cmd.Tools;
import researcher.blast.periodical.LocalSearchJob;
import researcher.db.GlobalDAO;
import researcher.ssh.SshClient;
import researcher.utils.SeqUtil;


public class SshPsiBlastExecutor implements PsiBlastExecutor {

    private static Logger log = LocalSearchJob.log;

    private Configuration conf;

    private SshClient sshConnection;

    private Tools tools;

    public SshPsiBlastExecutor(Configuration conf) {
        if (conf == null)
            throw new NullPointerException();
        this.conf = conf;
    }

    private void initialize() {
        if (sshConnection == null) {
            if (conf.getSshPassword() != null) {
                sshConnection = new SshClient(conf.getBlastHostName(), conf.getBlastUserName(), conf.getSshPassword());
            }
            else {
                sshConnection = new SshClient(conf.getBlastHostName(), conf.getBlastUserName(), conf
                        .getPathToPrivateKey(), conf.getPassphrase());
            }
            sshConnection.connect();
            
            tools = new Tools(sshConnection);
        }
    }

    public void close() {
        if (sshConnection != null && sshConnection.isConnected()) {
            sshConnection.disconnect();
        }
    }

    public PsiBlastExecutorResult execute(BlastQuery query) throws PsiblastExecutionException {

        initialize();
        
        PsiBlastCommand cmd;
        
        String inputAlignmentFile = null;
        String checkPointFile = null;
        if (query.isDoubleSearch()) {
            log.info("DOUBLE SEARCH");
            PsiBlastCommand tmp = PsiBlastCommandFactory.constructPrimary(query, conf);
            log.info(tmp);
            PsiBlastResult res = tools.psiBlast(tmp);
            if (res.hasErrors()) {
                String errs = res.getErrors();
                log.warn("error on first search, query_id=" + query.getId() + " errors="
                        + res.getErrors());
                return new PsiBlastExecutorResult(errs);
            }
            if (res.hasWarnings()) {
                log.warn("warnings on first search, query_id=" + query.getId() + " warnings="
                        + res.getErrors());
            }
            log.info("SUCCESSFULLY finished the first search");
            checkPointFile = tmp.getCheckpointFileOutput();
            cmd = PsiBlastCommandFactory.constructSecondary(query, conf, checkPointFile);
        } else {
            log.info("UNARY SEARCH");
            cmd = PsiBlastCommandFactory.construct(query, conf);
        }

        if (query.getInputAlignmentFile() != null) {            
            GlobalDAO dao = new GlobalDAO();
            try {
                query = dao.getQuery(query.getId());
                Blob file = query.getInputAlignmentFile();
                byte[] bytes = file.getBytes(1, (int) file.length());
                inputAlignmentFile = conf.generateInputAlignmentFileName();
                String inputAlignmentDirectory = conf.getInputAlignmentDirectory();
                sshConnection.copyFileToServer(bytes, inputAlignmentFile, inputAlignmentDirectory);
                cmd.setInputAlignmentFile(inputAlignmentDirectory + inputAlignmentFile);
            } catch (SQLException e) {
                throw new PsiblastExecutionException(e);
            } finally {                
                dao.close();
            }
        }

        log.info(cmd);

        PsiBlastResult blastResult = tools.psiBlast(cmd);

        if (inputAlignmentFile != null)
        	tools.deleteFile(conf.getInputAlignmentDirectory() + inputAlignmentFile);
        
        if (checkPointFile != null)
        	tools.deleteFile(checkPointFile);
        
        if (blastResult.hasErrors()) {        	
            String errs = blastResult.getErrors();
            return new PsiBlastExecutorResult(errs);
        }

        List<HitDetails> hitDetails = blastResult.getHitDetails();

        return new PsiBlastExecutorResult(hitDetails);
    }
    

   public List<String> getFastas(List<String> ids, String dbPath) {
      if (ids == null || dbPath == null)
          throw new NullPointerException(" ids=" + ids + " database=" + dbPath);
      if (conf.getFastaCmdCommand() == null)
          throw new RuntimeException("conf.getFastaCmdCommand() == null");
      
      initialize();
      
      List<String> result = new ArrayList<String>();
      
      for (String id : ids) {
        
        String fasta = tools.getFasta(conf.getFastaCmdCommand(), id,
                dbPath, log);
        result.add(fasta);
        
      }
      
      
      return result;
   }

   
   public static void main(String[] args) {
      GlobalDAO dao = new GlobalDAO();
      BlastQuery query = dao.getQuery(1900544L);
      Set<Hit> hits = query.getHits();
      
      List<String> ids = new ArrayList<String>();
      List<Hit> newHits = new ArrayList<Hit>();
      for (Hit hit : hits) {
         String id = SeqUtil.extractFastaId(hit.getSubjectId());
         if (id != null) {
            ids.add(id);
            newHits.add(hit);
         }
         else {
            log.warn("no id for subjid= " + hit.getSubjectId());
         }
      }
      
      SshPsiBlastExecutor executor = new SshPsiBlastExecutor(dao.getConfiguration());
      
      executor.getFastas(ids, "/home/posu/documents/blastdb/nr");
      
      dao.close();
   }
   
}
