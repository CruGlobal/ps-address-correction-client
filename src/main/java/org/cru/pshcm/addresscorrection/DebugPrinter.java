package org.cru.pshcm.addresscorrection;

import com.sun.xml.internal.ws.api.message.Packet;
import org.ccci.postalsoft.PostalsoftService;

import java.net.URLConnection;

/**
 * @author Matt Drees
 */
public interface DebugPrinter
{
    void printEndpointUrl(PostalsoftService service);

    void correcting(Address address);

    void success(Address address);

    void failure();

    void exception(RuntimeException e);

    void printInitializationDebugInfo(String url);

    void beginHttpTransportPipeProcess(Packet request);

    void debugUrlConnection(URLConnection urlConnection);

    void debug(String s);
}
