/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.youdevise.fbplugins.findbugs4junit.benchmarks;

import static junit.framework.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

public class WithIgnoredTestCases {

	@Ignore
	/**
	 * This is a 'live' @Ignore, which will appear in the bytecode.
	 */
	@Test public void myIgnoredTest() throws Exception {
		assertTrue(false);
	}
	
}
