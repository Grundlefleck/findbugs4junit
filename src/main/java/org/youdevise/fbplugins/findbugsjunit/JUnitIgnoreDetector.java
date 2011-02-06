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

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.EmptyVisitor;

public class JUnitIgnoreDetector extends EmptyVisitor {
	
	private boolean classContainsIgnore = false;

	public boolean classContainsIgnoredTests() {
		return classContainsIgnore;
	}

	@Override public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
	}

	@Override public void visitAttribute(Attribute attr) { }

	@Override public void visitEnd() { }

	@Override public void visitInnerClass(String name, String outerName, String innerName, int access) { }

	@Override public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		return new JUnitTestMethodVisitor();
	}

	@Override public void visitOuterClass(String owner, String name, String desc) { }

	@Override public void visitSource(String source, String debug) { }
	
	
	private class JUnitTestMethodVisitor extends EmptyVisitor {

		
		
		
		@Override public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			System.out.println(desc + visible);
			if("Lorg/junit/Ignore;".equals(desc)) {
				classContainsIgnore = true;
			}
			return null;
		}

	}
}
