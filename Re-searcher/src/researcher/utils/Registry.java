package researcher.utils;

import org.apache.hivemind.impl.RegistryBuilder;

import researcher.web.ResearcherServlet;


public abstract class Registry {

    private static org.apache.hivemind.Registry hivemindRegistry = null;

    /**
     * Returns the web application registry if available, if not constructs a
     * new registry. It can happen that web and blastjob is running on the same
     * virtual machine, but tapestrys registry on servlet is not yet
     * initialized, so we have two registrys then, not very optimal.
     * 
     * @return
     */
    public static org.apache.hivemind.Registry getHivemindRegistry() {
        if (hivemindRegistry == null) {
            hivemindRegistry = ResearcherServlet.getHivemindRegistry();
            if (hivemindRegistry == null) {
                System.out.println("CONSTRUCTING A NEW HIVEMIND REGISTRY");
                hivemindRegistry = RegistryBuilder.constructDefaultRegistry();
                System.out.println("DONE..");
            }
        }
        return hivemindRegistry;
    }

}
