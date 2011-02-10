package com.youdevise.fbplugins.junit.impl;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;

import com.youdevise.fbplugins.junit.AgeOfIgnoreFinder;
import com.youdevise.fbplugins.junit.CommittedCodeDetailsFetcher;
import com.youdevise.fbplugins.junit.IgnoredTestDetails;
import com.youdevise.fbplugins.junit.LineOfCommittedCode;
import com.youdevise.fbplugins.junit.PluginProperties;
import com.youdevise.fbplugins.junit.TooOldIgnoreBug;
import com.youdevise.fbplugins.junit.VersionControlledSourceFileFinder;

public class SvnAgeOfIgnoreChecker implements AgeOfIgnoreFinder {


    private final VersionControlledSourceFileFinder vcsFileFinder;
	private final CommittedCodeDetailsFetcher committedCodeDetailsFetcher;
    private final DateTime tooOldIgnoreThresholdDate;

    public SvnAgeOfIgnoreChecker(VersionControlledSourceFileFinder vcsFileFinder, 
                                 CommittedCodeDetailsFetcher committedCodeDetailsFetcher,
                                 DateTime tooOldThresholdDate) {
        this.vcsFileFinder = vcsFileFinder;
		this.committedCodeDetailsFetcher = committedCodeDetailsFetcher;
        this.tooOldIgnoreThresholdDate = tooOldThresholdDate;
    }


    public List<TooOldIgnoreBug> ignoredForTooLong(String fullFilePath, List<IgnoredTestDetails> ignoredTests) {
        String vcsFileLocation = vcsFileFinder.location(fullFilePath);
        List<LineOfCommittedCode> linesOfCode = committedCodeDetailsFetcher.logHistoryOfFile(vcsFileLocation);
		return getTooOldIgnoreBugs(ignoredTests, linesOfCode);
	}

    private List<TooOldIgnoreBug> getTooOldIgnoreBugs(List<IgnoredTestDetails> ignoredTests, List<LineOfCommittedCode> linesOfCode) {
        if(linesOfCode.isEmpty()) { return Collections.emptyList(); }
        
    	List<TooOldIgnoreBug> tooOldIgnores = new ArrayList<TooOldIgnoreBug>();
    	for(int i = 0; i < ignoredTests.size(); i++) {
    		IgnoredTestDetails ignoredTest = ignoredTests.get(i);
    		LineOfCommittedCode firstLineInIgnoredMethod = linesOfCode.get(ignoredTest.lineNumber -1);
    		
    		for(int j = linesOfCode.indexOf(firstLineInIgnoredMethod); j > 0; j--) {
    			LineOfCommittedCode readingBack = linesOfCode.get(j);
    			if(readingBack.lineContents.contains("@Ignore")) {
    			    if(readingBack.dateOfCommit.isBefore(tooOldIgnoreThresholdDate)) {
    			        tooOldIgnores.add(new TooOldIgnoreBug(ignoredTest.fileName, 
    				                                      ignoredTest.methodName, 
    				                                      readingBack.lineNumber + 1));
    			    }
    				break;
    			}
    		}
    	}
    	
    	return tooOldIgnores;
	}

    
    public static void main(String[] args) {
    	DAVRepositoryFactory.setup();
		String fullFileName = "/home/gallan/dev/workspaces/latest/HIP/browsertest/com/youdevise/seleniumrc/tests/LoginAsAndRevertToAdmin.java";
		Integer lineNumber = 98;
		String methodName = "cannotLoginAsUserWithAnUnescapedBackslash";
		
		PluginProperties properties = PluginProperties.fromArguments("http://srcctrl/opt/repo/projects/HIP/trunk/", 
																	 "/home/gallan/dev/workspaces/latest/HIP/",
																	 "14");
		
		DateTime tooOldIgnoreThreshold = new DateTime().minusDays(14);
		
		SvnAgeOfIgnoreChecker ignoreFinder = new SvnAgeOfIgnoreChecker(new VersionControlledSourceFileFinder(properties),
		                                                               new SvnCommittedCodeDetailsFetcher(), 
		                                                               tooOldIgnoreThreshold);
		List<IgnoredTestDetails> ignoredTest = asList(new IgnoredTestDetails(lineNumber, methodName, fullFileName));
        List<TooOldIgnoreBug> ignoredForTooLong = ignoreFinder.ignoredForTooLong(fullFileName, ignoredTest);
		
		for (TooOldIgnoreBug tooOldIgnoreBug : ignoredForTooLong) {
			System.out.println(tooOldIgnoreBug);
		}
		
	}
    
}
