package com.youdevise.fbplugins.junit;

public class TooOldIgnoreBug {

	private final String sourceFileName;
	private final int lineNumber;

	public TooOldIgnoreBug(String sourceFileName, int lineNumber) {
		this.sourceFileName = sourceFileName;
		this.lineNumber = lineNumber;
	}

	public int lineNumber() { return lineNumber; }

	public String sourceFileName() { return sourceFileName; }

    @Override
    public String toString() {
        return "TooOldIgnoreBug [@ line " + lineNumber + " of " + sourceFileName + "]";
    }

	
	
	
}
