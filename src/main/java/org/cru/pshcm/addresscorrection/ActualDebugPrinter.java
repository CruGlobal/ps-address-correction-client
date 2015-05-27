package org.cru.pshcm.addresscorrection;

import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.message.Packet;
import org.ccci.postalsoft.PostalsoftService;
import org.ccci.postalsoft.Util_002fPostalSoft;

import javax.xml.ws.BindingProvider;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * @author Matt Drees
 */
public class ActualDebugPrinter implements DebugPrinter
{

    private final PrintStream out;

    public ActualDebugPrinter(PrintStream out)
    {
        this.out = out;
    }

    @Override
    public void printInitializationDebugInfo(String url)
    {
        debugCodeSource(Util_002fPostalSoft.class, "wsapi-postalsoft-client jar");
        debugCodeSource(ServiceFactory.class, "ps-address-correction-client jar");

        debugUrlHandlerSystemProperty();

        out.println("using address correction soap endpoint url: " + url);

        debugUrlHandlerFactory();
        debugUrlHandlers(url);
    }

    private void debugCodeSource(Class<?> classInJar, String jarDescription)
    {
        out.println("debug information for " + jarDescription + ":");
        URL classResource = classInJar.getResource(classInJar.getSimpleName() + ".class");
        if (classResource.getProtocol().equals("jar"))
        {
            debugJarInformation(classResource);
        }
        else
        {
            debugNonJarInformation(classInJar);
        }
        out.println();
    }

    private void debugJarInformation(URL classResource)
    {
        try
        {
            JarURLConnection connection = (JarURLConnection) classResource.openConnection();

            URL jarFileURL = connection.getJarFileURL();
            out.println("location: " + jarFileURL);

            Manifest manifest = connection.getManifest();
            out.println("manifest:");
            Attributes mainAttributes = manifest.getMainAttributes();
            for (Map.Entry<Object, Object> entry : mainAttributes.entrySet())
            {
                out.println("  " + entry.getKey() + ": " + entry.getValue());
            }
        }
        catch (IOException e)
        {
            out.println("unable to read jar " + classResource);
            e.printStackTrace(out);
        }
    }

    private void debugNonJarInformation(Class<?> classInJar)
    {
        out.println("classes are actually not in a jar.");
        URL location =
            classInJar.getProtectionDomain().getCodeSource().getLocation();
        out.println("location: " + location);
    }

    private void debugUrlHandlerSystemProperty()
    {
        String key = "java.protocol.handler.pkgs";
        String value = System.getProperty(key);
        out.println("system property " + key + ": " + value);
    }

    private void debugUrlHandlerFactory()
    {
        try
        {
            Field factoryField = URL.class.getDeclaredField("factory");
            factoryField .setAccessible(true);
            Object factory = factoryField .get(null);
            out.println("configured url handler factory: " + factory);
        }
        catch (Exception e)
        {
            e.printStackTrace(out);
        }
    }

    private void debugUrlHandlers(String url)
    {
        try
        {
            //make sure the handler gets initialized
            new URL(url);

            Field handlersField = URL.class.getDeclaredField("handlers");
            handlersField.setAccessible(true);
            Object handlersTable = handlersField.get(null);
            out.println("configured url handlers:");
            out.println(handlersTable);
        }
        catch (Exception e)
        {
            e.printStackTrace(out);
        }
    }

    @Override
    public void printEndpointUrl(PostalsoftService service)
    {
        BindingProvider provider = (BindingProvider) service;
        String url = (String) provider
            .getRequestContext()
            .get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);

        out.println("address correction url: " + url);

        debugDnsLookup(url);

        URL location;
        try
        {
            location = new URL(url);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace(out);
            return;
        }

        try
        {
            URLConnection urlConnection = location.openConnection();
            out.println("URLConnection for this url: " + urlConnection);

            urlConnection = location.openConnection(Proxy.NO_PROXY);
            out.println("URLConnection for this url (when opened with Proxy.NO_PROXY): " + urlConnection);
        }
        catch (IOException e)
        {
            e.printStackTrace(out);
        }
    }

    private void debugDnsLookup(String url)
    {
        try
        {
            String host = new URI(url).getHost();
            out.println("which DNS resolves to: " + InetAddress.getByName(host).getHostAddress() );
        }
        catch (URISyntaxException e)
        {
            out.println("cannot parse url:");
            e.printStackTrace(out);
        }
        catch (UnknownHostException e)
        {
            out.println("unable to resolve hostname:");
            e.printStackTrace(out);
        }
    }

    @Override
    public void correcting(Address address)
    {
        out.println("correcting address:");
        print(address);
    }

    private void print(Address address)
    {
        out.println(" 1. " + emptyIfNull(address.getAddressLine1()));
        out.println(" 2. " + emptyIfNull(address.getAddressLine2()));
        out.println(" 3. " + emptyIfNull(address.getAddressLine3()));
        out.println(
            " 4. " +
            emptyIfNull(address.getCity()) + " " +
            emptyIfNull(address.getState()) + " " +
            emptyIfNull(address.getZip()));
    }

    private String emptyIfNull(String string)
    {
        if (string == null)
            return "";
        else
            return string;
    }

    @Override
    public void success(Address address)
    {
        out.println("address correction successful; new address:");
        print(address);
    }

    @Override
    public void failure()
    {
        out.println("address correction failed");
    }

    @Override
    public void exception(RuntimeException e)
    {
        out.println("exception correcting address:");
        e.printStackTrace(out);
        out.flush();
    }

    @Override
    public void beginHttpTransportPipeProcess(Packet request)
    {
        out.println("http transport pipe information:");
        EndpointAddress address = request.endpointAddress;
        URL url = address.getURL();
        out.println("endpoint address as URL: " + url);
        out.println("endpoint address as URI: " + address.getURI());

    }

    @Override
    public void debugUrlConnection(URLConnection urlConnection)
    {
        out.println("url connection: " + urlConnection);
        if (urlConnection instanceof HttpURLConnection)
        {
            HttpURLConnection httpUrlConnection1 = (HttpURLConnection) urlConnection;
            out.println("using proxy: " + httpUrlConnection1.usingProxy());
        }
    }

    @Override
    public void debug(String s)
    {
        out.println(s);
    }


}
