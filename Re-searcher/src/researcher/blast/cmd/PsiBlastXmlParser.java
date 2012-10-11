package researcher.blast.cmd;

import java.io.IOException;
import java.io.StringReader;

import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;


public class PsiBlastXmlParser {

    public static PsiBlastResult parsePsiBlastXML(String xml) throws PsiBlastXmlParseException {
    	if (xml.trim().equals("")) throw new PsiBlastXmlParseException("can not parse empty file"); 
        SAXParser parser = new SAXParser();
        try {
            parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",
                    false);
        } catch (SAXNotRecognizedException e) {
            throw new PsiBlastXmlParseException(e);
        } catch (SAXNotSupportedException e) {
            throw new PsiBlastXmlParseException(e);
        }
        PsiBlastResultHandler handler = new PsiBlastResultHandler();
        parser.setContentHandler(handler);
        try {
            parser.parse(new InputSource(new StringReader(xml)));
        } catch (SAXException e) {
            throw new PsiBlastXmlParseException(e);
        } catch (IOException e) {
            throw new PsiBlastXmlParseException(e);
        }
        PsiBlastResult res = handler.getResult();
        return res;
    }

}
