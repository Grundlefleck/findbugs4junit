package com.youdevise.fbplugins.junit;

import java.io.IOException;

import org.eclipse.jgit.storage.file.FileRepository;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;

import com.youdevise.fbplugins.junit.impl.FBFullSourcePathFinder;
import com.youdevise.fbplugins.junit.impl.GitCommittedCodeDetailsFetcher;
import com.youdevise.fbplugins.junit.impl.JUnitTestVisitor;
import com.youdevise.fbplugins.junit.impl.SvnCommittedCodeDetailsFetcher;
import com.youdevise.fbplugins.junit.impl.VersionControlledAgeOfIgnoreChecker;

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
    private final AgeOfIgnoreFinder ageOfIgnoreFinder;
    
    private static PluginProperties properties;

    public ThisPluginDetector(BugReporter bugReporter) throws IOException {
        this.bugReporter = bugReporter;
        this.ageOfIgnoreFinder = createAgeOfIgnoreFinder();
    }
    
    public void report() { }

    public void visitClassContext(ClassContext classContext) {
        if(!properties.areValid()) { return; }
        
        createActualDetector(classContext).visitClassContext(classContext);
    }
    
    private JUnitTestIgnoredForTooLong createActualDetector(ClassContext classContext) {
        FullSourcePathFinder fullSourcePathFinder = new FBFullSourcePathFinder();
        UnitTestVisitor unitTestVisitor = analyseClassToDiscoverIgnoredTestCases(classContext);
        return new JUnitTestIgnoredForTooLong(this, bugReporter, ageOfIgnoreFinder, fullSourcePathFinder, unitTestVisitor);
    }

    private AgeOfIgnoreFinder createAgeOfIgnoreFinder() throws IOException {
        if (!properties.areValid()) { return null; }
        
        CommittedCodeDetailsFetcher codeDetailsFetcher = PluginProperties.isBlank(properties.versionControlProjectRoot())
                ? new GitCommittedCodeDetailsFetcher(new FileRepository(properties.projectBaseDirName() + "/.git"))
                : new SvnCommittedCodeDetailsFetcher();
        return new VersionControlledAgeOfIgnoreChecker(new VersionControlledSourceFileFinder(properties),
                                         codeDetailsFetcher,
                                         properties.tooOldThresholdDate(),
                                         properties.annotationsToLookFor());
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
