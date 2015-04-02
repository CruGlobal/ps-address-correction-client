package org.cru.pshcm.addresscorrection;

/**
 * @author Matt Drees
 */
public class InterfaceTest
{
    public static void main(String[] args)
    {

        ServiceFactory factory = new ServiceFactory(
            args[0],
            args[1],
            null,
            true
        );
        AddressCorrectionService service = factory.buildService();

        Address address = new Address();

        address.setAddressLine1("5841 Neptune Dr");
        address.setCity("Ft. Collins");
        address.setState("Colorado");
        address.setZip("80525");

        CorrectionResult result = service.correctAddress(address);
    }
}
