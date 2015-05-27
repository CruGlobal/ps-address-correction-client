package org.cru.pshcm.addresscorrection;

import com.sun.xml.internal.ws.api.message.Packet;
import org.ccci.postalsoft.PostalsoftService;

import java.net.URLConnection;

/**
 * @author Matt Drees
 */
public class NoOpDebugPrinter implements DebugPrinter
{
    @Override
    public void printEndpointUrl(PostalsoftService service)
    {
    }

    @Override
    public void correcting(Address address)
    {
    }

    @Override
    public void success(Address address)
    {
    }

    @Override
    public void failure()
    {
    }

    @Override
    public void exception(RuntimeException e)
    {
    }

    @Override
    public void printInitializationDebugInfo(String url)
    {
    }

    @Override
    public void beginHttpTransportPipeProcess(Packet request)
    {
    }

    @Override
    public void debugUrlConnection(URLConnection urlConnection)
    {
    }

    @Override
    public void debug(String s)
    {
    }
}
