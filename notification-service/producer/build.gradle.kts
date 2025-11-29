dependencies {
    implementation("io.quarkus:quarkus-messaging-kafka")
    implementation("io.quarkus:quarkus-rest-jackson")
    implementation("io.quarkus:quarkus-smallrye-health")
    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("org.jboss.resteasy:resteasy-client")
    testImplementation("io.rest-assured:rest-assured")
}
quarkus {
    setFinalName("kafka-quickstart-producer")
}
