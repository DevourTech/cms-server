include scripts.mk

.ONESHELL:
.DEFAULT_GOAL := run

.PHONY: build
build:
	@echo "Installing dependencies..."
	@mvn clean install -U -s settings.xml

.PHONY: run
run: build
	@echo "Launching Spring REST API Service..."
	@mvn spring-boot:run

.PHONY: test
test: build
	@echo "Running server tests..."
	@mvn test

.PHONY: fmt
fmt: ; @$(value format_if_executable_present)

.PHONY: install-formatter
install-formatter:
	@npm list --global prettier || npm install -g prettier prettier-plugin-java
	@echo "In an event of a permission error from npm, run sudo make install-formatter"