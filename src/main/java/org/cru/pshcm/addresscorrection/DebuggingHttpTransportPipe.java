package org.cru.pshcm.addresscorrection;

import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.ws.WebServiceException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;

/**
 * @author Matt Drees
 */
public class DebuggingHttpTransportPipe extends HttpTransportPipe
{
    private final PrintStream out;

    public DebuggingHttpTransportPipe(
        Codec codec,
        WSBinding binding,
        PrintStream out)
    {
        super(codec, binding);
        this.out = out;
    }

    @Override
    public Packet process(Packet request)
    {
        out.println("http transport pipe information:");
        EndpointAddress address = request.endpointAddress;
        out.println("endpoint address as URL: " + address.getURL());
        out.println("endpoint address as URI: " + address.getURI());
        try
        {
            URLConnection urlConnection = address.openConnection();

            out.println("url connection: " + urlConnection);

            String scheme = address.getURI().getScheme();
            if (scheme.equals("https") &&
                !(urlConnection instanceof HttpsURLConnection))
            {
                out.println("https url did not open a HttpsURLConnection!");
            }

            HttpURLConnection httpUrlConnection = (HttpURLConnection) urlConnection;
            out.println("using proxy: " + httpUrlConnection.usingProxy());

        }
        catch (IOException e)
        {
            throw new WebServiceException(e);
        }

        return super.process(request);
    }
}
