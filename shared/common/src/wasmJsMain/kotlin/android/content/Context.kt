package android.content

class Context {
    val resources: Resources = Resources()
}

class Resources {
    fun getIdentifier(name: String, defType: String, defPackage: String): Int = 0
}
