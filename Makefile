.PHONY: dev build test migrate

dev:
	./gradlew bootRun

build:
	./gradlew clean build

test:
	./gradlew test
run:
    ./gradlew bootRun
migrate:
	./gradlew createMigration -Pname=$(name)
unmigrate:
    ./gradlew flywayUndo