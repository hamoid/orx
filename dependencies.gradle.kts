import org.gradle.internal.os.OperatingSystem

extra.apply {
    set("aaa", 214)
    set(
        "openrndrVersion", ((findProperty("OPENRNDR.version")?.toString())
            ?: System.getenv("OPENRNDR_VERSION"))?.replace("v", "")
            ?: "0.5.1-SNAPSHOT"
    )
    set(
        "openrndrOS", when (OperatingSystem.current()) {
            OperatingSystem.WINDOWS -> "windows"
            OperatingSystem.MAC_OS -> "macos"
            else -> "linux-x64"
        }
    )
}

// doesn't work here
// dependencyResolutionManagement {}
