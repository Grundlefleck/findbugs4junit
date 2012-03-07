package com.youdevise.fbplugins.junit;

import org.joda.time.DateTime;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;

import com.youdevise.fbplugins.junit.impl.FBFullSourcePathFinder;
import com.youdevise.fbplugins.junit.impl.JUnitTestVisitor;
import com.youdevise.fbplugins.junit.impl.SvnAgeOfIgnoreChecker;
import com.youdevise.fbplugins.junit.impl.SvnCommittedCodeDetailsFetcher;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.asm.FBClassReader;
import edu.umd.cs.findbugs.ba.AnalysisContext;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.classfile.CheckedAnalysisException;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.classfile.Global;

public class ThisPluginDetector implements Detector {
	
	private static class SetupChecker {
	    
	    private SetupChecker() { }
	    public static SetupChecker SINGLETON_INSTANCE = new SetupChecker();
	    
	    public PluginProperties initialiseProperties() {
	        PluginProperties properties = PluginProperties.fromSystemProperties();
	        for(String property: properties.properties()) {
	            System.out.printf("[%s] Using system property: %s%n", loggingLabel, property);
	        }
	        
	        if (!properties.areValid()) {
	            for(String error: properties.errors()) {
	                System.out.printf("[%s] Error in system properties: %s%n", loggingLabel, error);
	            }
	        } else {
	            DAVRepositoryFactory.setup();
	        }
	        
	        return properties;
	    }
	}
    
    private static final String loggingLabel = JUnitTestIgnoredForTooLong.class.getSimpleName();

	static {
        System.out.printf("Registered plugin detector [%s]%n", loggingLabel);
        properties = SetupChecker.SINGLETON_INSTANCE.initialiseProperties();
    }
	
	private final BugReporter bugReporter;
    private static PluginProperties properties;

	public ThisPluginDetector(BugReporter bugReporter) {
		this.bugReporter = bugReporter;
	}
	
    private JUnitTestIgnoredForTooLong createActualDetector(ClassContext classContext) {
        VersionControlledSourceFileFinder vcsFileFinder = new VersionControlledSourceFileFinder(properties);
        CommittedCodeDetailsFetcher committedCodeDetailsFetcher = new SvnCommittedCodeDetailsFetcher();
        DateTime tooOldThresholdDate = properties.tooOldThresholdDate();
		AgeOfIgnoreFinder ageOfIgnoreFinder = new SvnAgeOfIgnoreChecker(vcsFileFinder, 
		                                                                committedCodeDetailsFetcher, 
		                                                                tooOldThresholdDate, 
		                                                                properties.annotationsToLookFor());
		FullSourcePathFinder fullSourcePathFinder = new FBFullSourcePathFinder();
		UnitTestVisitor unitTestVisitor = analyseClassToDiscoverIgnoredTestCases(classContext);
		return new JUnitTestIgnoredForTooLong(this, bugReporter, ageOfIgnoreFinder, fullSourcePathFinder, unitTestVisitor);
	}

	public void report() { }

	public void visitClassContext(ClassContext classContext) {
	    if(!properties.areValid()) { return; }
	    
		createActualDetector(classContext).visitClassContext(classContext);
	}
	
	private JUnitTestVisitor analyseClassToDiscoverIgnoredTestCases(ClassContext classContext) {
        ClassDescriptor classDescriptor = classContext.getClassDescriptor();
        
        FBClassReader reader;
        JUnitTestVisitor ignoredTestCasesFinder = new JUnitTestVisitor(properties.annotationsToLookFor());
        try {
            reader = Global.getAnalysisCache().getClassAnalysis(FBClassReader.class, classDescriptor);
        } catch (CheckedAnalysisException e) {
            AnalysisContext.logError("Error finding old @Ignore'd tests.%n" + classDescriptor, e);
            return ignoredTestCasesFinder;
        }
        reader.accept(ignoredTestCasesFinder, 0);
        return ignoredTestCasesFinder;
    }

}
