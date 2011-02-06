package com.youdevise.fbplugins.junit.impl;

import com.youdevise.fbplugins.junit.AgeOfIgnoreFinder;
import com.youdevise.fbplugins.junit.IgnoredTestDetails;

public class SvnAgeOfIgnoreFinder implements AgeOfIgnoreFinder {


	@Override public boolean ignoredForTooLong(String fullFilePath, IgnoredTestDetails ignoredTest) {
		return false;
	}

	// try {
	// SourceFile findSourceFile = classContext.getAnalysisContext().getSourceFinder().findSourceFile(packageName,
	// sourceFile);
	// String fullFileName = findSourceFile.getFullFileName();
	// fullFileName =
	// "https://mutability-detector.googlecode.com/svn/trunk/MutabilityDetector/trunk/MutabilityDetector/src/test/java/org/mutabilitydetector/benchmarks/settermethod/SetterMethodCheckerTest.java";
	// logHistoryOfFile(fullFileName);
	//  
	// } catch (IOException e1) {
	// e1.printStackTrace();
	// }

	// private void logHistoryOfFile(String fullFileName) {
	// try {
	// SVNURL fileURL = SVNURL.parseURIEncoded(fullFileName);
	//
	// //SVNLogClient is the class with which you can perform annotations
	// SVNLogClient logClient = SVNClientManager.newInstance().getLogClient();
	// boolean ignoreMimeType = false;
	// boolean includeMergedRevisions = false;
	//
	// logClient.doAnnotate(fileURL, SVNRevision.UNDEFINED, SVNRevision.create(1), SVNRevision.HEAD,
	// ignoreMimeType, includeMergedRevisions,
	// new AnnotationHandler(includeMergedRevisions, false, logClient.getOptions()),
	// null);
	// } catch (SVNException svne) {
	// System.out.println(svne.getMessage());
	// }
	//
	// }

	// private static class AnnotationHandler implements ISVNAnnotateHandler {
	//
	// public AnnotationHandler(boolean includeMergedRevisions, boolean b, ISVNOptions options) {
	// }
	//
	// @Override public void handleEOF() {
	// }
	//
	// @Override public void handleLine(Date date, long revision, String author, String line) throws SVNException {
	//	
	//	
	// }
	//
	// @Override public void handleLine(Date date, long revision, String author, String line, Date mergedDate,
	// long mergedRevision, String mergedAuthor, String mergedPath, int lineNumber) throws SVNException {
	//
	// }
	//
	// @Override public boolean handleRevision(Date date, long revision, String author, File contents) throws
	// SVNException {
	// return false;
	// }
	//
	// }

}
