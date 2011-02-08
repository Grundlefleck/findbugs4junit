package com.youdevise.fbplugins.junit;

import java.util.List;

public interface AgeOfIgnoreFinder {

	List<TooOldIgnoreBug> ignoredForTooLong(String fullFilePath, List<IgnoredTestDetails> detailsOfIgnoredTests);

}
