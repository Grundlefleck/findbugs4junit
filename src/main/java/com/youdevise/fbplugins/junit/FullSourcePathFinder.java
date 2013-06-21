package com.youdevise.fbplugins.junit;

import java.io.IOException;

import edu.umd.cs.findbugs.ba.ClassContext;

public interface FullSourcePathFinder {

    String fullSourcePath(ClassContext classContext) throws IOException;
    
}
