package org.cru.pshcm.addresscorrection;

/**
 * @author Matt Drees
 */
public class InterfaceTest
{
    public static void main(String[] args)
    {
        System.setProperty(
            "com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump",
            "true");


        ServiceFactory factory = ServiceFactory.getFactory(
            args[0],
            args[1],
            null
        );
        AddressCorrectionService service = factory.buildService();

        Address address = new Address();

        address.setAddressLine1("5841 Neptune Dr");
        address.setCity("Ft. Collins");
        address.setState("Colorado");
        address.setZip("80525");

        CorrectionResult result = service.correctAddress(address);

        System.out.println("successful: " + result.isSuccessful());
    }
}
