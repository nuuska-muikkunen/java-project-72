DEFAULT_GOAL := build-run

setup:
	gradle wrapper --gradle-version 8.3

clean:
	./app/gradlew -p app clean

build:
	./app/gradlew -p app clean build

install:
	./app/gradlew -p app clean install

run-dist:
	./app/build/install/app/bin/app

run:
	./app/gradlew -p app run

test:
	./app/gradlew -p app test

report:
	./app/gradlew -p app jacocoTestReport

lint:
	./app/gradlew -p app checkstyleMain

check-deps:
	./app/gradlew -p app dependencyUpdates -Drevision=release

build-run: build run

.PHONY: build
