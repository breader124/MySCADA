package elka.achlebos.model

import java.lang.Exception

class CertificateCreationException(message: String) : Exception(message)

class CertificateLoadingException(message: String) : Exception(message)