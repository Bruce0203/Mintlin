# Mintlin (not for production level now)

Mintlin (Min(ecraft) (Ko)tlin) is a Minecraft server fully written in Kotlin,
that aims to customize minigame server, being able to run itself without any of Mojang code.

It is designed to generate dependency-less Java code and in the future JS or WASM code too.
The project can be embedded as a library and also allows compiling code into multiplatform.



**Benchmark: The MacBook M1 can handle approximately 100 players moving every 1 tick.**
![img.png](img.png)


## Expected new scenarios

* Not depending on the JVM.
* AntiCheat implementation on the server.
* Various game protocol versions are supported, enabling users to connect across different versions.
