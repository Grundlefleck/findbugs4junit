package com.youdevise.fbplugins;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.youdevise.fbplugins.junit.PluginProperties;
import com.youdevise.fbplugins.junit.VersionControlledSourceFileFinder;

public class VersionControlledSourceFileFinderTest {
    @Test public void
    usesFullFilePathAndVersionControlPropertiesToCreateAnHttpLocationOfSourceFile() throws Exception {
        PluginProperties properties = PluginProperties.fromArguments("http://somehost", "/some/vcs/dir/MyProject", "MyProject");
        String fullFilePath = "/home/jemima/workspace/MyProject/src/org/surrender/MyMainClass.java";
        
        String location = new VersionControlledSourceFileFinder(properties).location(fullFilePath);
        
        assertThat(location, is("http://somehost/some/vcs/dir/MyProject/src/org/surrender/MyMainClass.java"));
    }
}
