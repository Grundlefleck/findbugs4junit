package com.youdevise.fbplugins.junit;

public interface AgeOfIgnoreFinder {

	boolean ignoredForTooLong(String fullFilePath, IgnoredTestDetails ignoredTest);

}
