package org.cru.pshcm.addresscorrection;

/**
 * @author Matt Drees
 */
public class CorrectionResult
{
    private boolean successful;
    private Address address;

    public boolean isSuccessful()
    {
        return successful;
    }

    public void setSuccessful(boolean successful)
    {
        this.successful = successful;
    }

    public Address getAddress()
    {
        return address;
    }

    public void setAddress(Address address)
    {
        this.address = address;
    }
}
