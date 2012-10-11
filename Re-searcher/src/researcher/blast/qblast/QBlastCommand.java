package researcher.blast.qblast;

import java.util.ArrayList;
import java.util.List;

import researcher.qblast.parameters.Params;
import researcher.qblast.parameters.QBlastFormatObject;
import researcher.qblast.parameters.QBlastFormatType;


public class QBlastCommand {

    private final String url = "http://www.ncbi.nlm.nih.gov/BLAST/Blast.cgi";
    
    private int maxNumberOfHits;

    public int getMaxNumberOfHits() {
      return maxNumberOfHits;
   }

   public void setMaxNumberOfHits(int maxNumberOfHits) {
      this.maxNumberOfHits = maxNumberOfHits;
   }

   public String getUrl() {
        return url;
    }

    private QBlastCommand() {

    }

    public QBlastCommand(List<Parameter> params) {
        if (params == null)
            throw new NullPointerException("QBlast parameters can not be null");
        this.params = params;
    }

    private List<Parameter> params = new ArrayList<Parameter>();

    public void addParameter(Params param, String value) {
        params.add(new Parameter(param.toString(), value));
    }

    public void removeParameter(Params param) {
        String pname = param.toString();
        List<Parameter> tmp = new ArrayList<Parameter>(params);
        for (Parameter p : tmp) {
            if (p.getName().equals(pname))
                params.remove(p);
        }
    }

    public void setParameter(Params param, String value) {
        removeParameter(param);
        addParameter(param, value);
    }

    /**
     * set parameter if value is not null
     * 
     * @param param
     * @param value
     */
    public void setParameterVNN(Params param, String value) {
        if (value == null)
            return;
        removeParameter(param);
        addParameter(param, value);
    }

    public List<Parameter> getParameters() {
        return new ArrayList<Parameter>(params);
    }

    public List<Parameter> getParameters(String name) {
        List<Parameter> result = new ArrayList<Parameter>();
        for (Parameter p : getParameters()) {
            if (p.getName().equals(name)) {
                result.add(p);
            }
        }
        return result;
    }

    public void setOutput(OutputType ot) {
        QBlastFormatObject formatObject;
        QBlastFormatType formatType;
        if (ot == OutputType.PSSM) {
            formatObject = QBlastFormatObject.PSSM;
            formatType = QBlastFormatType.TEXT;
        } else if (ot == OutputType.XML) {
            formatObject = QBlastFormatObject.ALIGNMENT;
            formatType = QBlastFormatType.XML;
        } else if (ot == OutputType.TEXT) {
            formatObject = QBlastFormatObject.ALIGNMENT;
            formatType = QBlastFormatType.TEXT;
        } else if (ot == OutputType.HTML) {
            formatObject = QBlastFormatObject.ALIGNMENT;
            formatType = QBlastFormatType.HTML;
        } else {
            throw new IllegalArgumentException();
        }
        setParameter(Params.FORMAT_TYPE, formatType.getQBlastName());
        setParameter(Params.FORMAT_OBJECT, formatObject.getQBlastName());
    }

    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append(getClass().getName());
        s.append(" ");
        s.append(getParameters(Params.CMD.toString()));
        List<Parameter> qs = getParameters(Params.CMD.toString());
        if (qs.size() > 0) {
            String q = qs.get(0).getValue();
            if (q.length() > 10) {
                q = q.substring(0, 10);
            }
            s.append(" ");
            s.append("QUERY=");
            s.append(q);
        }
        return s.toString();
    }

    public enum OutputType {
        PSSM, TEXT, XML, HTML
    }

}
