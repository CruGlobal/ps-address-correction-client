package org.cru.pshcm.addresscorrection;

/**
 * @author Matt Drees
 */
public class ServiceFactory
{

    private static final String USER_ENV = "ADDRESS_CORRECTION_USER";
    private static final String PASSWORD_ENV = "ADDRESS_CORRECTION_PASSWORD";
    private static final String URL_ENV = "ADDRESS_CORRECTION_URL";
    private static ServiceFactory instance;

    private final String user;
    private final String password;
    private final String url;

    private ServiceFactory(String user, String password, String url)
    {
        this.user = user;
        this.password = password;
        this.url = url;
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
            System.getenv(URL_ENV));
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

    public static synchronized ServiceFactory getFactory(String user, String password, String url) {
        if (instance == null)
            instance = new ServiceFactory(user, password, url);
        return instance;
    }

    public AddressCorrectionService buildService() {
        return new AddressCorrectionService(user, password, url);
    }
}
