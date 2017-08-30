# Build instructions

`./gradlew assemble` (or something like `gradlew.bat assemble` on Windows I guess).
You can also get an APK from the [Releases](https://github.com/stoyicker/master-slave-clean-store/releases) 
tab, courtesy of Travis. 

# Architecture
This is a reactive app: it runs by reacting to user interactions. Here
is how:
![Architecture](Diagram1.png)

# Language choice
I chose Kotlin over Java because:
* It is less verbose than Java.
* It is more natural both to read and write, which makes
writing code easier and faster while still allowing Java developers
who have never seen it to understand it.
* It can be configured to generate Java 6/8 bytecode, which means its evolution is independent of that of the platform.
* It is [officially supported by Google as a first-class language for Android](https://blog.jetbrains.com/kotlin/2017/05/kotlin-on-android-now-official/).

# Documentation
Documentation is generated using [Dokka](https://github.com/Kotlin/dokka), which is the
code documentation generation tool for Kotlin, similar to what Javadoc is for Java.
`index.html` for the documentation of each module can be found in their `build` directories:
 `module_name/build/dokka/module_name/index.html`.

# Tests
 Unit and integration tests are written using [Spek](https://spekframework.org), the specification
 framework for Kotlin. Run them with the `test` Gradle task in each module.
 Instrumentation tests are only present in the `app` module and can be run using the `cAT` task.

# Setup for contributions

Once cloned, just setup the hooks:

```shell
$<project-dir>: ./hooks/setup (or whatever equivalent if on Windows).
```
