package org.cru.pshcm.addresscorrection;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.TransportTubeFactory;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe;

/**
 * @author Matt Drees
 */
public class DebuggingTransportTubeFactory extends TransportTubeFactory
{
    private static volatile boolean debug;

    @Override
    public Tube doCreate(
        @NotNull ClientTubeAssemblerContext context)
    {
        if (debug)
            return new DebuggingHttpTransportPipe(context.getCodec(), context.getBinding(), System.out);
        else
            return new HttpTransportPipe(context.getCodec(), context.getBinding());
    }

    public static void setDebug(boolean debug)
    {
        DebuggingTransportTubeFactory.debug = debug;
    }

}
