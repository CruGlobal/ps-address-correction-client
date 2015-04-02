package org.cru.pshcm.addresscorrection;

import org.ccci.postalsoft.PostalsoftService;

import javax.xml.ws.BindingProvider;
import java.io.PrintStream;

/**
 * @author Matt Drees
 */
public class ActualDebugPrinter implements DebugPrinter
{

    private final PrintStream out;

    public ActualDebugPrinter(PrintStream out)
    {
        this.out = out;
    }

    @Override
    public void printEndpointUrl(PostalsoftService service)
    {
        BindingProvider provider = (BindingProvider) service;
        String url = (String) provider
            .getRequestContext()
            .get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);

        out.println("address correction url: " + url);
    }

    @Override
    public void correcting(Address address)
    {
        out.println("correcting address:");
        print(address);
    }

    private void print(Address address)
    {
        out.println(" 1. " + emptyIfNull(address.getAddressLine1()));
        out.println(" 2. " + emptyIfNull(address.getAddressLine2()));
        out.println(" 3. " + emptyIfNull(address.getAddressLine3()));
        out.println(
            " 4. " +
            emptyIfNull(address.getCity()) + " " +
            emptyIfNull(address.getState()) + " " +
            emptyIfNull(address.getZip()));
    }

    private String emptyIfNull(String string)
    {
        if (string == null)
            return "";
        else
            return string;
    }

    @Override
    public void success(Address address)
    {
        out.println("address correction successful; new address:");
        print(address);
    }

    @Override
    public void failure()
    {
        out.println("address correction failed");
    }

    @Override
    public void exception(RuntimeException e)
    {
        out.println("exception calling address correction service:");
        e.printStackTrace(out);
    }
}
