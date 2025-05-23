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

.PHONY: run
run: ## Compile & run the project
	@./gradlew run

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

.PHONY: help
help: ## Display this help
	@awk 'BEGIN {FS = ":.*##"; printf "\nUsage:\n  $(YELLOW)make$(RST) $(BLUE)command$(RST)\n"} /^[a-zA-Z0-9_-]+:.*?##/ { printf "  $(BLUE)%-15s$(RST) %s\n", $$1, $$2 } /^##@/ { printf "\n$(BOLD)%s$(RST)\n", substr($$0, 5) } ' $(MAKEFILE_LIST)

##@ CI/CD Pipeline
# Define variables that would typically be provided by a CI/CD environment.
# You can override these when running make, e.g.:
# make ci-upload-package CI_COMMIT_TAG="v1.0.0" CI_JOB_TOKEN="your_token" CI_PROJECT_ID="12345"
CI_COMMIT_TAG     ?= ""
CI_COMMIT_SHORT_SHA ?= $(shell git rev-parse --short HEAD)
CI_JOB_TOKEN      ?= "your_gitlab_job_token_here" # Replace with a real token in CI
CI_API_V4_URL     ?= "https://gitlab.com/api/v4"
CI_PROJECT_ID     ?= "your_gitlab_project_id_here" # Replace with your project ID

# Define a common 'before_script' block for CI/CD targets.
# This block sets up the Gradle user home and installs necessary packages.
define CI_BEFORE_SCRIPT
	@echo "--- Running CI before_script setup ---"
	export GRADLE_USER_HOME=$(CURDIR)/.gradle
	apt-get update --quiet --assume-yes
	apt-get install --quiet --assume-yes git make
endef

.PHONY: ci-upload-package
ci-upload-package: build ## Run the deploy stage (upload package) of the CI pipeline
	$(CI_BEFORE_SCRIPT)
	@echo "--- CI Stage: deploy (upload_package) ---"
	# Determine the version for the package.
	# If CI_COMMIT_TAG is set, use it; otherwise, use a SHA-based version.
	ifeq ($(CI_COMMIT_TAG),)
		VERSION=$(CI_COMMIT_SHORT_SHA)-$(shell date +%Y%m%d-%H%M%S)
	else
		VERSION=$(CI_COMMIT_TAG)
	endif
	@echo "Uploading version: $(VERSION)"
	# Use curl to upload the zip file to the GitLab API.
	curl --header "JOB-TOKEN: $(CI_JOB_TOKEN)" \
		--upload-file IdleRSC.zip \
		"$(CI_API_V4_URL)/projects/$(CI_PROJECT_ID)/packages/generic/IdleRSC/$(VERSION)/IdleRSC-$(VERSION).zip"
