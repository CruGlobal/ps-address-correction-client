package org.cru.pshcm.addresscorrection;

import org.ccci.postalsoft.PostalsoftService;

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
}
