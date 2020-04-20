package elka.achlebos.model.certificate

import java.lang.Exception

class CertificateCreationException(message: String) : Exception(message)

class CertificateLoadingException(message: String) : Exception(message)