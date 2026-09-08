.PHONY: dev build test migrate

dev:
	./gradlew bootRun

build:
	./gradlew clean build

test:
	./gradlew test

migrate:
	./gradlew createMigration -Pname=$(name)
unmigrate:
    ./gradlew flywayUndo