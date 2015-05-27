package org.cru.pshcm.addresscorrection;

import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.ws.WebServiceException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * @author Matt Drees
 */
public class HttpsHandlerEnforcingHttpTransportPipe extends HttpTransportPipe
{
    private final DebugPrinter debugPrinter;

    private static volatile boolean replacedHandlerMessage;

    public HttpsHandlerEnforcingHttpTransportPipe(
        Codec codec,
        WSBinding binding,
        DebugPrinter debugPrinter)
    {
        super(codec, binding);
        this.debugPrinter = debugPrinter;
    }

    @Override
    public Packet process(Packet request)
    {
        debugPrinter.beginHttpTransportPipeProcess(request);
        try
        {
            EndpointAddress address = request.endpointAddress;
            URLConnection urlConnection = address.openConnection();

            debugPrinter.debugUrlConnection(urlConnection);

            String scheme = address.getURI().getScheme();
            if (scheme.equals("https") &&
                !(urlConnection instanceof HttpsURLConnection))
            {
                debugPrinter.debug("https url did not open an HttpsURLConnection!");
                debugPrinter.debug("the url's handler was: " + getHandlerAsString(address.getURL()));

                replaceURLStreamHandler(address, new sun.net.www.protocol.https.Handler());
            }
        }
        catch (IOException e)
        {
            throw new WebServiceException(e);
        }

        return super.process(request);
    }

    private void replaceURLStreamHandler(EndpointAddress address, URLStreamHandler newHandler)
        throws IOException
    {
        try
        {
            Field handlerField = getAccessibleHandlerField();
            handlerField.set(address.getURL(), newHandler);
            debugPrinter.debug("replaced handler with " + newHandler);

            warn("An https URL opened a non-HttpsURLConnection. " +
                 "The handler has been replaced with oracle's HttpsURLHandler. " +
                 "This message will not be printed again. ");

            checkURLConnectionType(address);
        }
        catch (NoSuchFieldException e)
        {
            debugPrinter.debug(
                "unable to replace URLStreamHandler " +
                "(no 'handler' field found in URL.class)");
        }
        catch (IllegalAccessException e)
        {
            throw new AssertionError("we called setAccessible(true)");
        }
    }

    private void warn(String s)
    {
        if (!replacedHandlerMessage)
        {
            System.err.println(s);
            replacedHandlerMessage = true;
        }
    }

    private void checkURLConnectionType(EndpointAddress address) throws IOException
    {
        URLConnection urlConnection = address.openConnection();
        if (urlConnection instanceof HttpsURLConnection)
        {
            debugPrinter.debug("the url now opens an HttpsURLConnection");
        }
        else
        {
            debugPrinter.debug("the url *still* opens a non-HttpsURLConnection!");
        }
    }

    private String getHandlerAsString(URL url)
    {
        try
        {
            Field handlerField = getAccessibleHandlerField();
            Object handler = handlerField.get(url);
            return String.valueOf(handler);
        }
        catch (NoSuchFieldException e)
        {
            return "not available (no 'handler' field found in URL.class)";
        }
        catch (IllegalAccessException e)
        {
            throw new AssertionError("we called setAccessible(true)");
        }
    }

    private Field getAccessibleHandlerField() throws NoSuchFieldException
    {
        Field handlerField = URL.class.getDeclaredField("handler");
        handlerField.setAccessible(true);
        return handlerField;
    }
}
