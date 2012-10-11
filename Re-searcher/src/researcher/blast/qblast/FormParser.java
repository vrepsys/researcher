package researcher.blast.qblast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import researcher.qblast.parameters.Params;
import researcher.utils.Utils;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.FormControl;
import au.id.jericho.lib.html.Source;
import au.id.jericho.lib.html.Tag;

public class FormParser {

    public static List<Parameter> getNextIterFormElements(String html) {
		Source source = new Source(html);
		List<Element> forms = source.findAllElements(Tag.FORM);
    
//    System.out.println("forms.size="+forms.size());
    
		
		Element theFormWeNeed = null;
		for (Element el : forms) {
//       System.out.println("----------------------------FORM---------------------------");
			List<FormControl> formControls = el.findFormControls();
			for (FormControl fc : formControls) {
        
				Collection values = fc.getValues();
//        System.out.println(fc.getName()+"="+fc.getValues());
				if (fc.getName() != null && fc.getName().equals("NEXT_I")) {
					theFormWeNeed = el;
          break;
				}
			}
		}
    
		if (theFormWeNeed == null) {
			throw new RuntimeException("Could not find the iteration form needed");
		}
    
    
    List<Parameter> params = new ArrayList<Parameter>();
		List<FormControl> formControls = theFormWeNeed.findFormControls();
    
		for (FormControl fc : formControls) {
//      System.out.println("name="+fc.getName() + " value="+fc.getValues());
			Collection values = fc.getValues();
			if (values.size() > 0 && fc.getName() != null) {
				Parameter p = new Parameter(fc.getName(), (String) values.iterator().next());
				params.add(p);
			}

		}
    System.out.println("params.size="+params.size());
		return params;
	}

    public static List<Parameter> getFormatFormElements(String html) {
        Source source = new Source(html);
        List<Element> elements = source.findAllElements(Tag.FORM);
        List<Parameter> params = new ArrayList<Parameter>();
        for (Element el : elements) {
            String fname = el.getAttributeValue("name");
            if (fname != null && fname.equals("RequestFormat")) {
                List<FormControl> formControls = el.findFormControls();
                for (FormControl fc : formControls) {
                    Collection values = fc.getValues();
                    if (values.size() > 0 && fc.getName() != null) {
                        Parameter p = new Parameter(fc.getName(), (String) values.iterator().next());
                        params.add(p);
                    }
                }
                return params;
            }
        }
        return null;
    }

    public static List<Parameter> getFirstFormElements(String html) {
        Source source = new Source(html);
        List<Element> elements = source.findAllElements(Tag.FORM);
        if (elements == null || elements.size() == 0)
            return null;
        List<Parameter> params = new ArrayList<Parameter>();
        Element el = elements.get(0);
        List<FormControl> formControls = el.findFormControls();
        for (FormControl fc : formControls) {
           if (fc.getName()!=null && fc.getName().equals("GAPCOSTS")) System.out.println(fc.getName() + " values size="+fc.getValues().size());
            Collection values = fc.getValues();
            if (values.size() > 0 && fc.getName() != null) {            	
                Parameter p = new Parameter(fc.getName(), (String) values.iterator().next());
                params.add(p);
            }
        }
        return params;
    }

    public static void main(String[] args) throws MalformedURLException, IOException {
        String fp = "/home/posu/Desktop/invokeNewest.html";
        String html = Utils.readTextFile(fp);
        List<Parameter> params = getNextIterFormElements(html);
        for (Parameter param : params) {
            if (param.getName().equals(Params.EXPECT.toString())) {
                System.out.println(param);
            }
        }
    }

}
