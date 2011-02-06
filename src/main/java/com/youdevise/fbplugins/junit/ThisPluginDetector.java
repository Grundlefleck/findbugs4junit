package com.youdevise.fbplugins.junit;

import com.youdevise.fbplugins.junit.impl.FBFullSourcePathFinder;
import com.youdevise.fbplugins.junit.impl.JUnitTestVisitor;
import com.youdevise.fbplugins.junit.impl.SvnAgeOfIgnoreFinder;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.asm.FBClassReader;
import edu.umd.cs.findbugs.ba.AnalysisContext;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.classfile.CheckedAnalysisException;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.classfile.Global;

public class ThisPluginDetector implements Detector {
	
	
    static {
//    	DAVRepositoryFactory.setup();
        System.out.printf("Registered plugin detector [%s]%n", JUnitTestIgnoredForTooLong.class.getSimpleName());
    }
	private final BugReporter bugReporter;

	public ThisPluginDetector(BugReporter bugReporter) {
		this.bugReporter = bugReporter;
	}
	
	private JUnitTestIgnoredForTooLong createActualDetector(ClassContext classContext) {
		AgeOfIgnoreFinder ageOfIgnoreFinder = new SvnAgeOfIgnoreFinder();
		FullSourcePathFinder fullSourcePathFinder = new FBFullSourcePathFinder();
		UnitTestVisitor unitTestVisitor = analyseClassToDiscoverIgnoredTestCases(classContext);
		return new JUnitTestIgnoredForTooLong(bugReporter, ageOfIgnoreFinder, fullSourcePathFinder, unitTestVisitor);
	}

	@Override public void report() { }

	@Override public void visitClassContext(ClassContext classContext) {
		createActualDetector(classContext).visitClassContext(classContext);
	}
	
	private JUnitTestVisitor analyseClassToDiscoverIgnoredTestCases(ClassContext classContext) {
        ClassDescriptor classDescriptor = classContext.getClassDescriptor();
        
        FBClassReader reader;
        JUnitTestVisitor ignoredTestCasesFinder = new JUnitTestVisitor();
        try {
            reader = Global.getAnalysisCache().getClassAnalysis(FBClassReader.class, classDescriptor);
        } catch (CheckedAnalysisException e) {
            AnalysisContext.logError("Error finding old @Ignore'd tests." + classDescriptor, e);
            return ignoredTestCasesFinder;
        }
        reader.accept(ignoredTestCasesFinder, 0);
        return ignoredTestCasesFinder;
    }

}
