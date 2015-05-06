package org.cru.pshcm.addresscorrection;

/**
 * @author Matt Drees
 */
public class ServiceFactory
{

    private static final String USER_ENV = "ADDRESS_CORRECTION_USER";
    private static final String PASSWORD_ENV = "ADDRESS_CORRECTION_PASSWORD";
    private static final String URL_ENV = "ADDRESS_CORRECTION_URL";
    private static final String DEBUG_ENV = "ADDRESS_CORRECTION_DEBUG";


    private static ServiceFactory instance;

    private final String user;
    private final String password;
    private final String url;
    private final boolean debug;

    ServiceFactory(String user, String password, String url, boolean debug)
    {
        this.user = user;
        this.password = password;
        this.url = url;
        this.debug = debug;

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
    }

    public static synchronized ServiceFactory getFactory() {
        if (instance == null)
            initializeFromEnvironment();
        return instance;
    }

    private static void initializeFromEnvironment()
    {
        instance = new ServiceFactory(
            getRequiredEnvVariable(USER_ENV),
            getRequiredEnvVariable(PASSWORD_ENV),
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
        DebugPrinter debugPrinter = debug ?
            new ActualDebugPrinter(System.out) :
            new NoOpDebugPrinter();
        return new AddressCorrectionService(user, password, url, debugPrinter);
    }
}
