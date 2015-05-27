package org.cru.pshcm.addresscorrection;

/**
 * @author Matt Drees
 */
public class ServiceFactory
{

    private static final String SYSTEM_ID_ENV = "ADDRESS_CORRECTION_SYSID";
    private static final String SYSTEM_KEY_ENV = "ADDRESS_CORRECTION_SYSKEY";
    private static final String URL_ENV = "ADDRESS_CORRECTION_URL";
    private static final String DEBUG_ENV = "ADDRESS_CORRECTION_DEBUG";


    private static ServiceFactory instance;

    private final String systemId;
    private final String systemKey;
    private final String url;
    private DebugPrinter debugPrinter;

    ServiceFactory(String systemId, String systemKey, String url, boolean debug)
    {
        this.systemId = systemId;
        this.systemKey = systemKey;
        this.url = url;

        if (debug) {

            // the jdk-provided jax-ws client uses this
            System.setProperty(
                "com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump",
                "true");

            // just in case the non-jdk jax-ws client is in use, we'll set this too
            System.setProperty(
                "com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump",
                "true");

        }

        debugPrinter = buildDebugPrinter(debug);
        HttpsHandlerEnforcingTransportTubeFactory.setDebugPrinter(debugPrinter);
        debugPrinter.printInitializationDebugInfo(url);
    }

    private DebugPrinter buildDebugPrinter(boolean debug)
    {
        System.out.println(
            "address correction client debug output " +
            (debug ? "enabled" : "disabled"));

        return debug ?
            new ActualDebugPrinter(System.out) :
            new NoOpDebugPrinter();
    }

    public static synchronized ServiceFactory getFactory() {
        if (instance == null)
            initializeFromEnvironment();
        return instance;
    }

    private static void initializeFromEnvironment()
    {
        instance = new ServiceFactory(
            getRequiredEnvVariable(SYSTEM_ID_ENV),
            getRequiredEnvVariable(SYSTEM_KEY_ENV),
            System.getenv(URL_ENV),
            Boolean.parseBoolean(System.getenv(DEBUG_ENV)));
    }

    private static String getRequiredEnvVariable(String name)
    {
        String value = System.getenv(name);
        if (value == null) {
            String message = "required environment variable " + name + " is not set";
            System.err.println(message);
            throw new IllegalStateException(message);
        }
        return value;
    }

    public AddressCorrectionService buildService() {
        return new AddressCorrectionService(systemId, systemKey, url, debugPrinter);
    }
}
