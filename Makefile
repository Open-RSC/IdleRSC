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
	./gradlew build

.PHONY: release
release: ## Create a release archive
	./gradlew release

.PHONY: clean
clean: ## Clean everything up
	./gradlew clean

##@ Maintenance
.PHONY: compare-cache
compare-cache: ## Compare cache contents
	@scripts/compare-cache.sh

.PHONY: compare-client
compare-client: ## Compare client contents
	@scripts/compare-client.sh

.PHONY: update-cache
update-cache: ## Update client cache
	@scripts/update-cache.sh

.PHONY: update-client
update-client: ## Update client source
	@scripts/update-client.sh

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
	./gradlew app:javadoc
	cp -r app/build/docs/javadoc public

.PHONY: check-format
check-format: ## Check if source code is properly formatted
	./gradlew spotlessCheck
