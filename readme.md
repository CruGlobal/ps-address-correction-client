PeopleSoft Address Correction Client
====================================

This project is a small java wrapper around the jax-ws generated soap client
for WSAPI's address correction service.
The wrapper makes it convenient to use the client from Peoplecode.


It also installs a workaround that fixes an apparent peoplesoft URL-handling bug,
that sometimes causes jax-ws to use an `HttpURLConnection` on `https` URLs,
which can wreak havok of various sorts.

Usage
-----

 1. Put ps-address-correction-client.jar and wsapi-postalsoft-client.jar on peoplesoft's classpath.
 2. Set up environment variables:
     *  `ADDRESS_CORRECTION_SYSID` - the system id to authenticate with
     *  `ADDRESS_CORRECTION_SYSKEY` - the system key to authenticate with
     *  `ADDRESS_CORRECTION_URL` - (optional) the wsapi url to use.
        If this is not present, the wsapi url will be determined by the embedded wsdl in
        wsapi-postalsoft-client.jar.
        Examples:
         *  `https://wsapi.cru.org/wsapi/services/util/PostalSoft` (production)
         *  `https://wsapi-stage.cru.org/wsapi/services/util/PostalSoft` (staging)
         *  `http://hart-a331.net.ccci.org:8680/wsapi/services/util/PostalSoft`
            (staging, bypass loadbalancer for debugging purposes)

     *  `ADDRESS_CORRECTION_DEBUG` - (optional) set to `true` to log debugging information to STDOUT.
        (Note: this also attempts to activate the oracle-provided jax-ws request dumping
        by programmatically setting the
        `com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump` and
        `com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump`
        system properties to true.
        If there are no other jax-ws clients in the jvm, this should succeed.
        If there are, the jvm might have cached the 'false' value for these properties,
        and the requests will not be dumped.
        In this case, you will have to add the system properties to the "JavaVM Options"
        peoplesoft configuration parameter.

 3. Call the client from peoplecode.
    See CCCVerifyAddress.pcode for an example.


Fine Print
----------

This library uses internal implementation classes in Oracle's JRE.
(This is acceptable for us because the peoplesoft jvm is an Oracle jvm.)
Therefore, this project will only compile against an oracle JDK.


Specifically, we are using the `com.sun.xml.internal.ws.*` classes
that make up the jax-ws implementation.
We are also using reflection to access private fields in `java.net.URL`,
which may not be present in other JREs.
