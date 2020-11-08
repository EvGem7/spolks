package me.evgem.client.model

enum class CommandType {
    Echo,
    Time,
    Close,
    Download,
    Upload,
    Stop,
    Connect;

    companion object {
        fun fromKey(key: String): CommandType? {
            CommandType.values().forEach {
                if (it.name.equals(key, true)) {
                    return it
                }
            }
            return null
        }
    }
}
