package researcher.web.components;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.Parameter;

import researcher.utils.Utils;

public abstract class HelpBox extends BaseComponent {

	@Asset("images/question.png")
	public abstract IAsset getQuestionImage();

	@Parameter
	public abstract String getLink();

	@Parameter
	public abstract String getText();
	
	@Parameter
	public abstract String getStyle();
	
	@Parameter(name="left", defaultValue="0")
	public abstract String getLeft();
	
	@Parameter(name="top", defaultValue="0")
	public abstract String getTop();
	
	@Parameter
	public abstract String getWidth();	
	
	private static String border = "border: 0; ";
	
	public String getInlineStyle(){		
		if (getStyle() != null && !getStyle().equals("")){
			return border + getStyle();
		}
		return border;
	}

	public String getMouseOverScript() {
		String text = Utils.escapeQuotes(getText());
		String w = getWidth();
		if (w == null || w.equals("")) w = "20em";
		return "helpOn(this, '"+w+"', 'auto', "+getLeft()+", "+ getTop()+", '" + text + "', event)";
	}

	public String getMouseOutScript() {
		return "helpOff()";
	}

}