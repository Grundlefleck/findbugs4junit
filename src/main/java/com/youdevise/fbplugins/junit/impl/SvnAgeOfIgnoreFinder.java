package com.youdevise.fbplugins.junit.impl;

import static java.util.Arrays.asList;

import java.util.ArrayList;
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
	private final CommittedCodeDetailsFetcher committedCodeDetailsFetcher;

    public SvnAgeOfIgnoreFinder(VersionControlledSourceFileFinder vcsFileFinder, CommittedCodeDetailsFetcher committedCodeDetailsFetcher) {
        this.vcsFileFinder = vcsFileFinder;
		this.committedCodeDetailsFetcher = committedCodeDetailsFetcher;
    }

    @Override public List<TooOldIgnoreBug> ignoredForTooLong(String fullFilePath, List<IgnoredTestDetails> ignoredTests) {
        
        String vcsFileLocation = vcsFileFinder.location(fullFilePath);
        
        List<LineOfCommittedCode> linesOfCode = committedCodeDetailsFetcher.logHistoryOfFile(vcsFileLocation);
        
		return getTooOldIgnoreBugs(ignoredTests, linesOfCode);
	}

    private List<TooOldIgnoreBug> getTooOldIgnoreBugs(List<IgnoredTestDetails> ignoredTests, List<LineOfCommittedCode> linesOfCode) {
    	List<TooOldIgnoreBug> tooOldIgnores = new ArrayList<TooOldIgnoreBug>();
    	for(int i = 0; i < ignoredTests.size(); i++) {
    		IgnoredTestDetails ignoredTest = ignoredTests.get(i);
    		LineOfCommittedCode firstLineInIgnoredMethod = linesOfCode.get(ignoredTest.lineNumber - 1);
    		
    		for(int j = linesOfCode.indexOf(firstLineInIgnoredMethod); j > 0; j--) {
    			LineOfCommittedCode readingBack = linesOfCode.get(j);
    			if(readingBack.lineContents.contains("@Ignore")) {
    				tooOldIgnores.add(new TooOldIgnoreBug(ignoredTest.fileName, readingBack.lineNumber));
    			}
    		}
    		
    	}
    	
    	return tooOldIgnores;
	}

    
    public static void main(String[] args) {
    	DAVRepositoryFactory.setup();
		String fullFileName = "/home/graham/dev/workspaces/projects/MutabilityDetector/src/test/java/org/mutabilitydetector/benchmarks/settermethod/SetterMethodCheckerTest.java";
		Integer lineNumber = 46;
		String methodName = "reassignmentOfStackConfinedObjectDoesNotFailCheck";
		
		PluginProperties properties = PluginProperties.fromArguments("http://mutability-detector.googlecode.com", 
																	 "/svn/trunk/MutabilityDetector/trunk/MutabilityDetector/", 
																	 "MutabilityDetector");
		
		SvnAgeOfIgnoreFinder ignoreFinder = new SvnAgeOfIgnoreFinder(new VersionControlledSourceFileFinder(properties), new SvnCommittedCodeDetailsFetcher());
		List<TooOldIgnoreBug> ignoredForTooLong = ignoreFinder.ignoredForTooLong(fullFileName, asList(new IgnoredTestDetails(lineNumber, methodName, fullFileName)));
		
		for (TooOldIgnoreBug tooOldIgnoreBug : ignoredForTooLong) {
			System.out.println(tooOldIgnoreBug);
		}
		
	}
    
}
