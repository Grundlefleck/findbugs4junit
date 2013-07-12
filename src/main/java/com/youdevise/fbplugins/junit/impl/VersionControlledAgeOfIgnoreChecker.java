package com.youdevise.fbplugins.junit.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.jgit.storage.file.FileRepository;
import org.joda.time.DateTime;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;

import com.youdevise.fbplugins.junit.AgeOfIgnoreFinder;
import com.youdevise.fbplugins.junit.CommittedCodeDetailsFetcher;
import com.youdevise.fbplugins.junit.IgnoredTestDetails;
import com.youdevise.fbplugins.junit.LineOfCommittedCode;
import com.youdevise.fbplugins.junit.PluginProperties;
import com.youdevise.fbplugins.junit.TooOldIgnoreBug;
import com.youdevise.fbplugins.junit.VersionControlledSourceFileFinder;

import static java.util.Arrays.asList;

public class VersionControlledAgeOfIgnoreChecker implements AgeOfIgnoreFinder {


    private final VersionControlledSourceFileFinder vcsFileFinder;
    private final CommittedCodeDetailsFetcher committedCodeDetailsFetcher;
    private final DateTime tooOldIgnoreThresholdDate;
    private final Collection<String> annotationsToLookFor;

    public VersionControlledAgeOfIgnoreChecker(VersionControlledSourceFileFinder vcsFileFinder,
                                 CommittedCodeDetailsFetcher committedCodeDetailsFetcher, 
                                 DateTime tooOldThresholdDate, 
                                 Collection<String> annotationsToLookFor) {
        this.vcsFileFinder = vcsFileFinder;
        this.committedCodeDetailsFetcher = committedCodeDetailsFetcher;
        this.tooOldIgnoreThresholdDate = tooOldThresholdDate;
        this.annotationsToLookFor = annotationsToLookFor;
    }


    public List<TooOldIgnoreBug> ignoredForTooLong(String fullFilePath, List<IgnoredTestDetails> ignoredTests) {
        String vcsFileLocation = vcsFileFinder.location(fullFilePath);
        List<LineOfCommittedCode> linesOfCode = committedCodeDetailsFetcher.logHistoryOfFile(vcsFileLocation);
        return getTooOldIgnoreBugs(ignoredTests, linesOfCode);
    }

    private List<TooOldIgnoreBug> getTooOldIgnoreBugs(List<IgnoredTestDetails> ignoredTests, List<LineOfCommittedCode> linesOfCode) {
        if(linesOfCode.isEmpty()) { return Collections.emptyList(); }
        
        return collectTooOldignores(ignoredTests, linesOfCode);
    }


    private List<TooOldIgnoreBug> collectTooOldignores(List<IgnoredTestDetails> ignoredTests, List<LineOfCommittedCode> linesOfCode) {
        List<TooOldIgnoreBug> tooOldIgnores = new ArrayList<TooOldIgnoreBug>();
        for(int i = 0; i < ignoredTests.size(); i++) {
            IgnoredTestDetails ignoredTest = ignoredTests.get(i);
            LineOfCommittedCode firstLineInIgnoredMethod = linesOfCode.get(ignoredTest.lineNumber - 1);
            
            List<TooOldIgnoreBug> findLineOfIgnoreAnnotation = findLineOfIgnoreAnnotation(linesOfCode, ignoredTest, firstLineInIgnoredMethod);
            tooOldIgnores.addAll(findLineOfIgnoreAnnotation);
        }
        
        return tooOldIgnores;
    }


    private List<TooOldIgnoreBug> findLineOfIgnoreAnnotation(List<LineOfCommittedCode> linesOfCode, IgnoredTestDetails ignoredTest, LineOfCommittedCode firstLineInIgnoredMethod) {
        List<TooOldIgnoreBug> tooOldIgnores = new ArrayList<TooOldIgnoreBug>();
        for(int j = linesOfCode.indexOf(firstLineInIgnoredMethod); j > 0; j--) {
            LineOfCommittedCode readingBack = linesOfCode.get(j);
            if(hasAnAnnotationToLookForWhichIsTooOld(readingBack)) {
                tooOldIgnores.add(newBug(ignoredTest, readingBack));
            }
        }
        
        return tooOldIgnores;
    }


    private TooOldIgnoreBug newBug(IgnoredTestDetails ignoredTest, LineOfCommittedCode readingBack) {
        return new TooOldIgnoreBug(ignoredTest.fileName, ignoredTest.methodName, readingBack.lineNumber + 1);
    }


    private boolean hasAnAnnotationToLookForWhichIsTooOld(LineOfCommittedCode readingBack) {
        return lineContainsAnnotationToLookFor(readingBack) && readingBack.dateOfCommit.isBefore(tooOldIgnoreThresholdDate);
    }


    private boolean lineContainsAnnotationToLookFor(LineOfCommittedCode readingBack) {
        for (String annotationName : annotationsToLookFor) {
            String simpleNameAnnotation = annotationName.substring(annotationName.lastIndexOf(".") + 1);
            String fullyQualifiedNameAnnotation = annotationName;
            if (readingBack.lineContents.contains("@".concat(simpleNameAnnotation))
                || readingBack.lineContents.contains("@".concat(fullyQualifiedNameAnnotation))) {
                return true;
            }
        }
        return false;
    }


    public static void main(String[] args) throws IOException {
        testSubversion();
        testGit();
    }

    private static void testSubversion() {
        DAVRepositoryFactory.setup();
        String fullFileName = "/home/mstoy/workspace/TIM/TIMWeb/src/integrationTest/java/test/integration/resources/research/ResearchResourcePerformanceIntegrationTest.java";
        Integer lineNumber = 413;
        String methodName = "a_request_for_research_on_a_stock_with_dividends_against_a_fixed_benchmark_gives_numbers_against_that_benchmark";
        
        String annotationsToLookFor = "net.ttsui.junit.rules.pending.PendingImplementation";
        PluginProperties properties = PluginProperties.fromArguments("http://srcctrl/opt/repo/projects/TIM/trunk/TIMWeb", 
                                                                     "/home/mstoy/workspace/TIM/TIMWeb/",
                                                                     "14",
                                                                     annotationsToLookFor);
        
        DateTime tooOldIgnoreThreshold = new DateTime().minusDays(14);
        
        VersionControlledAgeOfIgnoreChecker ignoreFinder = new VersionControlledAgeOfIgnoreChecker(new VersionControlledSourceFileFinder(properties),
                                                                       new SvnCommittedCodeDetailsFetcher(), 
                                                                       tooOldIgnoreThreshold,
                                                                       Arrays.asList(annotationsToLookFor));
        List<IgnoredTestDetails> ignoredTest = asList(new IgnoredTestDetails(lineNumber, methodName, fullFileName));
        List<TooOldIgnoreBug> ignoredForTooLong = ignoreFinder.ignoredForTooLong(fullFileName, ignoredTest);
        
        for (TooOldIgnoreBug tooOldIgnoreBug : ignoredForTooLong) {
            System.out.println(tooOldIgnoreBug);
        }
    }

    private static void testGit() throws IOException {
        String projectDir = "/home/mstoy/workspace/findbugs4junit";
        String relativeFileName = "src/test/benchmarks/com/youdevise/fbplugins/junit/benchmarks/OneIgnoredTestCase.java";
        Integer lineNumber = 38;
        String methodName = "myIgnoredTest";
        String annotationsToLookFor = "org.junit.Ignore";
        int tooOldThreshold = 0;
        String fullFileName = projectDir + "/" + relativeFileName;

        VersionControlledAgeOfIgnoreChecker ignoreFinder = new VersionControlledAgeOfIgnoreChecker(
                new VersionControlledSourceFileFinder(PluginProperties.fromArguments("", projectDir, String.valueOf(tooOldThreshold), annotationsToLookFor)),
                new GitCommittedCodeDetailsFetcher(new FileRepository(projectDir + "/.git")),
                new DateTime().minusDays(tooOldThreshold),
                Arrays.asList(annotationsToLookFor));
        List<IgnoredTestDetails> ignoredTest = asList(new IgnoredTestDetails(lineNumber, methodName, fullFileName));

        List<TooOldIgnoreBug> ignoredForTooLong = ignoreFinder.ignoredForTooLong(fullFileName, ignoredTest);
        for (TooOldIgnoreBug tooOldIgnoreBug : ignoredForTooLong) {
            System.out.println(tooOldIgnoreBug);
        }
    }
}
