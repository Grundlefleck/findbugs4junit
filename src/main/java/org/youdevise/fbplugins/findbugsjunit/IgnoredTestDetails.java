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

	@Override public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + ((lineNumber == null) ? 0 : lineNumber.hashCode());
		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
		return result;
	}

	@Override public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		IgnoredTestDetails other = (IgnoredTestDetails) obj;
		if (fileName == null) {
			if (other.fileName != null) {
				return false;
			}
		} else if (!fileName.equals(other.fileName)) {
			return false;
		}
		if (lineNumber == null) {
			if (other.lineNumber != null) {
				return false;
			}
		} else if (!lineNumber.equals(other.lineNumber)) {
			return false;
		}
		if (methodName == null) {
			if (other.methodName != null) {
				return false;
			}
		} else if (!methodName.equals(other.methodName)) {
			return false;
		}
		return true;
	}

	
}
