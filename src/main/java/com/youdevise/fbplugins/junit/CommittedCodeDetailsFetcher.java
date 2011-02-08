package com.youdevise.fbplugins.junit;

import java.util.Collection;


public interface CommittedCodeDetailsFetcher {

	Collection<LineOfCommittedCode> logHistoryOfFile(String httpLocationOfVersionControlledSourceFile);

}
