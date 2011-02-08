package com.youdevise.fbplugins.junit.impl;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.List;

import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;

import com.youdevise.fbplugins.junit.AgeOfIgnoreFinder;
import com.youdevise.fbplugins.junit.CommittedCodeDetailsFetcher;
import com.youdevise.fbplugins.junit.IgnoredTestDetails;
import com.youdevise.fbplugins.junit.LineOfCommittedCode;
import com.youdevise.fbplugins.junit.PluginProperties;
import com.youdevise.fbplugins.junit.TooOldIgnoreBug;
import com.youdevise.fbplugins.junit.VersionControlledSourceFileFinder;

public class SvnAgeOfIgnoreFinder implements AgeOfIgnoreFinder {


    private final VersionControlledSourceFileFinder vcsFileFinder;
	private CommittedCodeDetailsFetcher fetcher = new SvnCommittedCodeDetailsFetcher();

    public SvnAgeOfIgnoreFinder(VersionControlledSourceFileFinder vcsFileFinder) {
        this.vcsFileFinder = vcsFileFinder;
    }

    @Override public List<TooOldIgnoreBug> ignoredForTooLong(String fullFilePath, List<IgnoredTestDetails> ignoredTests) {
        
        String vcsFileLocation = vcsFileFinder.location(fullFilePath);
        
        Collection<LineOfCommittedCode> linesOfCode = fetcher.logHistoryOfFile(vcsFileLocation);
        
		return getTooOldIgnoreBugs(ignoredTests, linesOfCode);
	}

    private List<TooOldIgnoreBug> getTooOldIgnoreBugs(List<IgnoredTestDetails> ignoredTests,
			Collection<LineOfCommittedCode> linesOfCode) {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

    
    public static void main(String[] args) {
    	DAVRepositoryFactory.setup();
		String fullFileName = "/home/graham/dev/workspaces/projects/MutabilityDetector/src/test/java/org/mutabilitydetector/benchmarks/settermethod/SetterMethodCheckerTest.java";
		Integer lineNumber = 46;
		String methodName = "reassignmentOfStackConfinedObjectDoesNotFailCheck";
		
		PluginProperties properties = PluginProperties.fromArguments("http://mutability-detector.googlecode.com", 
																	 "/svn/trunk/MutabilityDetector/trunk/MutabilityDetector/", 
																	 "MutabilityDetector");
		
		SvnAgeOfIgnoreFinder ignoreFinder = new SvnAgeOfIgnoreFinder(new VersionControlledSourceFileFinder(properties));
		ignoreFinder.ignoredForTooLong(fullFileName, asList(new IgnoredTestDetails(lineNumber, methodName, fullFileName)));
		
	}
    
}
