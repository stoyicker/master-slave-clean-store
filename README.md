# Documentation
Documentation is generated using [Dokka](https://github.com/Kotlin/dokka), which is the
code documentation generation tool for Kotlin, similar to what Javadoc is for Java.
`index.html` for the documentation of each module can be found in their `build` directories:
 `module_name/build/dokka/module_name/index.html`.
# Tests
 Unit and integration tests are written using [Spek](htts://spekframework.org), the specification
 framework for Kotlin. Run them with the `test` Gradle task in each module.
 Instrumentation tests are only present in the `app` module and can be run using the `cAT` task.
