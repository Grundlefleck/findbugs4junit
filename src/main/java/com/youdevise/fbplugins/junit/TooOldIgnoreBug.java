package com.youdevise.fbplugins.junit;

public class TooOldIgnoreBug {

	private final String sourceFileName;
	private final int lineNumber;
    private final String methodName;

	public TooOldIgnoreBug(String sourceFileName, String methodName, int lineNumber) {
		this.sourceFileName = sourceFileName;
        this.methodName = methodName;
		this.lineNumber = lineNumber;
	}

	public int lineNumber() { return lineNumber; }

	public String sourceFileName() { return sourceFileName; }

	public String methodName() { return methodName; }
	
    @Override
    public String toString() {
        return "TooOldIgnoreBug [@ line " + lineNumber + " of " + methodName + " in "+ sourceFileName + "]";
    }


	
	
	
}
