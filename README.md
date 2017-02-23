# Build instructions

Place the key to the API in the root gradle.properties file following
the format below:
````
prop_api_url="https://api-url.com/"
````

__Make sure to include the quotes.__

# Architecture
This is a reactive app: it runs by reacting to user interactions. Here
is how:
![Architecture](Diagram1.png)

# Language choice
I chose Kotlin over Java because:
* It compiles to Java bytecode.
* It is less verbose than Java.
* It is more natural both to read and write, which makes
writing code easier and faster while still allowing Java developers
who have never seen it to understand it.

# Documentation
Documentation is generated using [Dokka](https://github.com/Kotlin/dokka), which is the
code documentation generation tool for Kotlin, similar to what Javadoc is for Java.
`index.html` for the documentation of each module can be found in their `build` directories:
 `module_name/build/dokka/module_name/index.html`.

# Tests
 Unit and integration tests are written using [Spek](https://spekframework.org), the specification
 framework for Kotlin. Run them with the `test` Gradle task in each module.
 Instrumentation tests are only present in the `app` module and can be run using the `cAT` task.
