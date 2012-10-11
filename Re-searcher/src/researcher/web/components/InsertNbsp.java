package researcher.web.components;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.components.Insert;

public abstract class InsertNbsp extends Insert {

	@Override
	protected void renderComponent(IMarkupWriter writer, IRequestCycle arg1) {
		if (getValue() == null || getValue().toString().trim().length() == 0) {
			writer.printRaw("&nbsp;");
			return;
		}
		super.renderComponent(writer, arg1);
	}

}
