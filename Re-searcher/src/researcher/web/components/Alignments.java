package researcher.web.components;

import java.io.StringWriter;

import org.apache.tapestry.AbstractComponent;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Parameter;

import researcher.beans.Hit;
import researcher.utils.AlignmentsRenderer;


public abstract class Alignments extends AbstractComponent {

    @Parameter
    public abstract Hit getHit();

    @Override
    protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle) {
        writer.begin("div");
        renderInformalParameters(writer, cycle);
        StringWriter stringWriter = new StringWriter();
        AlignmentsRenderer renderer = new AlignmentsRenderer(stringWriter);
        renderer.renderHit(getHit());
        writer.printRaw(stringWriter.getBuffer().toString());
        writer.end();
    }

}