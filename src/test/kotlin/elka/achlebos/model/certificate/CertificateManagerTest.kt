package elka.achlebos.model.certificate

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.nio.file.Files

@Tag("unitTest")
class CertificateManagerTest {
    private val creator = X509CertificateCreator(info, path)

    @Test
    fun remoteCertificateTest() {
        // given
        val manager = X509CertificateManager()
        manager.create(info, path)
        // when
        manager.remove(path)
        // then
        Assertions.assertThat(Files.exists(path)).isFalse()
    }
}