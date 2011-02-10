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

import org.apache.bcel.classfile.Method;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.Priorities;
import edu.umd.cs.findbugs.ba.ClassContext;

public class JUnitTestIgnoredForTooLong implements Detector {

	private static final int PRIORITY_TO_REPORT = Priorities.NORMAL_PRIORITY;
	
	private final BugReporter bugReporter;
	private final AgeOfIgnoreFinder ageOfIgnoreFinder;
	private final FullSourcePathFinder sourcePathFinder;
	private final UnitTestVisitor unitTestVisitor;

    private final Detector pluginToRegisterBugsWith;

	public JUnitTestIgnoredForTooLong(Detector pluginToRegisterBugsWith, BugReporter bugReporter, AgeOfIgnoreFinder ageOfIgnoreFinder, FullSourcePathFinder sourcePathFinder, UnitTestVisitor visitor) {
		this.pluginToRegisterBugsWith = pluginToRegisterBugsWith;
        this.bugReporter = bugReporter;
		this.ageOfIgnoreFinder = ageOfIgnoreFinder;
		this.sourcePathFinder = sourcePathFinder;
		this.unitTestVisitor = visitor;
	}

	public void visitClassContext(ClassContext classContext) {
		
		if(!unitTestVisitor.classContainsIgnoredTests()) { return; }
		try {
			String fullSourcePath = sourcePathFinder.fullSourcePath(classContext);
			
			for (TooOldIgnoreBug bug : ageOfIgnoreFinder.ignoredForTooLong(fullSourcePath, unitTestVisitor.detailsOfIgnoredTests())) {
				doReportBug(classContext, bug);
			}
		} catch (IOException e) {
			logError("Could not find source file location for " + classContext.getJavaClass().getFileName(), e);
			return;
		
		}
	}
	
    private void doReportBug(ClassContext classContext, TooOldIgnoreBug tooOldIgnore) {
        Method testMethod = getMethodFrom(classContext, tooOldIgnore);
        BugInstance bug = new BugInstance(pluginToRegisterBugsWith, "JUNIT_IGNORED_TOO_LONG", PRIORITY_TO_REPORT)
                                            .addClassAndMethod(classContext.getJavaClass(), testMethod);
        bugReporter.reportBug(bug);
	}
	
    private Method getMethodFrom(ClassContext classContext, TooOldIgnoreBug tooOldIgnore) {
        for (Method method : classContext.getMethodsInCallOrder()) {
            if(method.getName().equalsIgnoreCase(tooOldIgnore.methodName())) {
                return method;
            }
        }
        return null;
    }

    private void logError(String message, Exception e) {
        System.err.printf("[Findbugs4JUnit plugin:] Error in detecting old @Ignores in %s%n%s%n", message, e);
    }

	public void report() { }

    
	
}