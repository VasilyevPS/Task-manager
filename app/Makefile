setup:
	gradle wrapper --gradle-version 8.2.1

clean:
	./gradlew clean

build:
	./gradlew clean build

start:
	./gradlew bootRun

lint:
	./gradlew checkstyleMain checkstyleTest

test:
	./gradlew test

report:
	./gradlew jacocoTestReport

.PHONY: build