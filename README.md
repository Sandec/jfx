# Publish:
1.
`./gradlew sdk graphics:publishToMavenLocal base:publishToMavenLocal`

2. publish all local:`
   `./gradlew publishToMavenLocal`

3. publish online
   `./gradlew clean sdk publish`

4. clear cache (optional) (tested for sbt)
   `find ~/Library/Caches/Coursier/v1 ~/.m2 ~/.ivy2 ~/.gradle | grep openjfx | grep jpro | xargs rm -r`


MAC INTEL
./gradlew clean && ./gradlew sdk -PTARGET_ARCH=x86_64 && ./gradlew publish -PMAVEN_PUBLISH=true -PCLASSIFIER_POSTFIX=

MAC ARM
./gradlew clean && ./gradlew sdk -PTARGET_ARCH=arm64 && ./gradlew publish -PMAVEN_PUBLISH=true -PCLASSIFIER_POSTFIX=-aarch64

Linux ARM64 (Run only on ARM64 machine)
./gradlew clean && ./gradlew sdk -PTARGET_ARCH=arm64 && ./gradlew publish -PMAVEN_PUBLISH=true -PCLASSIFIER_POSTFIX=-aarch64



# OpenJFX 21 Updates

This repository has the source code for OpenJFX 21 Update Releases. The main repository for OpenJFX development is [https://github.com/openjdk/jfx](https://github.com/openjdk/jfx).


## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on submitting backport pull requests to OpenJFX 21. If you are looking to contribute to OpenJFX, please visit [openjdk/jfx](https://github.com/openjdk/jfx) instead.
