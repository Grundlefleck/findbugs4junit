package com.youdevise.fbplugins.junit;

import static java.lang.Integer.parseInt;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PluginProperties {

	
	private static final String propertyPrefix = "";
	
	
	public static final String VERSION_CONTROL_PROJECT_ROOT = property("versionControlProjectRoot");
	public static final String PROJECT_BASE_DIR_NAME = property("projectBaseDirName");
	public static final String TOO_OLD_THRESHOLD = property("tooOldThreshold");
	
	public static final String VERSION_CONTROL_PROJECT_ROOT_ERROR = mandatory(VERSION_CONTROL_PROJECT_ROOT, ", and must begin with 'http://'", "http://srcserver/svn/trunk/MyProject");
	public static final String PROJECT_BASE_DIR_NAME_ERROR = mandatory(PROJECT_BASE_DIR_NAME, "", "/home/me/workspace/MyProject");
	public static final String TOO_OLD_THRESHOLD_ERROR = mandatory(TOO_OLD_THRESHOLD, ", an integer for days old an @Ignore has to be", "14");


	private final List<String> errors;
	private final String versionControlProjectRoot;
	private final String projectBaseDirName;
    private final int tooOldThreshold;


	private static String property(String propertyName) {
		return propertyPrefix + propertyName;
	}
	
	private static String mandatory(String propertyName, String extraRequirements, String example) {
		return propertyPrefix + propertyName + extraRequirements + " e.g. " + example;
	}
	
	public static PluginProperties fromSystemProperties() {
		String versionControlProjectRoot = System.getProperty(VERSION_CONTROL_PROJECT_ROOT);
		String projectBaseDirName = System.getProperty(PROJECT_BASE_DIR_NAME);
		String tooOldThreshold = "";
		return fromArguments(versionControlProjectRoot, projectBaseDirName, tooOldThreshold);
	}

	public static PluginProperties fromArguments(String versionControlProjectRoot, String projectBaseDirName, String tooOldThreshold) {
		List<String> errors = new ArrayList<String>();
		
		if(isBlank(versionControlProjectRoot) || !(versionControlProjectRoot.startsWith("http://"))) {
			errors.add(VERSION_CONTROL_PROJECT_ROOT_ERROR);
		} 
		
		if(isBlank(projectBaseDirName)) {
			errors.add(PROJECT_BASE_DIR_NAME_ERROR);
		}
		
		if(isBlank(tooOldThreshold) || !isNumber(tooOldThreshold)) {
			tooOldThreshold = "14";
			errors.add(TOO_OLD_THRESHOLD_ERROR);
		}
		
		return new PluginProperties(unmodifiableList(errors), versionControlProjectRoot, projectBaseDirName, parseInt(tooOldThreshold));
	}

	private static boolean isNumber(String tooOldThreshold) {
		try {
			Integer.parseInt(tooOldThreshold);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private static boolean isBlank(String property) {
		return property == null || property.isEmpty();
	}
	
	public PluginProperties(List<String> errors, String versionControlProjectRoot, String projectBaseDirName, int tooOldThreshold) {
		this.errors = errors;
		this.versionControlProjectRoot = versionControlProjectRoot;
		this.projectBaseDirName = projectBaseDirName;
        this.tooOldThreshold = tooOldThreshold;
	}


    public Iterable<String> errors() { return errors; }
	
	public Iterable<String> properties() {
		return Arrays.asList(VERSION_CONTROL_PROJECT_ROOT + "=" + versionControlProjectRoot,
						     PROJECT_BASE_DIR_NAME + "=" + projectBaseDirName);
	}

	public String versionControlProjectRoot() { return versionControlProjectRoot; }

	public String projectBaseDirName() { return projectBaseDirName; }

	public Integer tooOldThreshold() { return tooOldThreshold; }

	public boolean areValid() {
        return errors.isEmpty();
    }

}
