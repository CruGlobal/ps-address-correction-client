package org.cru.pshcm.addresscorrection;

import org.ccci.postalsoft.PostalsoftService;

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
}
