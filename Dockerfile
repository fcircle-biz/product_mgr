FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

ARG SKIP_TESTS=false
ENV SKIP_TESTS=${SKIP_TESTS}

COPY src ./src
RUN if [ "$SKIP_TESTS" = "true" ] ; then mvn package -DskipTests ; else mvn package ; fi

FROM amazoncorretto:17
WORKDIR /app
COPY --from=build /app/target/product-mgr-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]