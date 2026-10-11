package dev.naspo.packmanagerpro.applicationtype

enum class ApplicationType(val string: String) {
    GLOBAL("global"),
    PER_WORLD("per-world");

    companion object {
        /**
         * Creates an [ApplicationType] from a string.
         * @return The resulting [ApplicationType]. Or null if the provided string was invalid.
         */
        fun fromString(string: String): ApplicationType? {
            return entries.firstOrNull { it.string.equals(string, ignoreCase = true) }
        }
    }
}