package com.youdevise.fbplugins.junit;

import org.joda.time.DateTime;

public class LineOfCommittedCode {

    public final DateTime dateOfCommit;
    public final long revision;
    public final String author;
    public final String lineContents;
    public final int lineNumber;

    public LineOfCommittedCode(DateTime dateOfIgnore, long revision, String author, String lineContents, int lineNumber) {
        this.dateOfCommit = dateOfIgnore;
        this.revision = revision;
        this.author = author;
        this.lineContents = lineContents;
        this.lineNumber = lineNumber;
    }

    @Override public String toString() {
        return "LineOfCommittedCode [r" + revision + " " + author + " " + dateOfCommit + " " + lineNumber + " " + lineContents + "]";
    }
    

}