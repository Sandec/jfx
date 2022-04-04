# Publish:
1.
`./gradlew sdk graphics:publishToMavenLocal base:publishToMavenLocal`

2. publish all local:`
   `./gradlew publishToMavenLocal`

3. publish online
   `./gradlew clean sdk publish`

4. clear cache (optional) (tested for sbt)
   `find ~/.m2 ~/.ivy2 ~/.gradle | grep openjfx | grep jpro | xargs rm -r`


# OpenJFX

OpenJFX is an open source, next generation client application platform for desktop, mobile and embedded systems based on JavaSE. It is a collaborative effort by many individuals and companies with the goal of producing a modern, efficient, and fully featured toolkit for developing rich client applications. This is the open source project where we develop JavaFX.

OpenJFX is free software, licensed under [GPL v2 with the Classpath exception](LICENSE), just like the JDK. Anybody is welcome to contribute to this project, port it to other platforms or devices, or do anything else that a free software license allows you to do!

OpenJFX is a project under the charter of the OpenJDK. The [OpenJDK Bylaws](https://openjdk.java.net/bylaws) and [License](LICENSE) govern our work. The OpenJFX project membership can be found on the [OpenJDK Census](https://openjdk.java.net/census#openjfx). We welcome patches and involvement from individual contributors or companies. If this is your first time contributing to an OpenJDK project, you will need to review the rules on [becoming a Contributor](https://openjdk.java.net/bylaws#contributor), and sign the [Oracle Contributor Agreement](https://oca.opensource.oracle.com/) (OCA).

This repository has the source code for OpenJFX 17 Update Releases. The main repository for OpenJFX development is [https://github.com/openjdk/jfx](https://github.com/openjdk/jfx).


## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on submitting backport pull requests to OpenJFX 17. If you are looking to contribute to OpenJFX, please visit [openjdk/jfx](https://github.com/openjdk/jfx) instead.
