package elka.achlebos.model.connection

import elka.achlebos.model.CertificateLoadingException
import elka.achlebos.model.certificate.X509CertificateManager
import org.eclipse.milo.opcua.sdk.client.OpcUaClient
import org.eclipse.milo.opcua.sdk.client.api.UaClient
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider
import org.eclipse.milo.opcua.stack.client.DiscoveryClient
import org.eclipse.milo.opcua.stack.core.UaException
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription
import java.nio.file.Path
import java.security.cert.X509Certificate
import java.util.concurrent.CompletableFuture
import java.util.prefs.Preferences

class Connection(private val serverUri: String) {

    fun discoverEndpoints(): CompletableFuture<List<EndpointDescription>> {
        return DiscoveryClient.getEndpoints(serverUri)
    }

    @Throws(CertificateLoadingException::class, UaException::class)
    fun connectUsingX509Cert(endpoint: EndpointDescription,
                             keyStorePath: Path,
                             certPassword: String): CompletableFuture<UaClient>  {

        val userRootPreferences = Preferences.userRoot().node("/elka/achlebos")
        val certName = userRootPreferences.get("certificateName", "")
        val appUri = userRootPreferences.get("applicationUri", "")

        val certificateManager = X509CertificateManager()
        val (cert, keyPair) = certificateManager.load(certPassword, keyStorePath, certName)

        val x509cert = cert as X509Certificate

        val config = OpcUaClientConfig.builder()
                .setEndpoint(endpoint)
                .setApplicationUri(appUri)
                .setCertificate(x509cert)
                .setKeyPair(keyPair)
                .setIdentityProvider(AnonymousProvider())
                .setRequestTimeout(uint(5000))
                .build()
        return OpcUaClient.create(config).connect()
    }
}