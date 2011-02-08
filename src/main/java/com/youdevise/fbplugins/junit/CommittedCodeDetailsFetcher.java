package com.youdevise.fbplugins.junit;

import java.util.List;


public interface CommittedCodeDetailsFetcher {

	List<LineOfCommittedCode> logHistoryOfFile(String httpLocationOfVersionControlledSourceFile);

}
