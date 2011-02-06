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

public class IgnoredTestDetails {

	public final Integer lineNumber;
	public final String methodName;
	public final String fileName;
	
	public IgnoredTestDetails(Integer lineNumber, String methodName, String fileName) {
		this.lineNumber = lineNumber;
		this.methodName = methodName;
		this.fileName = fileName;
	}

}
