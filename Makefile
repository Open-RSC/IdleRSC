# Terminal Colours
RED?=$(shell tput setaf 1)
GREEN?=$(shell tput setaf 2)
YELLOW?=$(shell tput setaf 3)
BLUE?=$(shell tput setaf 4)
BOLD?=$(shell tput bold)
RST?=$(shell tput sgr0)

##@ Build
.PHONY: build
build: ## Build the project
	@./gradlew build

.PHONY: release
release: ## Create a release archive
	@./gradlew release

.PHONY: clean
clean: ## Clean everything up
	@./gradlew clean

##@ Maintenance
.PHONY: check-cache
check-cache: ## Check cache contents and update if necessary
	@scripts/check-cache.sh

.PHONY: check-client
check-client: ## Check client contents and update if necessary
	@scripts/check-client.sh

.PHONY: update-core
update-core: ## Update core repository
	@scripts/get-core-repository.sh

##@ Utility/CI
.DEFAULT_GOAL = help
.PHONY: help
help: ## Display this help
	@awk 'BEGIN {FS = ":.*##"; printf "\nUsage:\n  $(YELLOW)make$(RST) $(BLUE)command$(RST)\n"} /^[a-zA-Z0-9_-]+:.*?##/ { printf "  $(BLUE)%-15s$(RST) %s\n", $$1, $$2 } /^##@/ { printf "\n$(BOLD)%s$(RST)\n", substr($$0, 5) } ' $(MAKEFILE_LIST)

.PHONY: build-javadoc
build-javadoc: ## Build API documentation
	@./gradlew app:javadoc
	@cp -r app/build/docs/javadoc public

.PHONY: check-format
check-format: ## Check if source code is properly formatted
	@./gradlew spotlessCheck

.PHONY: format
format: ## Format source code
	@./gradlew spotlessApply
