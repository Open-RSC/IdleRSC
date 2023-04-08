# Gradle Plugins

This provides custom plugins that simplify subproject configuration.

* `bundle-dependencies` - Modifies the `jar` task to include all dependencies.
* `compiler-warnings` - Modifies the `compileJava` task to emit additional
  warnings (deprecation & unchecked).
* `git-metadata` - Provides the following functions: `gitCommitHash`,
  `gitCommitDate`, `gitCommitCount`
* `reproducible-archives` - Ensures all builds are reproducible byte-for-byte.

## References

* [Organising Gradle Projects](https://docs.gradle.org/current/userguide/organizing_gradle_projects.html)
* [Sharing convention plugins with build logic](https://docs.gradle.org/current/samples/sample_sharing_convention_plugins_with_build_logic.html)
