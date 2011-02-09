package com.youdevise.fbplugins.junit;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Test;

import com.youdevise.fbplugins.junit.impl.SvnAgeOfIgnoreFinder;

@SuppressWarnings("deprecation")
public class SvnAgeOfIgnoreFinderTest {

	private VersionControlledSourceFileFinder vcsFileFinder;
    private CommittedCodeDetailsFetcher committedCodeDetailsFetcher;
    private SvnAgeOfIgnoreFinder ageFinder;
    
    private void mockSvnQueryToReturn(List<LineOfCommittedCode> linesOfCode) {
        vcsFileFinder = mock(VersionControlledSourceFileFinder.class);
        committedCodeDetailsFetcher = mock(CommittedCodeDetailsFetcher.class);
        when(vcsFileFinder.location("local file path")).thenReturn("svn file path");
        when(committedCodeDetailsFetcher.logHistoryOfFile("svn file path")).thenReturn(linesOfCode);
        
        ageFinder = new SvnAgeOfIgnoreFinder(vcsFileFinder, committedCodeDetailsFetcher);
    }
    

    @Test public void
	returnsATooOldIgnoreBugForTheMethodGivenInIgnoredTestDetails() {
		Date dateOfIgnore = new Date(); dateOfIgnore.setYear(2011);
		List<LineOfCommittedCode> linesOfCode = asList(new LineOfCommittedCode(new Date(), 0, "", "first line in file", 0),
														     new LineOfCommittedCode(dateOfIgnore, 0, "", "@Ignore", 1),
															 new LineOfCommittedCode(new Date(), 0, "", "public void myIgnoredTest() {", 2),
															 new LineOfCommittedCode(new Date(), 0, "", "	assertThat(...);", 3));
		mockSvnQueryToReturn(linesOfCode);
		
		List<IgnoredTestDetails> ignoredTests = asList(new IgnoredTestDetails(4, "myIgnoredTest", "JavaSource.java"));
        List<TooOldIgnoreBug> ignoredForTooLong = ageFinder.ignoredForTooLong("local file path", ignoredTests);
		
		assertThat(ignoredForTooLong.size(), is(1));
		
		TooOldIgnoreBug tooOldIgnoreBug = ignoredForTooLong.get(0);
		
		assertThat(tooOldIgnoreBug.lineNumber(), is(2));
		assertThat(tooOldIgnoreBug.sourceFileName(), is("JavaSource.java"));
	}

	@Test public void
	returnsAnEmptyListWhenGivenNoLinesOfCode() throws Exception {
        mockSvnQueryToReturn(Collections.<LineOfCommittedCode>emptyList());
        
        List<IgnoredTestDetails> ignoredTests = asList(new IgnoredTestDetails(4, "myIgnoredTest", "JavaSource.java"));
        List<TooOldIgnoreBug> ignoredForTooLong = ageFinder.ignoredForTooLong("local file path", ignoredTests);
        
        assertThat(ignoredForTooLong.isEmpty(), is(true));
        
    }
	
	
}
