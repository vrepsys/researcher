package researcher.ssh;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import researcher.exceptions.SshAuthenticationException;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class SshClient {

    private String hostname;

    private String username;

    private String pathToPrivateKey;

    private String passPhrase;

    private Writer out;

    private StreamGobbler in;

    private StreamGobbler err;

    private Connection connection;

    private Session sshSession;

    private boolean connected = false;

    private boolean sshSessionEstablished = false;

    private SCPClient scpClient;

    private boolean sftpClientOpened = false;
    
    private String password;

    private static final String ENDING = "ThE_eNd#-@";

    public class SshResult {

        private String stdout;

        private String stderr;

        SshResult(String output, String errors) {
            this.stdout = output;
            this.stderr = errors;
        }

        public String getStderr() {
            return stderr;
        }

        public String getStdout() {
            return stdout;
        }

        public boolean hasErrors() {
            if (stderr == null) {
                return false;
            }
            if (stderr.length() == 0) {
                return false;
            }
            return true;
        }
    }

    public class SshException extends RuntimeException {

        private static final long serialVersionUID = -2593262867311078241L;

        public SshException() {
            super();
        }

        public SshException(String message, Throwable cause) {
            super(message, cause);
        }

        public SshException(String message) {
            super(message);
        }

        public SshException(Throwable cause) {
            super(cause);
        }
    }

    /**
     * @param hostname
     *            blast server hostname
     * @param username
     *            ssh username
     * @param pathToPrivateKey
     *            path to the private key file in local computer
     * @param passPhrase
     *            passphrase for the private key
     */
    public SshClient(String hostname, String username, String pathToPrivateKey, String passPhrase) {
        if (hostname == null)
            throw new NullPointerException("parameter hostname can not be null");
        if (username == null)
            throw new NullPointerException("parameter username can not be null");
        if (pathToPrivateKey == null)
            throw new NullPointerException("parameter pathToPrivateKey can not be null");
        if (passPhrase == null)
            throw new NullPointerException("passPhrase can not be null");
        this.hostname = hostname;
        this.username = username;
        this.pathToPrivateKey = pathToPrivateKey;
        this.passPhrase = passPhrase;
    }
    
    public SshClient(String hostname, String username, String password) {
        if (hostname == null)
            throw new NullPointerException("parameter hostname can not be null");
        if (username == null)
            throw new NullPointerException("parameter username can not be null");
        if (password == null)
            throw new NullPointerException("parameter password can not be null");
        this.hostname = hostname;
        this.username = username;
        this.password = password;
    }

    /**
     * Connects to ssh server
     * 
     */
    public void connect() {
        connection = new Connection(hostname);
        try {
            connection.connect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        boolean isAuthenticated;
        if (password != null) {
            try {
                isAuthenticated = connection.authenticateWithPassword(username, password);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }            
        }
        else {
            try {
                isAuthenticated = connection.authenticateWithPublicKey(username, new File(
                        pathToPrivateKey), passPhrase);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (isAuthenticated == false)
            throw new SshAuthenticationException("Ssh authentication failed, host="+hostname+" uname="+username);
        connected = true;
    }

    private void establishSshSession() throws IOException {
        if (sshSessionEstablished)
            throw new RuntimeException("ssh connection already established");
        sshSession = connection.openSession();
        sshSession.startShell();
        out = new OutputStreamWriter(sshSession.getStdin());
        in = new StreamGobbler(sshSession.getStdout());
        err = new StreamGobbler(sshSession.getStderr());
        sshSessionEstablished = true;
    }

    private void openSftpClient() throws IOException {
        if (sftpClientOpened)
            throw new RuntimeException("sftp client already opened");
        scpClient = new SCPClient(connection);
        sftpClientOpened = true;
    }

    /**
     * disconnects from the ssh server
     * 
     * @throws IOException
     */
    public void disconnect() {
        if (connected) {
            if (sshSessionEstablished) {
                // we get in, out and err from sshSession..
                // so it should close there if needed
                try {
                    out.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                sshSession.close();
                sshSessionEstablished = false;
            }

            if (sftpClientOpened) {
                sftpClientOpened = false;
            }

            connection.close();

            connected = false;
        }
    }

    /**
     * Executes psi blast command on the ssh server
     * 
     * @param cmd
     * @return text returned by the executed command
     * @throws IOException
     */
    public SshResult invoke(String cmd) throws SshException {
        if (!connected) {
            throw new RuntimeException("not connected, connect first");
        }
        if (!sshSessionEstablished) {
            try {
                establishSshSession();
            } catch (IOException e) {
                throw new SshException("Exception while executing psi-blast command", e);
            }
        }
        String command = cmd + "\n echo " + ENDING + "\n";
        StringBuffer result = new StringBuffer();
        StringBuffer errors = new StringBuffer();
        try {
            out.write(command);
            out.flush();
            byte buffer[] = new byte[255];
            int read;
            while (true) {
                if (err.available() > 0) {
                    read = err.read(buffer);
                    if (read < 0)
                        break;
                    String output = new String(buffer, 0, read);
                    errors.append(output);
                }
                if (in.available() > 0 && !(err.available() > 0)) {
                    read = in.read(buffer);
                    if (read < 0)
                        break;
                    String output = new String(buffer, 0, read);
                    result.append(output);
                } else {
                    if (result.length() >= ENDING.length()) {
                        int index = result.length() - (ENDING.length() + 1);
                        String ending = result.substring(index);
                        if (ending.contains(ENDING)) {
                            int ind = ending.indexOf(ENDING);
                            index += ind;
                            return new SshResult(result.substring(0, index), errors.toString());
                        }
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        } catch (IOException e1) {
            throw new SshException("Exception while executing psi-blast command", e1);
        }
        return new SshResult(result.toString(), errors.toString());
    }
    
    public String deleteFileFromServer(String filename){
        if (!connected) {
            throw new RuntimeException("not connected, connect first");
        }
        if (!sshSessionEstablished) {
            try {
                establishSshSession();
            } catch (IOException e) {
                throw new SshException("Exception while establishing ssh connection", e);
            }
        }        
        StringBuffer output = new StringBuffer();
        try {
            out.write("rm " + filename + "\n");
            out.flush();            
            byte buffer[] = new byte[255];
            try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            while(in.available() > 0) {
            	in.read(buffer);
            	output.append(buffer);
            }
            while(err.available() > 0) {
            	err.read(buffer);
            	output.append(buffer);
            }            
        } catch (IOException e1) {
            throw new SshException("Exception while deleting a file", e1);
        }
        return output.toString();
    }

    /**
     * Copies a file to ssh server
     * 
     * @param is
     * @param remoteFileName
     * @param remoteDirectory
     * @throws SshException
     */
    public void copyFileToServer(byte[] bytes, String remoteFileName, String remoteDirectory)
            throws SshException {
        if (!sftpClientOpened) {
            try {
                openSftpClient();
            } catch (IOException e) {
                throw new SshException("Exception while trying to copy the file to server", e);
            }
        }
        try {
            scpClient.put(bytes, remoteFileName, remoteDirectory);
        } catch (IOException e) {
            throw new SshException("Exception while trying to copy the file to server", e);
        }
    }

    public boolean isConnected() {
        return connected;
    }
    
    public static void main(String[] args) {
        SshClient client = new SshClient("localhost", "posu", "LeiPhei4");
        client.connect();
        System.out.println(client.deleteFileFromServer("/home/posu/testfile"));
        client.disconnect();
    }


}
