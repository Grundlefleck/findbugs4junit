package com.youdevise.fbplugins.junit;

import java.util.List;

public interface UnitTestVisitor {

	boolean classContainsIgnoredTests();

	List<IgnoredTestDetails> detailsOfIgnoredTests();

}