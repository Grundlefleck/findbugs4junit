package com.youdevise.fbplugins.junit;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.youdevise.fbplugins.junit.impl.SvnAgeOfIgnoreFinder;

@SuppressWarnings("deprecation")
public class SvnAgeOfIgnoreFinderTest {

	@Test public void
	returnsATooOldIgnoreBugForTheMethodGivenInIgnoredTestDetails() {
		Date dateOfIgnore = new Date(); dateOfIgnore.setYear(2011);
		List<LineOfCommittedCode> linesOfCode = asList(new LineOfCommittedCode(new Date(), 0, "", "first line in file", 1),
														     new LineOfCommittedCode(dateOfIgnore, 0, "", "@Ignore", 2),
															 new LineOfCommittedCode(new Date(), 0, "", "public void myIgnoredTest() {", 3),
															 new LineOfCommittedCode(new Date(), 0, "", "	assertThat(...);", 4));

		VersionControlledSourceFileFinder vcsFileFinder = mock(VersionControlledSourceFileFinder.class);
		CommittedCodeDetailsFetcher committedCodeDetailsFetcher = mock(CommittedCodeDetailsFetcher.class);
		when(vcsFileFinder.location("local file path")).thenReturn("svn file path");
		when(committedCodeDetailsFetcher.logHistoryOfFile("svn file path")).thenReturn(linesOfCode);
		
		SvnAgeOfIgnoreFinder ageFinder = new SvnAgeOfIgnoreFinder(vcsFileFinder, committedCodeDetailsFetcher);
		
		List<TooOldIgnoreBug> ignoredForTooLong = ageFinder.ignoredForTooLong("local file path", asList(new IgnoredTestDetails(4, "myIgnoredTest", "JavaSource.java")));
		
		assertThat(ignoredForTooLong.size(), is(1));
		
		TooOldIgnoreBug tooOldIgnoreBug = ignoredForTooLong.get(0);
		
		assertThat(tooOldIgnoreBug.lineNumber(), is(2));
		assertThat(tooOldIgnoreBug.sourceFileName(), is("JavaSource.java"));
	}
	
	
}
