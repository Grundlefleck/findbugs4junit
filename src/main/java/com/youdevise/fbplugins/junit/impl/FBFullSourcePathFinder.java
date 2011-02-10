package com.youdevise.fbplugins.junit.impl;

import java.io.IOException;

import org.apache.bcel.classfile.JavaClass;

import com.youdevise.fbplugins.junit.FullSourcePathFinder;

import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.ba.SourceFile;
import edu.umd.cs.findbugs.ba.SourceFinder;

public class FBFullSourcePathFinder implements FullSourcePathFinder {

	public String fullSourcePath(ClassContext classContext) throws IOException {
		JavaClass javaClass = classContext.getJavaClass();
        String sourceFile = javaClass.getSourceFileName();
        String packageName = javaClass.getPackageName();
        
		SourceFile findSourceFile = sourceFinderFrom(classContext).findSourceFile(packageName, sourceFile);

		return findSourceFile.getFullFileName();
	}

	private SourceFinder sourceFinderFrom(ClassContext classContext) {
		return classContext.getAnalysisContext().getSourceFinder();
	}

}
