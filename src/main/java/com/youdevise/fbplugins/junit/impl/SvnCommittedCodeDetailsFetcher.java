package com.youdevise.fbplugins.junit.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.ISVNAnnotateHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;

import com.youdevise.fbplugins.junit.CommittedCodeDetailsFetcher;
import com.youdevise.fbplugins.junit.LineOfCommittedCode;

public class SvnCommittedCodeDetailsFetcher implements CommittedCodeDetailsFetcher {

    public List<LineOfCommittedCode> logHistoryOfFile(String httpLocationOfVersionControlledSourceFile) {
        try {
            SVNURL fileURL = SVNURL.parseURIEncoded(httpLocationOfVersionControlledSourceFile);

            SVNLogClient logClient = SVNClientManager.newInstance().getLogClient();
            boolean ignoreMimeType = false;
            boolean includeMergedRevisions = false;

            AnnotationHandler handler = new AnnotationHandler();
            logClient.doAnnotate(fileURL, SVNRevision.UNDEFINED, SVNRevision.create(1), SVNRevision.HEAD, 
                                 ignoreMimeType, includeMergedRevisions, handler, null);
            
            return handler.lines();
            
        } catch (SVNException svne) {
            System.out.println(svne.getMessage());
            return Collections.emptyList();
        }
    }

    
    private static class AnnotationHandler implements ISVNAnnotateHandler {

        private List<LineOfCommittedCode> lines = new ArrayList<LineOfCommittedCode>();
        
        public void handleLine(Date date, long revision, String author, String line, Date mergedDate, long mergedRevision,
                String mergedAuthor, String mergedPath, int lineNumber) throws SVNException {
            lines.add(new LineOfCommittedCode(new DateTime(date), revision, author, line, lineNumber));
        }

        public List<LineOfCommittedCode> lines() { return Collections.unmodifiableList(lines); }
        
        public void handleEOF() { }

        public void handleLine(Date date, long revision, String author, String line) throws SVNException { }

        public boolean handleRevision(Date date, long revision, String author, File contents) throws SVNException { return false; }

    }
}
