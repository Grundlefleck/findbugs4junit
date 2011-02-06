/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.youdevise.fbplugins.findbugsjunit;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.youdevise.fbplugins.findbugs4junit.benchmarks.WithIgnoredTestCases;

public class JUnitIgnoreVisitorTest {

	
	@Test public void
	hasFoundIgnore() throws Exception {
		Class<?> toVisit = WithIgnoredTestCases.class;
		JUnitIgnoreDetector visitor = new JUnitIgnoreDetector();
		ClassReader cr;
		try {
			cr = new ClassReader(toVisit.getName());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		cr.accept(visitor, 0);
		
		
		assertTrue("Should have found @Ignore'd test.", visitor.classContainsIgnoredTests());
		
	}
	
}
