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

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	private List<IgnoredTestDetails> ignoredTests = new ArrayList<IgnoredTestDetails>();
    private Set<String> annotationsToScanFor;

	public JUnitTestVisitor(Collection<String> annotationsToScanFor) {
        this.annotationsToScanFor = unmodifiableSet(new HashSet<String>(annotationsToScanFor));
    }
	
	public static JUnitTestVisitor lookingForIgnoreOnly() {
	    return new JUnitTestVisitor(asList("org.junit.Ignore"));
	}
	
    public static JUnitTestVisitor lookingForIgnoreAnd(Collection<String> annotationsToScanFor) {
        Collection<String> includeIgnore = new HashSet<String>(annotationsToScanFor);
        includeIgnore.add("org.junit.Ignore");
        return new JUnitTestVisitor(includeIgnore);
    }

    public boolean classContainsIgnoredTests() {
		return classContainsIgnore;
	}

	@Override public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
	}

	@Override public void visitAttribute(Attribute attr) { }

	@Override public void visitEnd() { }

	@Override public void visitInnerClass(String name, String outerName, String innerName, int access) { }

	@Override public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		return new JUnitTestMethodVisitor(name, convertAnnotationsToInternalName());
	}

	private Set<String> convertAnnotationsToInternalName() {
	    Set<String> internalNames = new HashSet<String>(annotationsToScanFor.size());
	    for (String dottedAnnotationTypeName : annotationsToScanFor) {
	        String internalName = "L".concat(dottedAnnotationTypeName.replace(".", "/")).concat(";");
            internalNames.add(internalName);
        }
	    return internalNames;
    }

    @Override public void visitOuterClass(String owner, String name, String desc) { }

	@Override public void visitSource(String source, String debug) { 
		this.sourceFileName = source;
	}
	
	
	private class JUnitTestMethodVisitor extends EmptyVisitor {
		
		
		private final String methodName;
		private int currentLineNumber;
		private boolean methodAnnotatedWithIgnore;
        private final Set<String> annotationsToLookFor;

		public JUnitTestMethodVisitor(String methodName, Set<String> annotationsToLookFor) {
			this.methodName = methodName;
            this.annotationsToLookFor = annotationsToLookFor;
		}

		@Override public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			if(annotationsToLookFor.contains(desc)) {
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
