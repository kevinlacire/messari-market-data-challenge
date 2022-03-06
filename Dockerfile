FROM openjdk:11-jre-slim
WORKDIR /
ADD app/build/libs/messari-market-data-coding-challenge.jar app.jar
EXPOSE 8081
ENV FOLDER="/"
ENV FILENAME="output.txt"
CMD java -jar app.jar --app.folder="$FOLDER" --app.filename="$FILENAME"