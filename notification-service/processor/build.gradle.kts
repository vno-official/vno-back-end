dependencies {
    implementation("io.quarkus:quarkus-messaging-kafka")
    testImplementation("io.quarkus:quarkus-junit5")
}
quarkus {
    setFinalName("kafka-quickstart-processor")
}
