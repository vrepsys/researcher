package researcher.test.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import org.hibernate.Session;

import researcher.beans.FastaSequence;

public class SaveFastasToFile {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        
        System.setProperty("derby.system.home", "/home/posu/projects/Starter/db/");
        Session s = TestHibUtil.getSession();
        s.beginTransaction();
        
        List<FastaSequence> fastas = s.createQuery("from FastaSequence").list();
        
        s.getTransaction().commit();
        s.close();
        
        Properties props = new Properties();
        for (FastaSequence fasta : fastas) {
            props.put(fasta.getSequenceId(), fasta.getFasta());
        }
        props.storeToXML(new FileOutputStream("query2_fastas.xml"), "query2 fastas");
        
    }
    
}
