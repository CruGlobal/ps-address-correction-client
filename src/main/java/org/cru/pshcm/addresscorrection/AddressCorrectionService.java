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
    private final String serviceUsername;
    private final String servicePassword;
    private final String endpointAddressOverride;

    public AddressCorrectionService(
        String serviceUsername,
        String servicePassword,
        String endpointAddressOverride)
    {
        this.serviceUsername = serviceUsername;
        this.servicePassword = servicePassword;
        this.endpointAddressOverride = endpointAddressOverride;
    }

    public CorrectionResult correctAddress(Address address)
    {
        PostalsoftService service =
            new Util_002fPostalSoft().getUtil_002fPostalSoftHttpPort();
        overrideAddressIfNecessary(service);


        PostalAddress postalAddress = createPostalAddress(address);

        org.ccci.webservices.services.postalsoft.CorrectionResult postalsoftResult =
            service.correctAddress(serviceUsername, servicePassword, postalAddress);

        return createResult(postalsoftResult);
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
            result.setAddress(createAddress(postalsoftResult.getAddress().getValue()));
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
