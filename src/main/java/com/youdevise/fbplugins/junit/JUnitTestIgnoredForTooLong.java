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

import org.apache.bcel.classfile.JavaClass;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.Priorities;
import edu.umd.cs.findbugs.asm.FBClassReader;
import edu.umd.cs.findbugs.ba.AnalysisContext;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.classfile.CheckedAnalysisException;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.classfile.Global;
import edu.umd.cs.findbugs.classfile.MethodDescriptor;

public class JUnitTestIgnoredForTooLong implements Detector {

	private static final int PRIORITY_TO_REPORT = Priorities.NORMAL_PRIORITY;
	
    static {
//    	DAVRepositoryFactory.setup();
        System.out.printf("Registered plugin detector [%s]%n", JUnitTestIgnoredForTooLong.class.getSimpleName());
    }

	
	private final BugReporter bugReporter;

	public JUnitTestIgnoredForTooLong(BugReporter bugReporter) {
		this.bugReporter = bugReporter;
	}

	@Override public void report() { }

    private void doReportBug(ClassContext classContext, String testCaseMethodName) {
        String slashedClassName = classContext.getClassDescriptor().getClassName();
		MethodDescriptor methodDescriptor = new MethodDescriptor(slashedClassName, testCaseMethodName, "()V", false);
        BugInstance bug = new BugInstance(this, "JUNIT_IGNORED_TOO_LONG", PRIORITY_TO_REPORT).addClassAndMethod(methodDescriptor);
        bugReporter.reportBug(bug);
	}
	
	@Override public void visitClassContext(ClassContext classContext) {
		JUnitTestVisitor visitor = analyseClassToDiscoverIgnoredTestCases(classContext);
		JavaClass javaClass = classContext.getJavaClass();
        String sourceFile = javaClass.getSourceFileName();
        String packageName = javaClass.getPackageName();
        
        
        for(IgnoredTestDetails ignoredTest: visitor.detailsOfIgnoredTests()) {
        	doReportBug(classContext, ignoredTest.methodName);
        }
        
        
//        List<Method> methods = classContext.getMethodsInCallOrder();
//        for (Method method : methods) {
//            if (testMethodFinder.isJUnitTestMethod(method)) {
//                try {
//                    analyzeMethod(classContext, method);
//                } catch (ClassNotFoundException e) {
//                    logError(classContext.getClassDescriptor(), e);
//                } catch (CheckedAnalysisException e) {
//                    logError(classContext.getClassDescriptor(), e);
//                }
//            }
//		}
	}
	
    private void logError(ClassDescriptor classDescriptor, Exception e) {
        System.err.printf("[Findbugs4JUnit plugin:] Error in detecting old @Ignores in %s%n%s", classDescriptor.getDottedClassName(), e.getMessage());
    }
    
    private void logError(String message) {
        System.err.printf("[Findbugs4JUnit plugin:] Error in detecting old @Ignores in %s%n%s", message);
    }
        

    
	private JUnitTestVisitor analyseClassToDiscoverIgnoredTestCases(ClassContext classContext) {
        ClassDescriptor classDescriptor = classContext.getClassDescriptor();
        
        FBClassReader reader;
        JUnitTestVisitor ignoredTestCasesFinder = new JUnitTestVisitor();
        try {
            reader = Global.getAnalysisCache().getClassAnalysis(FBClassReader.class, classDescriptor);
        } catch (CheckedAnalysisException e) {
            AnalysisContext.logError("Error finding old @Ignore'd tests." + classDescriptor, e);
            return ignoredTestCasesFinder;
        }
        reader.accept(ignoredTestCasesFinder, 0);
        return ignoredTestCasesFinder;
    }
    

//        try {
//            SourceFile findSourceFile = classContext.getAnalysisContext().getSourceFinder().findSourceFile(packageName, sourceFile);
//            String fullFileName = findSourceFile.getFullFileName();
//            fullFileName = "https://mutability-detector.googlecode.com/svn/trunk/MutabilityDetector/trunk/MutabilityDetector/src/test/java/org/mutabilitydetector/benchmarks/settermethod/SetterMethodCheckerTest.java";
//            logHistoryOfFile(fullFileName);
//            
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }
			
//    private void logHistoryOfFile(String fullFileName) {
//    	try {
//    		 SVNURL fileURL = SVNURL.parseURIEncoded(fullFileName);
// 
//			 //SVNLogClient is the class with which you can perform annotations 
//			 SVNLogClient logClient = SVNClientManager.newInstance().getLogClient();
//			 boolean ignoreMimeType = false;
//			 boolean includeMergedRevisions = false;
// 
//			 logClient.doAnnotate(fileURL, SVNRevision.UNDEFINED, SVNRevision.create(1), SVNRevision.HEAD, 
//					 ignoreMimeType, includeMergedRevisions, 
//					 new AnnotationHandler(includeMergedRevisions, false, logClient.getOptions()), 
//					 null);
//		 } catch (SVNException svne) {
// 		     System.out.println(svne.getMessage());
//		 }
//
//	}

	
//    private static class AnnotationHandler implements ISVNAnnotateHandler {
//
//		public AnnotationHandler(boolean includeMergedRevisions, boolean b, ISVNOptions options) {
//		}
//
//		@Override public void handleEOF() {
//		}
//
//		@Override public void handleLine(Date date, long revision, String author, String line) throws SVNException {
//			
//			
//		}
//
//		@Override public void handleLine(Date date, long revision, String author, String line, Date mergedDate,
//				long mergedRevision, String mergedAuthor, String mergedPath, int lineNumber) throws SVNException {
//
//		}
//
//		@Override public boolean handleRevision(Date date, long revision, String author, File contents) throws SVNException {
//			return false;
//		}
//    	
//    }
	
}