package com.youdevise.fbplugins.junit.impl;

import java.io.File;
import java.util.Date;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.ISVNAnnotateHandler;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;

import com.youdevise.fbplugins.junit.AgeOfIgnoreFinder;
import com.youdevise.fbplugins.junit.IgnoredTestDetails;
import com.youdevise.fbplugins.junit.VersionControlledSourceFileFinder;

public class SvnAgeOfIgnoreFinder implements AgeOfIgnoreFinder {


    private final VersionControlledSourceFileFinder vcsFileFinder;

    public SvnAgeOfIgnoreFinder(VersionControlledSourceFileFinder vcsFileFinder) {
        this.vcsFileFinder = vcsFileFinder;
    }

    @Override public boolean ignoredForTooLong(String fullFilePath, IgnoredTestDetails ignoredTest) {
        
        String vcsFileLocation = vcsFileFinder.location(fullFilePath);
        logHistoryOfFile(vcsFileLocation);
		return false;
	}

    private void logHistoryOfFile(String fullFileName) {
        try {
            SVNURL fileURL = SVNURL.parseURIEncoded(fullFileName);

            // SVNLogClient is the class with which you can perform annotations
            SVNLogClient logClient = SVNClientManager.newInstance().getLogClient();
            boolean ignoreMimeType = false;
            boolean includeMergedRevisions = false;

            logClient.doAnnotate(fileURL, SVNRevision.UNDEFINED, SVNRevision.create(1), SVNRevision.HEAD, ignoreMimeType,
                    includeMergedRevisions, new AnnotationHandler(includeMergedRevisions, false, logClient.getOptions()), null);
        } catch (SVNException svne) {
            System.out.println(svne.getMessage());
        }

    }

    private static class AnnotationHandler implements ISVNAnnotateHandler {

        public AnnotationHandler(boolean includeMergedRevisions, boolean b, ISVNOptions options) {
        }

        @Override
        public void handleEOF() {
        }

        @Override
        public void handleLine(Date date, long revision, String author, String line) throws SVNException {
            System.out.printf("handleLine: %s, %s, %s, %s%n", date, revision, author, line);

        }

        @Override
        public void handleLine(Date date, long revision, String author, String line, Date mergedDate, long mergedRevision,
                String mergedAuthor, String mergedPath, int lineNumber) throws SVNException {
            System.out.printf("handleLine: %s, %s, %s, %s, %s, %s, %s, %s, %s%n", date, revision, author, line,
                    mergedDate, mergedRevision, mergedAuthor, mergedPath, lineNumber);
        }

        @Override
        public boolean handleRevision(Date date, long revision, String author, File contents) throws SVNException {
            System.out.printf("handleRevision: %s, %s, %s, %s%n", date, revision, author, contents);
            return false;
        }

    }

    
//    public static void main(String[] args) {
//		String fullFileName = args[0];
//		Integer lineNumber = Integer.valueOf(args[1]);
//		String methodName = args[2];
//		PluginProperties properties = PluginProperties.fromSystemProperties();
//		SvnAgeOfIgnoreFinder ignoreFinder = new SvnAgeOfIgnoreFinder(new VersionControlledSourceFileFinder(properties));
//		ignoreFinder.ignoredForTooLong(fullFileName, new IgnoredTestDetails(lineNumber, methodName, fullFileName));
//		
//	}
    
}
