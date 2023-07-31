package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Manifest;

public class Version {
  private String commitHash;
  private String commitDate;
  private String commitCount;
  private String buildJDK;

  public Version() {
    try {
      InputStream in = Extractor.extractResourceAsStream("/META-INF/MANIFEST.MF");
      Manifest manifest = new Manifest(in);

      this.commitHash = manifest.getMainAttributes().getValue("Build-Commit-Hash");
      this.commitDate = manifest.getMainAttributes().getValue("Build-Commit-Date");
      this.commitCount = manifest.getMainAttributes().getValue("Build-Commit-Count");
      this.buildJDK = manifest.getMainAttributes().getValue("Build-Jdk");
    } catch (IOException e) {
      this.commitHash = "dev";
      this.commitDate = "2001.01.04";
      this.commitCount = "1";
      this.buildJDK = "unknown";
    }
  }

  public String getCommitHash() {
    return commitHash;
  }

  public String getCommitDate() {
    return commitDate;
  }

  public String getCommitCount() {
    return commitCount;
  }

  public String getBuildJDK() {
    return buildJDK;
  }
}