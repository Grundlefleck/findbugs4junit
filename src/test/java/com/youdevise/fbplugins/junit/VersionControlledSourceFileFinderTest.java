package com.youdevise.fbplugins.junit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class VersionControlledSourceFileFinderTest {
    @Test public void
    usesFullFilePathAndVersionControlPropertiesToCreateAnHttpLocationOfSourceFile() throws Exception {
        PluginProperties properties = PluginProperties.fromArguments("http://somehost/some/vcs/dir/trunk", 
                                                                     "/home/jemima/workspace/MyProject", "14", "");
        String fullFilePath = "/home/jemima/workspace/MyProject/src/org/surrender/MyMainClass.java";
        
        String location = new VersionControlledSourceFileFinder(properties).location(fullFilePath);
        
        assertThat(location, is("http://somehost/some/vcs/dir/trunk/src/org/surrender/MyMainClass.java"));
    }
    
    @Test public void
    willHandleExtraSlashes() throws Exception {
        PluginProperties properties = PluginProperties.fromArguments("http://somehost/some/vcs/dir/trunk/",
                                                                     "/home/jemima/workspace/MyProject/", "14", "");
        String fullFilePath = "/home/jemima/workspace/MyProject/src/org/surrender/MyMainClass.java";
        
        String location = new VersionControlledSourceFileFinder(properties).location(fullFilePath);
        
        assertThat(location, is("http://somehost/some/vcs/dir/trunk/src/org/surrender/MyMainClass.java"));
    }
    
    @Test public void
    usesRelativePathAsSourceFileLocationWhenNoVersionControlProjectRootIsGiven() throws Exception {
        PluginProperties properties = PluginProperties.fromArguments(null, "/home/user/projects/bockbier", "7", "");
        
        String location = new VersionControlledSourceFileFinder(properties).location("/home/user/projects/bockbier/de/andechs/Dunkel.java");
        
        assertThat(location, is("de/andechs/Dunkel.java"));
    }
}
