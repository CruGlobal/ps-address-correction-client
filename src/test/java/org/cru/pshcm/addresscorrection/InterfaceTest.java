package org.cru.pshcm.addresscorrection;

import java.net.URL;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

/**
 * @author Matt Drees
 */
public class InterfaceTest
{
    public static void main(String[] args)
    {
//        System.getProperties().put("proxySet", "true");
//        System.getProperties().put("https.proxyHost", "staffweb.cru.org");
//        System.getProperties().put("https.proxyPort", "444");

        setUpLogging();

//        URL.setURLStreamHandlerFactory(new StupidURLStreamHandlerFactory());

        ServiceFactory factory = new ServiceFactory(
            args[0],
            args[1],
            "https://wsapi.cru.org/wsapi/services/util/PostalSoft",
            true
        );
        AddressCorrectionService service = factory.buildService();

        Address address = new Address();

        address.setAddressLine1("5841 Neptune Dr");
        address.setCity("Fort Collins");
        address.setState("Colorado");

        CorrectionResult result = service.correctAddress(address);

        if (!result.isSuccessful())
        {
            throw new AssertionError("address correction wasn't successful");
        }
        String zip = result.getCorrectedAddress().getZip();
        if (!zip.equals("80525-3905"))
        {
            throw new AssertionError("address wasn't corrected; returned zip: " + zip);
        }
    }

    private static void setUpLogging()
    {
//        Logger.getLogger("sun.net").setLevel(Level.ALL);
//        Logger.getLogger("com.sun.xml").setLevel(Level.ALL);

        Logger root = Logger.getLogger("");
        Handler[] handlers = root.getHandlers();

        for (Handler handler : handlers)
        {
            root.removeHandler(handler);
        }
        StreamHandler handler = new StreamHandler(System.out, new SimpleFormatter());
        handler.setLevel(Level.ALL);
        root.addHandler(handler);
    }
}
