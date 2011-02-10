package com.youdevise.fbplugins.junit;

public class VersionControlledSourceFileFinder {

    private final PluginProperties properties;

    public VersionControlledSourceFileFinder(PluginProperties properties) {
        this.properties = properties;
    }

    public String location(String fullFilePath) {
        String vcsRoot = stripTrailingSlash(properties.versionControlProjectRoot());
        String baseDirPath = properties.projectBaseDirName();
        int indexOfDirInProjectBaseDir = fullFilePath.lastIndexOf(baseDirPath) + properties.projectBaseDirName().length();
        String pathFromBaseDirOfLocalProject = stripLeadingSlash(fullFilePath.substring(indexOfDirInProjectBaseDir));
        
        String fullVcsPath = vcsRoot + "/" + pathFromBaseDirOfLocalProject;
        
        return fullVcsPath;
    }

    private String stripLeadingSlash(String path) {
        while(path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }
    private String stripTrailingSlash(String path) {
        while(path.endsWith("/")) {
            path = path.substring(0, path.lastIndexOf("/"));
        }
        return path;
    }

}
