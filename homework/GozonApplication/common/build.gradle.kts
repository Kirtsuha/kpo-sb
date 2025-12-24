plugins { id("java-library") }

java { toolchain { languageVersion.set(JavaLanguageVersion.of(21)) } }

dependencies {
    api("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.2")
}
