package org.cru.pshcm.addresscorrection;

/**
 * @author Matt Drees
 */
public class CorrectionResult
{
    private boolean successful;
    private Address correctedAddress;

    public boolean isSuccessful()
    {
        return successful;
    }

    public void setSuccessful(boolean successful)
    {
        this.successful = successful;
    }

    public Address getCorrectedAddress()
    {
        return correctedAddress;
    }

    public void setCorrectedAddress(Address address)
    {
        this.correctedAddress = address;
    }
}
