package com.youdevise.fbplugins.junit;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;

import com.youdevise.fbplugins.junit.impl.SvnAgeOfIgnoreChecker;

public class SvnAgeOfIgnoreCheckerTest {

	private VersionControlledSourceFileFinder vcsFileFinder;
    private CommittedCodeDetailsFetcher committedCodeDetailsFetcher;
    private SvnAgeOfIgnoreChecker ageFinder;
    
    private void mockSvnQueryToReturn(List<LineOfCommittedCode> linesOfCode) {
        vcsFileFinder = mock(VersionControlledSourceFileFinder.class);
        committedCodeDetailsFetcher = mock(CommittedCodeDetailsFetcher.class);
        when(vcsFileFinder.location("local file path")).thenReturn("svn file path");
        when(committedCodeDetailsFetcher.logHistoryOfFile("svn file path")).thenReturn(linesOfCode);
        
        DateTime tooOldThresholdDate = new DateTime().minusDays(3); 

        ageFinder = new SvnAgeOfIgnoreChecker(vcsFileFinder, committedCodeDetailsFetcher, tooOldThresholdDate);
    }
    

    @Test public void
	returnsATooOldIgnoreBugForTheMethodGivenInIgnoredTestDetails() {
		DateTime dateOfIgnore = new DateTime().minusDays(4);
		List<LineOfCommittedCode> linesOfCode = asList(new LineOfCommittedCode(new DateTime(), 0, "", "first line in file", 0),
														     new LineOfCommittedCode(dateOfIgnore, 0, "", "@Ignore", 1),
															 new LineOfCommittedCode(new DateTime(), 0, "", "public void myIgnoredTest() {", 2),
															 new LineOfCommittedCode(new DateTime(), 0, "", "	assertThat(...);", 3));
		mockSvnQueryToReturn(linesOfCode);
		
		List<IgnoredTestDetails> ignoredTests = asList(new IgnoredTestDetails(4, "myIgnoredTest", "JavaSource.java"));
        List<TooOldIgnoreBug> ignoredForTooLong = ageFinder.ignoredForTooLong("local file path", ignoredTests);
		
		assertThat(ignoredForTooLong.size(), is(1));
		
		TooOldIgnoreBug tooOldIgnoreBug = ignoredForTooLong.get(0);
		
		assertThat(tooOldIgnoreBug.lineNumber(), is(2));
		assertThat(tooOldIgnoreBug.sourceFileName(), is("JavaSource.java"));
	}
    
    @Test public void
    doesNotReturnATooOldIgnoreBugIfTheIgnoreWasCommittedAfterTheThreshold() {
        DateTime dateOfIgnore = new DateTime().minusDays(2);
        List<LineOfCommittedCode> linesOfCode = asList(new LineOfCommittedCode(new DateTime(), 0, "", "first line in file", 0),
                                                             new LineOfCommittedCode(dateOfIgnore, 0, "", "@Ignore", 1),
                                                             new LineOfCommittedCode(new DateTime(), 0, "", "public void myIgnoredTest() {", 2),
                                                             new LineOfCommittedCode(new DateTime(), 0, "", "   assertThat(...);", 3));
        mockSvnQueryToReturn(linesOfCode);
        
        List<IgnoredTestDetails> ignoredTests = asList(new IgnoredTestDetails(4, "myIgnoredTest", "JavaSource.java"));
        List<TooOldIgnoreBug> ignoredForTooLong = ageFinder.ignoredForTooLong("local file path", ignoredTests);
        
        assertThat(ignoredForTooLong.size(), is(0));
    }
        

	@Test public void
	returnsAnEmptyListWhenGivenNoLinesOfCode() throws Exception {
        mockSvnQueryToReturn(Collections.<LineOfCommittedCode>emptyList());
        
        List<IgnoredTestDetails> ignoredTests = asList(new IgnoredTestDetails(4, "myIgnoredTest", "JavaSource.java"));
        List<TooOldIgnoreBug> ignoredForTooLong = ageFinder.ignoredForTooLong("local file path", ignoredTests);
        
        assertThat(ignoredForTooLong.isEmpty(), is(true));
        
    }
	
	
}
