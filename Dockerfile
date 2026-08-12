# --- Etapa 1: build ---
# Imagem com Maven + Java, usada só para compilar. Não faz parte da imagem final.
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copiamos primeiro o pom.xml para aproveitar cache: se as dependências não
# mudarem entre deploys, o Docker não baixa tudo de novo, acelerando o build.
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# --- Etapa 2: imagem final, só com o necessário para RODAR ---
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/target/controle-financeiro-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
