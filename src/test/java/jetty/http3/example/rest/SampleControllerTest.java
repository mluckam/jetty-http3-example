package jetty.http3.example.rest;

import java.nio.file.Paths;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.dynamic.HttpClientTransportDynamic;
import org.eclipse.jetty.http3.client.HTTP3Client;
import org.eclipse.jetty.http3.client.http.ClientConnectionFactoryOverHTTP3;
import org.eclipse.jetty.io.ClientConnector;
import org.eclipse.jetty.quic.common.QuicConfiguration;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
class SampleControllerTest {

    /**
     * This test passes only when {@link HTTP3Client#getQuicConfiguration()} ->
     * {@link QuicConfiguration#setVerifyPeerCertificates(boolean)} is false.  The default is
     * true for clients, see documentation https://docs.rs/quiche/latest/quiche/struct.Config.html#method.verify_peer
     */
    @Test
    void testClientHttp3_noPeerVerification() {
        SslContextFactory.Client sslContextFactory = new SslContextFactory.Client();
        String keySorePath = Paths.get("src", "main", "resources", "certs", "client",
                "client.p12").toAbsolutePath().normalize().toString();
        sslContextFactory.setKeyStorePath(keySorePath);
        sslContextFactory.setKeyStorePassword("password");
        String trustStorePath = Paths.get("src", "main", "resources", "certs",
                "trustStore.jks").toAbsolutePath().normalize().toString();
        sslContextFactory.setTrustStorePath(trustStorePath);
        sslContextFactory.setTrustStorePassword("password");
        /**
         * Configuration obtained from default {@link ClientConnector#newSslContextFactory} used
         * in {@link ClientConnector#doStart} when sslContextFactory is null.
         */
        sslContextFactory.setTrustAll(false);
        sslContextFactory.setEndpointIdentificationAlgorithm("HTTPS");

        HTTP3Client http3Client = new HTTP3Client();
        http3Client.getClientConnector().setSslContextFactory(sslContextFactory);
        /**
         * Connection fails without this set to false.  Configuration obtained from jetty test
         * https://github.com/eclipse/jetty.project/blob/jetty-11.0.x/jetty-http3/http3-tests/src/test/java/org/eclipse/jetty/http3/tests/AbstractClientServerTest.java#L92
         */
        http3Client.getQuicConfiguration().setVerifyPeerCertificates(false);

        ClientConnectionFactoryOverHTTP3.HTTP3 http3 =
                new ClientConnectionFactoryOverHTTP3.HTTP3(http3Client);
        HttpClientTransportDynamic httpClientTransportDynamic =
                new HttpClientTransportDynamic(http3);
        HttpClient httpClient = new HttpClient(httpClientTransportDynamic);
        try {
            httpClient.start();
            ContentResponse response =
                    httpClient.GET("https://localhost:8443/sample/hello-world");
            String expected = "HELLO WORLD!";
            String actual = response.getContentAsString();

            Assertions.assertEquals(expected, actual);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testClientHttp3_peerVerificationEnabled() {
        SslContextFactory.Client sslContextFactory = new SslContextFactory.Client();
        String keySorePath = Paths.get("src", "main", "resources", "certs", "client",
                "client.p12").toAbsolutePath().normalize().toString();
        sslContextFactory.setKeyStorePath(keySorePath);
        sslContextFactory.setKeyStorePassword("password");
        String trustStorePath = Paths.get("src", "main", "resources", "certs",
                "trustStore.jks").toAbsolutePath().normalize().toString();
        sslContextFactory.setTrustStorePath(trustStorePath);
        sslContextFactory.setTrustStorePassword("password");
        /**
         * Configuration obtained from default {@link ClientConnector#newSslContextFactory} used
         * in {@link ClientConnector#doStart} when sslContextFactory is null.
         */
        sslContextFactory.setTrustAll(false);
        sslContextFactory.setEndpointIdentificationAlgorithm("HTTPS");

        HTTP3Client http3Client = new HTTP3Client();
        http3Client.getClientConnector().setSslContextFactory(sslContextFactory);
        /**
         * Connection fails without this set to false.  Configuration obtained from jetty test
         * https://github.com/eclipse/jetty.project/blob/jetty-11.0.x/jetty-http3/http3-tests/src/test/java/org/eclipse/jetty/http3/tests/AbstractClientServerTest.java#L92
         */
        http3Client.getQuicConfiguration().setVerifyPeerCertificates(true);

        ClientConnectionFactoryOverHTTP3.HTTP3 http3 =
                new ClientConnectionFactoryOverHTTP3.HTTP3(http3Client);
        HttpClientTransportDynamic httpClientTransportDynamic =
                new HttpClientTransportDynamic(http3);
        HttpClient httpClient = new HttpClient(httpClientTransportDynamic);
        try {
            httpClient.start();
            ContentResponse response =
                    httpClient.GET("https://localhost:8443/sample/hello-world");
            String expected = "HELLO WORLD!";
            String actual = response.getContentAsString();

            Assertions.assertEquals(expected, actual);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This test should fail since the certificate is not trusted.
     */
    @Test
    void testHttp3Client_unauthorizedKeyStore() {
        SslContextFactory.Client sslContextFactory = new SslContextFactory.Client();
        String keySorePath = Paths.get("src", "main", "resources", "certs", "not_trusted",
                "not_trusted.p12").toAbsolutePath().normalize().toString();
        sslContextFactory.setKeyStorePath(keySorePath);
        sslContextFactory.setKeyStorePassword("password");
        //keystore is self-signed certificate so use the same for truststore
        sslContextFactory.setTrustStorePath(keySorePath);
        sslContextFactory.setTrustStorePassword("password");
        /**
         * Configuration obtained from default {@link ClientConnector#newSslContextFactory} used
         * in {@link ClientConnector#doStart} when sslContextFactory is null.
         */
        sslContextFactory.setTrustAll(false);
        sslContextFactory.setEndpointIdentificationAlgorithm("HTTPS");

        HTTP3Client http3Client = new HTTP3Client();
        http3Client.getClientConnector().setSslContextFactory(sslContextFactory);
        /**
         * Connection fails without this set to false.  Configuration obtained from jetty test
         * https://github.com/eclipse/jetty.project/blob/jetty-11.0.x/jetty-http3/http3-tests/src/test/java/org/eclipse/jetty/http3/tests/AbstractClientServerTest.java#L92
         */
        http3Client.getQuicConfiguration().setVerifyPeerCertificates(false);

        ClientConnectionFactoryOverHTTP3.HTTP3 http3 =
                new ClientConnectionFactoryOverHTTP3.HTTP3(http3Client);
        HttpClientTransportDynamic httpClientTransportDynamic =
                new HttpClientTransportDynamic(http3);
        HttpClient httpClient = new HttpClient(httpClientTransportDynamic);

        try {
            httpClient.start();
            ContentResponse response =
                    httpClient.GET("https://localhost:8443/sample/hello-world");
            Assertions.fail("Request completed succesfully. Certificate should not be trusted.");
        } catch (Exception e) {
            Assertions.assertNotNull(e);
        }
    }
}
