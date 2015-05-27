package org.cru.pshcm.addresscorrection;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.TransportTubeFactory;
import com.sun.xml.internal.ws.api.pipe.Tube;

/**
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
