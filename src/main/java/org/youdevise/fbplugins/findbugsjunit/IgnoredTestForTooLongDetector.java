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

import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.ba.ClassContext;

public class IgnoredTestForTooLongDetector implements Detector {

	@Override public void report() {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	@Override public void visitClassContext(ClassContext arg0) {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

}
