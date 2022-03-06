# Messari Market Data Challenge

## How to build

Run the following command from the root of the project
````bash
gradle bootJar
````

## How to run it

### Requirements

This is build written to work on OpenJDK 11

Run the following command from the root of the project

```bash
java -jar app/build/libs/messari-market-data-coding-challenge.jar --app.folder="/path/to/input/data" --app.filename="output.txt" --server.port=8081
```

### All-in-one version

```bash
docker build -t messari:latest .
docker run -v /path/to/input/data/output.txt:/output.txt -e FOLDER="/" -e FILENAME="output.txt" -t messari:latest
```