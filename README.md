
```
vno
├─ auth-service
│  ├─ .dockerignore
│  ├─ .mvn
│  │  └─ wrapper
│  │     ├─ maven-wrapper.jar
│  │     └─ maven-wrapper.properties
│  ├─ mvnw
│  ├─ mvnw.cmd
│  ├─ pom.xml
│  ├─ README.adoc
│  ├─ src
│  │  ├─ main
│  │  │  ├─ docker
│  │  │  │  ├─ Dockerfile.jvm
│  │  │  │  ├─ Dockerfile.legacy-jar
│  │  │  │  ├─ Dockerfile.native
│  │  │  │  └─ Dockerfile.native-micro
│  │  │  ├─ java
│  │  │  │  └─ com
│  │  │  │     └─ vno
│  │  │  │        ├─ auth
│  │  │  │        │  └─ entity
│  │  │  │        │     └─ User.java
│  │  │  │        └─ security
│  │  │  │           └─ jwt
│  │  │  │              └─ TokenSecuredResource.java
│  │  │  └─ resources
│  │  │     ├─ application.properties
│  │  │     └─ publicKey.pem
│  │  └─ test
│  │     ├─ java
│  │     │  └─ com
│  │     │     └─ vno
│  │     │        └─ security
│  │     │           └─ jwt
│  │     │              ├─ GenerateToken.java
│  │     │              ├─ TokenSecuredResourceIT.java
│  │     │              └─ TokenSecuredResourceTest.java
│  │     └─ resources
│  │        └─ privateKey.pem
│  └─ target
│     ├─ build-metrics.json
│     ├─ classes
│     │  ├─ application.properties
│     │  ├─ org
│     │  │  └─ acme
│     │  │     ├─ auth
│     │  │     │  └─ entity
│     │  │     │     └─ User.class
│     │  │     └─ security
│     │  │        └─ jwt
│     │  │           └─ TokenSecuredResource.class
│     │  └─ publicKey.pem
│     ├─ generated-sources
│     │  └─ annotations
│     ├─ generated-test-sources
│     │  └─ test-annotations
│     ├─ maven-status
│     │  └─ maven-compiler-plugin
│     │     ├─ compile
│     │     │  └─ null
│     │     │     ├─ createdFiles.lst
│     │     │     └─ inputFiles.lst
│     │     └─ testCompile
│     │        └─ null
│     │           ├─ createdFiles.lst
│     │           └─ inputFiles.lst
│     ├─ quarkus
│     │  └─ bootstrap
│     └─ test-classes
│        ├─ org
│        │  └─ acme
│        │     └─ security
│        │        └─ jwt
│        │           ├─ GenerateToken.class
│        │           ├─ TokenSecuredResourceIT.class
│        │           └─ TokenSecuredResourceTest.class
│        └─ privateKey.pem
├─ common-observability
│  ├─ .dockerignore
│  ├─ .mvn
│  │  └─ wrapper
│  │     ├─ maven-wrapper.jar
│  │     └─ maven-wrapper.properties
│  ├─ docker-compose.yml
│  ├─ mvnw
│  ├─ mvnw.cmd
│  ├─ otel-collector-config.yaml
│  ├─ pom.xml
│  ├─ README.md
│  ├─ src
│  │  ├─ main
│  │  │  ├─ docker
│  │  │  │  ├─ Dockerfile.jvm
│  │  │  │  ├─ Dockerfile.legacy-jar
│  │  │  │  ├─ Dockerfile.native
│  │  │  │  └─ Dockerfile.native-micro
│  │  │  ├─ java
│  │  │  │  └─ com
│  │  │  │     └─ vno
│  │  │  │        └─ opentelemetry
│  │  │  │           ├─ ResourceClient.java
│  │  │  │           └─ TracedResource.java
│  │  │  └─ resources
│  │  │     └─ application.properties
│  │  └─ test
│  │     └─ java
│  │        └─ com
│  │           └─ vno
│  │              └─ opentelemetry
│  │                 ├─ TracedResourceIT.java
│  │                 └─ TracedResourceTest.java
│  └─ target
│     ├─ classes
│     │  ├─ application.properties
│     │  └─ org
│     │     └─ acme
│     │        └─ opentelemetry
│     │           ├─ ResourceClient.class
│     │           └─ TracedResource.class
│     └─ test-classes
│        └─ org
│           └─ acme
│              └─ opentelemetry
│                 ├─ TracedResourceIT.class
│                 └─ TracedResourceTest.class
├─ common-openapi
│  ├─ .dockerignore
│  ├─ .mvn
│  │  └─ wrapper
│  │     ├─ maven-wrapper.jar
│  │     └─ maven-wrapper.properties
│  ├─ mvnw
│  ├─ mvnw.cmd
│  ├─ pom.xml
│  ├─ README.md
│  ├─ src
│  │  ├─ main
│  │  │  ├─ docker
│  │  │  │  ├─ Dockerfile.jvm
│  │  │  │  ├─ Dockerfile.legacy-jar
│  │  │  │  ├─ Dockerfile.native
│  │  │  │  └─ Dockerfile.native-micro
│  │  │  ├─ java
│  │  │  │  └─ com
│  │  │  │     └─ vno
│  │  │  │        └─ openapi
│  │  │  │           └─ swaggerui
│  │  │  │              ├─ Fruit.java
│  │  │  │              └─ FruitResource.java
│  │  │  └─ resources
│  │  │     └─ application.properties
│  │  └─ test
│  │     └─ java
│  │        └─ com
│  │           └─ vno
│  │              └─ openapi
│  │                 └─ swaggerui
│  │                    ├─ FruitResourceIT.java
│  │                    ├─ FruitResourceTest.java
│  │                    ├─ OpenApiIT.java
│  │                    ├─ OpenApiTest.java
│  │                    └─ SwaggerUiTest.java
│  └─ target
│     ├─ build-metrics.json
│     ├─ classes
│     │  ├─ application.properties
│     │  └─ org
│     │     └─ acme
│     │        └─ openapi
│     │           └─ swaggerui
│     │              ├─ Fruit.class
│     │              └─ FruitResource.class
│     ├─ generated-sources
│     │  └─ annotations
│     ├─ generated-test-sources
│     │  └─ test-annotations
│     ├─ maven-status
│     │  └─ maven-compiler-plugin
│     │     ├─ compile
│     │     │  └─ null
│     │     │     ├─ createdFiles.lst
│     │     │     └─ inputFiles.lst
│     │     └─ testCompile
│     │        └─ null
│     │           ├─ createdFiles.lst
│     │           └─ inputFiles.lst
│     ├─ quarkus
│     │  └─ bootstrap
│     └─ test-classes
│        └─ org
│           └─ acme
│              └─ openapi
│                 └─ swaggerui
│                    ├─ FruitResourceIT.class
│                    ├─ FruitResourceTest.class
│                    ├─ OpenApiIT.class
│                    ├─ OpenApiTest.class
│                    └─ SwaggerUiTest.class
├─ note-service
│  ├─ .dockerignore
│  ├─ .mvn
│  │  └─ wrapper
│  │     ├─ maven-wrapper.jar
│  │     └─ maven-wrapper.properties
│  ├─ mvnw
│  ├─ mvnw.cmd
│  ├─ pom.xml
│  ├─ README.md
│  ├─ src
│  │  ├─ main
│  │  │  ├─ docker
│  │  │  │  ├─ Dockerfile.jvm
│  │  │  │  ├─ Dockerfile.legacy-jar
│  │  │  │  ├─ Dockerfile.native
│  │  │  │  └─ Dockerfile.native-micro
│  │  │  ├─ java
│  │  │  │  └─ org
│  │  │  │     └─ acme
│  │  │  │        └─ hibernate
│  │  │  │           └─ orm
│  │  │  │              └─ panache
│  │  │  │                 ├─ Fruit.java
│  │  │  │                 └─ FruitResource.java
│  │  │  └─ resources
│  │  │     ├─ application.properties
│  │  │     ├─ import.sql
│  │  │     └─ META-INF
│  │  │        └─ resources
│  │  │           └─ index.html
│  │  └─ test
│  │     └─ java
│  │        └─ com
│  │           └─ vno
│  │              └─ hibernate
│  │                 └─ orm
│  │                    └─ panache
│  │                       ├─ FruitsEndpointIT.java
│  │                       └─ FruitsEndpointTest.java
│  └─ target
│     ├─ build-metrics.json
│     ├─ classes
│     │  ├─ application.properties
│     │  ├─ import.sql
│     │  ├─ META-INF
│     │  │  └─ resources
│     │  │     └─ index.html
│     │  └─ org
│     │     └─ acme
│     │        └─ hibernate
│     │           └─ orm
│     │              └─ panache
│     │                 ├─ Fruit.class
│     │                 ├─ FruitResource$ErrorMapper.class
│     │                 └─ FruitResource.class
│     ├─ generated-sources
│     │  └─ annotations
│     ├─ generated-test-sources
│     │  └─ test-annotations
│     ├─ maven-status
│     │  └─ maven-compiler-plugin
│     │     ├─ compile
│     │     │  └─ null
│     │     │     ├─ createdFiles.lst
│     │     │     └─ inputFiles.lst
│     │     └─ testCompile
│     │        └─ null
│     │           ├─ createdFiles.lst
│     │           └─ inputFiles.lst
│     ├─ quarkus
│     │  └─ bootstrap
│     └─ test-classes
│        └─ org
│           └─ acme
│              └─ hibernate
│                 └─ orm
│                    └─ panache
│                       ├─ FruitsEndpointIT.class
│                       └─ FruitsEndpointTest.class
├─ notification-service
│  ├─ .dockerignore
│  ├─ .mvn
│  │  └─ wrapper
│  │     ├─ maven-wrapper.jar
│  │     └─ maven-wrapper.properties
│  ├─ docker-compose.yaml
│  ├─ mvnw
│  ├─ mvnw.cmd
│  ├─ pom.xml
│  ├─ processor
│  │  ├─ pom.xml
│  │  ├─ src
│  │  │  ├─ main
│  │  │  │  ├─ docker
│  │  │  │  │  ├─ Dockerfile.jvm
│  │  │  │  │  ├─ Dockerfile.legacy-jar
│  │  │  │  │  ├─ Dockerfile.native
│  │  │  │  │  └─ Dockerfile.native-micro
│  │  │  │  ├─ java
│  │  │  │  │  └─ org
│  │  │  │  │     └─ acme
│  │  │  │  │        └─ kafka
│  │  │  │  │           ├─ model
│  │  │  │  │           │  └─ Quote.java
│  │  │  │  │           └─ processor
│  │  │  │  │              └─ QuotesProcessor.java
│  │  │  │  └─ resources
│  │  │  │     └─ application.properties
│  │  │  └─ test
│  │  │     └─ java
│  │  │        └─ org
│  │  │           └─ acme
│  │  │              └─ kafka
│  │  │                 └─ processor
│  │  │                    └─ QuoteProcessorTest.java
│  │  └─ target
│  │     ├─ classes
│  │     │  ├─ application.properties
│  │     │  └─ org
│  │     │     └─ acme
│  │     │        └─ kafka
│  │     │           ├─ model
│  │     │           │  └─ Quote.class
│  │     │           └─ processor
│  │     │              └─ QuotesProcessor.class
│  │     └─ test-classes
│  │        └─ org
│  │           └─ acme
│  │              └─ kafka
│  │                 └─ processor
│  │                    └─ QuoteProcessorTest.class
│  ├─ producer
│  │  ├─ pom.xml
│  │  ├─ src
│  │  │  ├─ main
│  │  │  │  ├─ docker
│  │  │  │  │  ├─ Dockerfile.jvm
│  │  │  │  │  ├─ Dockerfile.legacy-jar
│  │  │  │  │  ├─ Dockerfile.native
│  │  │  │  │  └─ Dockerfile.native-micro
│  │  │  │  ├─ java
│  │  │  │  │  └─ org
│  │  │  │  │     └─ acme
│  │  │  │  │        └─ kafka
│  │  │  │  │           ├─ model
│  │  │  │  │           │  ├─ Quote.java
│  │  │  │  │           │  └─ QuoteDeserializer.java
│  │  │  │  │           └─ producer
│  │  │  │  │              └─ QuotesResource.java
│  │  │  │  └─ resources
│  │  │  │     ├─ application.properties
│  │  │  │     └─ META-INF
│  │  │  │        └─ resources
│  │  │  │           └─ quotes.html
│  │  │  └─ test
│  │  │     └─ java
│  │  │        └─ org
│  │  │           └─ acme
│  │  │              └─ kafka
│  │  │                 └─ producer
│  │  │                    ├─ QuotesResourceIT.java
│  │  │                    └─ QuotesResourceTest.java
│  │  └─ target
│  │     ├─ classes
│  │     │  ├─ application.properties
│  │     │  ├─ META-INF
│  │     │  │  └─ resources
│  │     │  │     └─ quotes.html
│  │     │  └─ org
│  │     │     └─ acme
│  │     │        └─ kafka
│  │     │           ├─ model
│  │     │           │  ├─ Quote.class
│  │     │           │  └─ QuoteDeserializer.class
│  │     │           └─ producer
│  │     │              └─ QuotesResource.class
│  │     └─ test-classes
│  │        └─ org
│  │           └─ acme
│  │              └─ kafka
│  │                 └─ producer
│  │                    ├─ QuotesResourceIT.class
│  │                    └─ QuotesResourceTest.class
│  └─ README.md
├─ realtime-collab-service
│  ├─ .mvn
│  │  └─ wrapper
│  │     ├─ maven-wrapper.jar
│  │     └─ maven-wrapper.properties
│  ├─ mvnw
│  ├─ mvnw.cmd
│  ├─ pom.xml
│  ├─ README.md
│  ├─ src
│  │  └─ main
│  │     ├─ java
│  │     │  └─ org
│  │     │     └─ acme
│  │     │        └─ websockets
│  │     │           └─ ChatWebSocket.java
│  │     └─ resources
│  │        └─ META-INF
│  │           └─ resources
│  │              └─ index.html
│  └─ target
│     ├─ classes
│     │  ├─ META-INF
│     │  │  └─ resources
│     │  │     └─ index.html
│     │  └─ org
│     │     └─ acme
│     │        └─ websockets
│     │           ├─ ChatWebSocket$ChatMessage.class
│     │           ├─ ChatWebSocket$MessageType.class
│     │           └─ ChatWebSocket.class
│     └─ test-classes
├─ rename-packages.ps1
└─ user-service
   ├─ .dockerignore
   ├─ .mvn
   │  └─ wrapper
   │     ├─ maven-wrapper.jar
   │     └─ maven-wrapper.properties
   ├─ mvnw
   ├─ mvnw.cmd
   ├─ pom.xml
   ├─ README.md
   ├─ src
   │  ├─ main
   │  │  ├─ docker
   │  │  │  ├─ Dockerfile.jvm
   │  │  │  ├─ Dockerfile.legacy-jar
   │  │  │  ├─ Dockerfile.native
   │  │  │  └─ Dockerfile.native-micro
   │  │  ├─ java
   │  │  │  └─ org
   │  │  │     └─ acme
   │  │  │        └─ hibernate
   │  │  │           └─ orm
   │  │  │              └─ panache
   │  │  │                 ├─ entity
   │  │  │                 │  ├─ FruitEntity.java
   │  │  │                 │  └─ FruitEntityResource.java
   │  │  │                 └─ repository
   │  │  │                    ├─ Fruit.java
   │  │  │                    ├─ FruitRepository.java
   │  │  │                    └─ FruitRepositoryResource.java
   │  │  └─ resources
   │  │     ├─ application.properties
   │  │     ├─ import.sql
   │  │     └─ META-INF
   │  │        └─ resources
   │  │           └─ index.html
   │  └─ test
   │     └─ java
   │        └─ org
   │           └─ acme
   │              └─ hibernate
   │                 └─ orm
   │                    └─ panache
   │                       ├─ FruitsEndpointIT.java
   │                       └─ FruitsEndpointTest.java
   └─ target
      ├─ build-metrics.json
      ├─ classes
      │  ├─ application.properties
      │  ├─ import.sql
      │  ├─ META-INF
      │  │  └─ resources
      │  │     └─ index.html
      │  └─ org
      │     └─ acme
      │        └─ hibernate
      │           └─ orm
      │              └─ panache
      │                 ├─ entity
      │                 │  ├─ FruitEntity.class
      │                 │  ├─ FruitEntityResource$ErrorMapper.class
      │                 │  └─ FruitEntityResource.class
      │                 └─ repository
      │                    ├─ Fruit.class
      │                    ├─ FruitRepository.class
      │                    ├─ FruitRepositoryResource$ErrorMapper.class
      │                    └─ FruitRepositoryResource.class
      ├─ generated-sources
      │  └─ annotations
      ├─ generated-test-sources
      │  └─ test-annotations
      ├─ maven-status
      │  └─ maven-compiler-plugin
      │     ├─ compile
      │     │  └─ null
      │     │     ├─ createdFiles.lst
      │     │     └─ inputFiles.lst
      │     └─ testCompile
      │        └─ null
      │           ├─ createdFiles.lst
      │           └─ inputFiles.lst
      ├─ quarkus
      │  └─ bootstrap
      └─ test-classes
         └─ org
            └─ acme
               └─ hibernate
                  └─ orm
                     └─ panache
                        ├─ FruitsEndpointIT.class
                        └─ FruitsEndpointTest.class

```


build docker image
```
docker build -f src/main/docker/Dockerfile.jvm -t vno-auth-service:latest .
```