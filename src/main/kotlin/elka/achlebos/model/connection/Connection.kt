package elka.achlebos.model.connection

import elka.achlebos.model.certificate.X509CertificateManager
import org.eclipse.milo.opcua.sdk.client.OpcUaClient
import org.eclipse.milo.opcua.sdk.client.api.UaClient
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider
import org.eclipse.milo.opcua.stack.client.DiscoveryClient
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription
import java.nio.file.Path
import java.security.cert.X509Certificate
import java.util.concurrent.CompletableFuture

class Connection(private val serverUri: String) {
    fun discoverEndpoints(): CompletableFuture<List<EndpointDescription>> {
        return DiscoveryClient.getEndpoints(serverUri)
    }

    fun connectUsingX509Cert(endpoint: EndpointDescription,
                             certificatePath: Path,
                             certPassword: String): CompletableFuture<UaClient>  {

        val certificateManager = X509CertificateManager(certPassword)
        val (cert, keyPair) = certificateManager.load(certificatePath)

        val config = OpcUaClientConfig.builder()
                .setEndpoint(endpoint)
                .setApplicationUri(certificatePath.fileName.toString())
                .setCertificate(cert as X509Certificate)
                .setKeyPair(keyPair)
                .setIdentityProvider(AnonymousProvider())
                .setRequestTimeout(uint(5000))
                .build()
        return OpcUaClient.create(config).connect()
    }
}