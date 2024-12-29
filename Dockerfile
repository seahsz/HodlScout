FROM eclipse-temurin:23.0.1_11-jdk AS builder

WORKDIR /compiledDir

COPY src src
COPY .mvn .mvn
COPY pom.xml .
COPY mvnw .

RUN chmod a+x ./mvnw && ./mvnw package -Dmaven.test.skip=true

FROM eclipse-temurin:23.0.1_11-jdk

WORKDIR /myapp

COPY --from=builder /compiledDir/target/hodlscout-0.0.1-SNAPSHOT.jar hodlscoutApp.jar

# Set the environment variables

ENV SPRING_DATA_REDIS_HOST=localhost
ENV SPRING_DATA_REDIS_PORT=6379
ENV SPRING_DATA_REDIS_USERNAME=
ENV SPRING_DATA_REDIS_PASSWORD=
ENV SPRING_DATA_REDIS_DATABASE=0

ENV SPRING_API_COINGECKO_API_KEY=
ENV SPRING_API_COINGECKO_URL=https://api.coingecko.com/api/v3/

# Api configuration

ENV SPRING_API_COINGECKO_COINS_MARKET_TOTAL_COINS=500

# Each time the coinLoader fetches 2500 coins to populate
# Coin List. Total = 2500 * 2 per day
ENV SPRING_API_COINGECKO_COINS_MARKET_NUM_COINS_LIST=2500

ENV PORT=8080

EXPOSE ${PORT}

ENTRYPOINT SERVER_PORT=${PORT} java -jar hodlscoutApp.jar