/*
 * FindBugs4JUnit. Copyright (c) 2011 youDevise, Ltd.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
*/
package com.youdevise.fbplugins.junit;

import static com.youdevise.fbplugins.tdd4fb.DetectorAssert.assertAllBugsReported;
import static com.youdevise.fbplugins.tdd4fb.DetectorAssert.assertBugReported;
import static com.youdevise.fbplugins.tdd4fb.DetectorAssert.assertNoBugsReported;
import static com.youdevise.fbplugins.tdd4fb.DetectorAssert.bugReporterForTesting;
import static com.youdevise.fbplugins.tdd4fb.DetectorAssert.ofType;
import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import com.youdevise.fbplugins.junit.benchmarks.ManyIgnoredOneActive;
import com.youdevise.fbplugins.junit.benchmarks.NoIgnoredTests;
import com.youdevise.fbplugins.junit.benchmarks.OneIgnoredTestCase;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.ba.ClassContext;

public class JUnitTestIgnoredForTooLongTest {

    private BugReporter bugReporter;
    private Detector detector;
    private AgeOfIgnoreFinder ageOfIgnoreFinder;
    private FullSourcePathFinder sourcePathFinder;
    private UnitTestVisitor unitTestVisitor;
    private TooOldIgnoreBug ignoreBug = new TooOldIgnoreBug("SomeSourceFile.java", "myIgnoredTest", 27);
    private Detector detectorToRegisterBugsAs;

    @Before public void setUp() {
        bugReporter = bugReporterForTesting();
        ageOfIgnoreFinder = mock(AgeOfIgnoreFinder.class);
        sourcePathFinder = mock(FullSourcePathFinder.class);
        unitTestVisitor = mock(UnitTestVisitor.class);
        detectorToRegisterBugsAs = mock(Detector.class);
    }

    private void constructDetector() {
        detector = new JUnitTestIgnoredForTooLong(detectorToRegisterBugsAs, bugReporter, ageOfIgnoreFinder, sourcePathFinder, unitTestVisitor);
    }
    
    @Test public void
    doesNotReportBugWhenClassHasNoIgnoredTestCases() throws Exception {
        when(unitTestVisitor.classContainsIgnoredTests()).thenReturn(false);
        constructDetector();
        assertNoBugsReported(NoIgnoredTests.class, detector, bugReporter); 
    }
    
    @Test public void
    reportsABugForAJUnitTestWithIgnoreInItWhichIsTooOld() throws Exception {
        String fileNameWithIgnore = "OneIgnoredTestCase.java";
        when(unitTestVisitor.classContainsIgnoredTests()).thenReturn(true);
        when(unitTestVisitor.detailsOfIgnoredTests()).thenReturn(Arrays.asList(new IgnoredTestDetails(20, "myIgnoredTest", fileNameWithIgnore)));
        when(sourcePathFinder.fullSourcePath(any(ClassContext.class))).thenReturn(fileNameWithIgnore);
        
        when(ageOfIgnoreFinder.ignoredForTooLong(contains(fileNameWithIgnore), anyListOf(IgnoredTestDetails.class)))
                    .thenReturn(oldIgnoreBugs(ignoreBug));
        
        constructDetector();
        
        assertBugReported(OneIgnoredTestCase.class, detector, bugReporter);
    }
    
    @SuppressWarnings("unchecked")
    @Test public void
    reportsABugForEachOldIgnoreInATestFile() throws Exception {
        String fileNameWithIgnore = "OneIgnoredTestCase.java";
        when(unitTestVisitor.classContainsIgnoredTests()).thenReturn(true);
        when(unitTestVisitor.detailsOfIgnoredTests()).thenReturn(asList(new IgnoredTestDetails(20, "myIgnoredTest", fileNameWithIgnore),
                                                                        new IgnoredTestDetails(55, "someOtherMethodName", fileNameWithIgnore)));
        when(sourcePathFinder.fullSourcePath(any(ClassContext.class))).thenReturn(fileNameWithIgnore);
        when(ageOfIgnoreFinder.ignoredForTooLong(contains(fileNameWithIgnore), anyListOf(IgnoredTestDetails.class)))
                                    .thenReturn(oldIgnoreBugs(ignoreBug, ignoreBug));
        
        constructDetector();
        
        assertAllBugsReported(ManyIgnoredOneActive.class, detector, bugReporter, 
                              ofType("JUNIT_IGNORED_TOO_LONG"), ofType("JUNIT_IGNORED_TOO_LONG"));
    }

    private List<TooOldIgnoreBug> oldIgnoreBugs(TooOldIgnoreBug... bugs) {
        return Arrays.<TooOldIgnoreBug>asList(bugs);
    }
    
    @Test public void
    doesNotReportIgnoresWhichAreNotTooOld() throws Exception {
        String fileNameWithIgnore = "OneIgnoredTestCase.java";
        mockIgnoredTestInFile(fileNameWithIgnore, "myIgnoredTest");
        when(ageOfIgnoreFinder.ignoredForTooLong(contains(fileNameWithIgnore), anyListOf(IgnoredTestDetails.class))).thenReturn(oldIgnoreBugs());
        
        constructDetector();
        
        assertNoBugsReported(ManyIgnoredOneActive.class, detector, bugReporter);
    }
    

    @Test public void
    onlyReportsBugsForIgnoresWhichAreTooOld() throws Exception {
        mockIgnoredTestInFile("OneIgnoredTestCase.java", "myIgnoredTest");
        mockIgnoredTestInFile("OneIgnoredTestCase.java", "mySecondIgnoredTest");
        
        when(ageOfIgnoreFinder.ignoredForTooLong(contains("OneIgnoredTestCase.java"), argThat(isIgnoredTestMethod("myIgnoredTest")))).thenReturn(oldIgnoreBugs());
        when(ageOfIgnoreFinder.ignoredForTooLong(contains("OneIgnoredTestCase.java"), argThat(isIgnoredTestMethod("mySecondIgnoredTest")))).thenReturn(oldIgnoreBugs(ignoreBug));
        
        constructDetector();
        
        assertBugReported(ManyIgnoredOneActive.class, detector, bugReporter);
    }
    
    private ArgumentMatcher<List<IgnoredTestDetails>> isIgnoredTestMethod(final String methodName) {
        return new ArgumentMatcher<List<IgnoredTestDetails>>() {
            @SuppressWarnings("unchecked") @Override public boolean matches(Object argument) {
                boolean foundMatch = false;
                for(IgnoredTestDetails testDetails: ((List<IgnoredTestDetails>) argument)) {
                    String methodNameGiven = testDetails.methodName;
                    foundMatch |= methodName.equals(methodNameGiven);
                }
                return foundMatch;
            }
        };
    }

    private void mockIgnoredTestInFile(String fileNameWithIgnore, String methodName) throws IOException {
        when(unitTestVisitor.classContainsIgnoredTests()).thenReturn(true);
        when(unitTestVisitor.detailsOfIgnoredTests()).thenReturn(asList(new IgnoredTestDetails(20, methodName, fileNameWithIgnore)));
        when(sourcePathFinder.fullSourcePath(any(ClassContext.class))).thenReturn(fileNameWithIgnore);
    }

}
