package com.youdevise.fbplugins.junit;

import java.util.Date;

public class LineOfCommittedCode {

	public final Date dateOfCommit;
	public final long revision;
	public final String author;
	public final String lineContents;
	public final int lineNumber;

	public LineOfCommittedCode(Date dateOfCommit, long revision, String author, String lineContents, int lineNumber) {
		this.dateOfCommit = dateOfCommit;
		this.revision = revision;
		this.author = author;
		this.lineContents = lineContents;
		this.lineNumber = lineNumber;
	}
	
}