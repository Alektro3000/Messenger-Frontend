package git.alektro3000.messenger

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform