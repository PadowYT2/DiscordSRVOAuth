{pkgs, ...}: {
  languages.java = {
    enable = true;
    jdk.package = pkgs.temurin-bin-21;
  };
}
