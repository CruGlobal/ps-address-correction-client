package org.cru.pshcm.addresscorrection;

import sun.net.www.protocol.http.Handler;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * @author Matt Drees
 */
public class StupidURLStreamHandlerFactory implements URLStreamHandlerFactory
{
    @Override
    public URLStreamHandler createURLStreamHandler(String protocol)
    {
        if (protocol.equals("http") || protocol.equals("https"))
            return new Handler();
        else
            return null;
    }

}
