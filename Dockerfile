# Usa o Java 21 (ou a versão que você estiver usando)
FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
# Copia o arquivo .jar gerado para dentro do contêiner
COPY target/*.jar app.jar
# Expõe a porta 8080
EXPOSE 8080
# Comando para rodar a aplicação
ENTRYPOINT ["java","-jar","/app.jar"]