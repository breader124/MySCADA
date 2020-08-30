package elka.achlebos.viewmodel


enum class NodeReadOption {
    NODE_ID,
    NODE_CLASS,
    BROWSE_NAME,
    DISPLAY_NAME,
    DESCRIPTION,
    WRITE_MASKS,
    USER_WRITE_MASK,
    VALUE,
    DATA_TYPE,
    VALUE_RANK,
    ARRAY_DIMENSION,
    ACCESS_LEVEL,
    USER_ACCESS_LEVEL,
    MINIMUM_SAMPLING_INTERVAL,
    HISTORIZING;

    override fun toString(): String {
        val name = super.toString()
        return name.spacesSeparatedAndCapitalized()
    }
}

enum class CatalogueReadOption {
    NODE_ID,
    NODE_CLASS,
    BROWSE_NAME,
    DISPLAY_NAME,
    DESCRIPTION,
    WRITE_MASKS,
    USER_WRITE_MASK,
    EVENT_NOTIFIER;

    override fun toString(): String {
        val name = super.toString()
        return name.spacesSeparatedAndCapitalized()
    }
}

fun String.spacesSeparatedAndCapitalized(): String = this
        .split("_", " ")
        .joinToString(" ") { it.toLowerCase().capitalize() }