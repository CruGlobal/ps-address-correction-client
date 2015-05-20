package org.cru.pshcm.addresscorrection;

import org.ccci.postalsoft.PostalsoftService;
import org.ccci.postalsoft.Util_002fPostalSoft;

import javax.xml.ws.BindingProvider;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
    public void printInitializationDebugInfo()
    {
        debugCodeSource(Util_002fPostalSoft.class, "wsapi-postalsoft-client jar");
        debugCodeSource(ServiceFactory.class, "ps-address-correction-client jar");
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

    @Override
    public void printEndpointUrl(PostalsoftService service)
    {
        BindingProvider provider = (BindingProvider) service;
        String url = (String) provider
            .getRequestContext()
            .get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);

        out.println("address correction url: " + url);

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
}
