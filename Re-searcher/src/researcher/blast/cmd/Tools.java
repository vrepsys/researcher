package researcher.blast.cmd;

import org.apache.log4j.Logger;

import researcher.ssh.SshClient;
import researcher.ssh.SshClient.SshException;
import researcher.ssh.SshClient.SshResult;
import researcher.utils.Utils;


public class Tools {

    private SshClient sshClient;

    public Tools(SshClient connection) {
        if (!connection.isConnected()) {
            throw new RuntimeException("SshConnection is not connected");
        }
        this.sshClient = connection;
    }
    
    public void deleteFile(String filename){
    	this.sshClient.deleteFileFromServer(filename);
    }

    public PsiBlastResult psiBlast(PsiBlastCommand cmd) throws ToolsException {
        SshResult sshResult;
        try {
            sshResult = sshClient.invoke(cmd.toString());
        } catch (SshException e1) {
            throw new ToolsException(e1);
        }
        String xml = sshResult.getStdout();
        researcher.utils.Utils.saveToDesktop("psiblast_xml"+System.currentTimeMillis()+".html", xml);
        String errors = sshResult.getStderr();
        PsiBlastResult res;
        try {
            res = PsiBlastXmlParser.parsePsiBlastXML(xml);
        } catch (PsiBlastXmlParseException e) {
            if (!errors.trim().equals("")) {
                return new PsiBlastResult(errors);
            }
            throw new ToolsException(e);
        }
        return res;
    }

    public String getFasta(String fastaCmdPath, String accessionNumber, String dbPath, Logger log) {
        if (fastaCmdPath == null || accessionNumber == null || dbPath == null)
            throw new NullPointerException("fastaCmdPath=" + fastaCmdPath + " accessionNumber="
                    + accessionNumber + " database=" + dbPath);        
        accessionNumber = accessionNumber.trim();
        String cmd = fastaCmdPath + " -d " + dbPath + " -s " + accessionNumber;
        cmd = Utils.escapeDashes(cmd);
        log.info("invoke: " + cmd);
        SshResult sshResult = sshClient.invoke(cmd);
        log.info("invoke END");
        if (sshResult.hasErrors()) {
            log.error(sshResult.getStderr());
            return null;
        }
        log.info("result: " + sshResult.getStdout());
        return sshResult.getStdout();
    }

    public static class ToolsException extends RuntimeException {

        private static final long serialVersionUID = 6422302806298912940L;

        public ToolsException() {
            super();
        }

        public ToolsException(String message, Throwable cause) {
            super(message, cause);
        }

        public ToolsException(String message) {
            super(message);
        }

        public ToolsException(Throwable cause) {
            super(cause);
        }
    }

//    public static void main(String[] args) {
//        System.out.println("labas");
//        SshClient client = new SshClient("banginis", "valdemaras", "/home/posu/.ssh/id_rsa", "");
//        client.connect();
//        Tools tools = new Tools(client);
//        for (int i = 0; i < 100; i++) {
//            long start = System.currentTimeMillis();
//            tools.getFasta("/usr/local/bin/fastacmd", "CAA75881", "/usr/data/blast/db/nr90");
//            long end = System.currentTimeMillis();
//            System.out.print("time=" + (end - start));
//        }
//        client.disconnect();
//    }

}
