/*
 * FindBugs4JUnit. Copyright (c) 2011 youDevise, Ltd.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
*/
package com.youdevise.fbplugins.junit.impl;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.EmptyVisitor;

import com.youdevise.fbplugins.junit.IgnoredTestDetails;
import com.youdevise.fbplugins.junit.UnitTestVisitor;

public class JUnitTestVisitor extends EmptyVisitor implements UnitTestVisitor {
	
	private boolean classContainsIgnore = false;
	private String sourceFileName;
	private ArrayList<IgnoredTestDetails> ignoredTests = new ArrayList<IgnoredTestDetails>();

	public boolean classContainsIgnoredTests() {
		return classContainsIgnore;
	}

	@Override public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
	}

	@Override public void visitAttribute(Attribute attr) { }

	@Override public void visitEnd() { }

	@Override public void visitInnerClass(String name, String outerName, String innerName, int access) { }

	@Override public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		return new JUnitTestMethodVisitor(name);
	}

	@Override public void visitOuterClass(String owner, String name, String desc) { }

	@Override public void visitSource(String source, String debug) { 
		this.sourceFileName = source;
	}
	
	
	private class JUnitTestMethodVisitor extends EmptyVisitor {
		
		
		private final String methodName;
		private int currentLineNumber;
		private boolean methodAnnotatedWithIgnore;

		public JUnitTestMethodVisitor(String methodName) {
			this.methodName = methodName;
		}

		@Override public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			if("Lorg/junit/Ignore;".equals(desc)) {
				classContainsIgnore = true;
				methodAnnotatedWithIgnore = true;
			}
			return null;
		}

		@Override public void visitLineNumber(int lineNumber, Label label) {
			this.currentLineNumber = lineNumber;
			if(methodAnnotatedWithIgnore) {
				methodAnnotatedWithIgnore = false;
				ignoredTests.add(new IgnoredTestDetails(currentLineNumber, methodName, sourceFileName)); 
			}
		}
		
	}

	public List<IgnoredTestDetails> detailsOfIgnoredTests() {
		return ignoredTests;
	}
}
