package org.cru.pshcm.addresscorrection;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.TransportTubeFactory;
import com.sun.xml.internal.ws.api.pipe.Tube;
import java.net.HttpURLConnection;
import javax.net.ssl.HttpsURLConnection;

/**
 * This works around a peoplesoft JRE bug with https handling.
 * The problem is with psft.pt8.pshttp.https.Handler#openConnection(URL, Proxy);
 * it returns a {@link HttpURLConnection}, instead of a {@link HttpsURLConnection},
 * when the url is an https url.
 *
 * This factory creates a HttpTransportPipe that uses reflection to replace
 * the URLConnection, if necessary, for each request's
 * {@link com.sun.xml.internal.ws.api.EndpointAddress}.
 *
 *
 * This assumes that the JRE is an oracle JRE,
 * that there is no security manager enforcement,
 * and that the jax-ws library is the oracle built-in one.
 *
 *
 * @author Matt Drees
 */
public class HttpsHandlerEnforcingTransportTubeFactory extends TransportTubeFactory
{
    private static volatile DebugPrinter debugPrinter;

    @Override
    public Tube doCreate(
        @NotNull ClientTubeAssemblerContext context)
    {
        return new HttpsHandlerEnforcingHttpTransportPipe(context.getCodec(), context.getBinding(), debugPrinter);
    }

    public static void setDebugPrinter(DebugPrinter debugPrinter)
    {
        HttpsHandlerEnforcingTransportTubeFactory.debugPrinter = debugPrinter;
    }

}
