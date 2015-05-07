package org.cru.pshcm.addresscorrection;

import org.ccci.postalsoft.ObjectFactory;
import org.ccci.postalsoft.PostalAddress;
import org.ccci.postalsoft.PostalsoftService;
import org.ccci.postalsoft.Util_002fPostalSoft;

import javax.xml.ws.BindingProvider;

/**
 * @author Matt Drees
 */
public class AddressCorrectionService
{
    private final String systemId;
    private final String systemKey;
    private final String endpointAddressOverride;
    private final DebugPrinter debugPrinter;

    public AddressCorrectionService(
        String systemId,
        String systemKey,
        String endpointAddressOverride,
        DebugPrinter debugPrinter)
    {
        this.systemId = systemId;
        this.systemKey = systemKey;
        this.endpointAddressOverride = endpointAddressOverride;
        this.debugPrinter = debugPrinter;
    }

    public CorrectionResult correctAddress(Address address)
    {
        PostalsoftService service =
            new Util_002fPostalSoft().getUtil_002fPostalSoftHttpPort();
        overrideAddressIfNecessary(service);
        debugPrinter.printEndpointUrl(service);

        debugPrinter.correcting(address);
        PostalAddress postalAddress = createPostalAddress(address);

        org.ccci.webservices.services.postalsoft.CorrectionResult postalsoftResult =
            correctAddress(service, postalAddress);

        return createResult(postalsoftResult);
    }

    private org.ccci.webservices.services.postalsoft.CorrectionResult correctAddress(PostalsoftService service,
        PostalAddress postalAddress)
    {
        try
        {
            return service.correctAddress(systemId, systemKey, postalAddress);
        }
        catch (RuntimeException e)
        {
            debugPrinter.exception(e);
            throw e;
        }
    }

    private void overrideAddressIfNecessary(PostalsoftService service)
    {
        if (endpointAddressOverride != null)
        {
            BindingProvider provider = (BindingProvider) service;
            provider
                .getRequestContext()
                .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointAddressOverride);
        }
    }

    private PostalAddress createPostalAddress(Address address)
    {
        ObjectFactory factory = new ObjectFactory();
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setAddressLine1(
            factory.createPostalAddressAddressLine1(address.getAddressLine1()));
        postalAddress.setAddressLine2(
            factory.createPostalAddressAddressLine2(address.getAddressLine2()));
        postalAddress.setAddressLine3(
            factory.createPostalAddressAddressLine3(address.getAddressLine3()));
        postalAddress.setCity(factory.createPostalAddressCity(address.getCity()));
        postalAddress.setState(factory.createPostalAddressCity(address.getCity()));
        postalAddress.setZip(factory.createPostalAddressZip(address.getZip()));
        return postalAddress;
    }

    private CorrectionResult createResult(
        org.ccci.webservices.services.postalsoft.CorrectionResult postalsoftResult)

    {
        CorrectionResult result = new CorrectionResult();
        boolean success = postalsoftResult.getResultStatus().getValue().equals("SUCCESS");
        result.setSuccessful(success);
        if (success)
        {
            Address address = createAddress(postalsoftResult.getAddress().getValue());
            debugPrinter.success(address);
            result.setCorrectedAddress(address);
        }
        else
        {
            debugPrinter.failure();
        }
        return result;
    }

    private Address createAddress(PostalAddress postalsoftAddress)
    {
        Address address = new Address();
        address.setAddressLine1(postalsoftAddress.getAddressLine1().getValue());
        address.setAddressLine2(postalsoftAddress.getAddressLine2().getValue());
        address.setAddressLine3(postalsoftAddress.getAddressLine3().getValue());
        address.setCity(postalsoftAddress.getCity().getValue());
        address.setState(postalsoftAddress.getState().getValue());
        address.setZip(postalsoftAddress.getZip().getValue());
        return address;
    }
}
