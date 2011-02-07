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

package com.youdevise.fbplugins.junit;

import java.io.IOException;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.Priorities;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.classfile.MethodDescriptor;

public class JUnitTestIgnoredForTooLong implements Detector {

	private static final int PRIORITY_TO_REPORT = Priorities.NORMAL_PRIORITY;
	
	private final BugReporter bugReporter;
	private final AgeOfIgnoreFinder ageOfIgnoreFinder;
	private final FullSourcePathFinder sourcePathFinder;
	private final UnitTestVisitor unitTestVisitor;

	public JUnitTestIgnoredForTooLong(BugReporter bugReporter, AgeOfIgnoreFinder ageOfIgnoreFinder, FullSourcePathFinder sourcePathFinder, UnitTestVisitor visitor) {
		this.bugReporter = bugReporter;
		this.ageOfIgnoreFinder = ageOfIgnoreFinder;
		this.sourcePathFinder = sourcePathFinder;
		this.unitTestVisitor = visitor;
	}

	public void visitClassContext(ClassContext classContext) {
		
		if(!unitTestVisitor.classContainsIgnoredTests()) { return; }
		
		try {
			String fullSourcePath = sourcePathFinder.fullSourcePath(classContext);
			
			for(IgnoredTestDetails ignoredTest: unitTestVisitor.detailsOfIgnoredTests()) {
				if (ageOfIgnoreFinder.ignoredForTooLong(fullSourcePath, ignoredTest)) {
					doReportBug(classContext, ignoredTest.methodName);
				}
			}
		} catch (IOException e) {
			logError("Could not find source file location for " + classContext.getJavaClass().getFileName(), e);
			return;
		
		}
	}
	
    private void doReportBug(ClassContext classContext, String testCaseMethodName) {
        String slashedClassName = classContext.getClassDescriptor().getClassName();
		MethodDescriptor methodDescriptor = new MethodDescriptor(slashedClassName, testCaseMethodName, "()V", false);
        BugInstance bug = new BugInstance(this, "JUNIT_IGNORED_TOO_LONG", PRIORITY_TO_REPORT).addClassAndMethod(methodDescriptor);
        bugReporter.reportBug(bug);
	}
	
    private void logError(String message, Exception e) {
        System.err.printf("[Findbugs4JUnit plugin:] Error in detecting old @Ignores in %s%n%s%n", message, e);
    }

	@Override public void report() { }

    
	
}