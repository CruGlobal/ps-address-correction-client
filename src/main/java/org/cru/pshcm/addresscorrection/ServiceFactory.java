package org.cru.pshcm.addresscorrection;

/**
 * @author Matt Drees
 */
public class ServiceFactory
{

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
            instance = new ServiceFactory(
                System.getenv("ADDRESS_CORRECTION_USER"),
                System.getenv("ADDRESS_CORRECTION_PASSWORD"),
                System.getenv("ADDRESS_CORRECTION_URL"));
        return instance;
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
