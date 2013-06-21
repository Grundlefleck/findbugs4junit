package com.youdevise.fbplugins.junit;

import static java.lang.Integer.parseInt;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;

public class PluginProperties {
	private static final String propertyPrefix = "";
	
	
	public static final String VERSION_CONTROL_PROJECT_ROOT = property("versionControlProjectRoot");
	public static final String PROJECT_BASE_DIR_NAME = property("projectBaseDirName");
	public static final String TOO_OLD_THRESHOLD = property("tooOldThreshold");
	public static final String ANNOTATIONS_TO_LOOK_FOR = property("annotationsToLookFor");
	
	public static final String VERSION_CONTROL_PROJECT_ROOT_ERROR = mandatory(VERSION_CONTROL_PROJECT_ROOT, ", and must begin with 'http://'", "http://srcserver/svn/trunk/MyProject");
	public static final String PROJECT_BASE_DIR_NAME_ERROR = mandatory(PROJECT_BASE_DIR_NAME, "", "/home/me/workspace/MyProject");
	public static final String TOO_OLD_THRESHOLD_ERROR = mandatory(TOO_OLD_THRESHOLD, ", an integer for days old an @Ignore has to be", "14");


	private final List<String> errors;
	private final String versionControlProjectRoot;
	private final String projectBaseDirName;
    private final int tooOldThreshold;


    private final Collection<String> annotationsToLookFor;


	private static String property(String propertyName) {
		return propertyPrefix + propertyName;
	}
	
	private static String mandatory(String propertyName, String extraRequirements, String example) {
		return propertyPrefix + propertyName + extraRequirements + " e.g. " + example;
	}
	
	public static PluginProperties fromSystemProperties() {
		String versionControlProjectRoot = System.getProperty(VERSION_CONTROL_PROJECT_ROOT);
		String projectBaseDirName = System.getProperty(PROJECT_BASE_DIR_NAME);
		String tooOldThreshold = System.getProperty(TOO_OLD_THRESHOLD);
		String annotationsToLookFor = System.getProperty(ANNOTATIONS_TO_LOOK_FOR);
		return fromArguments(versionControlProjectRoot, projectBaseDirName, tooOldThreshold, annotationsToLookFor);
	}

	public static PluginProperties fromArguments(String versionControlProjectRoot, String projectBaseDirName, String tooOldThreshold, String annotationsToLookFor) {
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
		
		Collection<String> annotations = new ArrayList<String>();
		if(!isBlank(annotationsToLookFor)) {
		    for (String otherAnnotation : annotationsToLookFor.split(":")) {
		        annotations.add(otherAnnotation);
		    }
		}
		annotations.add("org.junit.Ignore");
		
		return new PluginProperties(unmodifiableList(errors), 
		                            versionControlProjectRoot, 
		                            projectBaseDirName, 
		                            parseInt(tooOldThreshold),
		                            annotations);
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
	
	private PluginProperties(List<String> errors, String versionControlProjectRoot, String projectBaseDirName, int tooOldThreshold, Collection<String> annotationsToLookFor) {
		this.errors = errors;
		this.versionControlProjectRoot = versionControlProjectRoot;
		this.projectBaseDirName = projectBaseDirName;
        this.tooOldThreshold = tooOldThreshold;
        this.annotationsToLookFor = Collections.unmodifiableCollection(annotationsToLookFor);
	}


    public Iterable<String> errors() { return errors; }
	
	public Iterable<String> properties() {
		return Arrays.asList(VERSION_CONTROL_PROJECT_ROOT + "=" + versionControlProjectRoot,
						     PROJECT_BASE_DIR_NAME + "=" + projectBaseDirName,
						     TOO_OLD_THRESHOLD + "=" + tooOldThreshold,
						     ANNOTATIONS_TO_LOOK_FOR + "=" + annotationsToLookFor);
	}

	public String versionControlProjectRoot() { return versionControlProjectRoot; }
	public String projectBaseDirName() { return projectBaseDirName; }
	public Integer tooOldThreshold() { return tooOldThreshold; }

	public boolean areValid() {
        return errors.isEmpty();
    }

    public DateTime tooOldThresholdDate() {
        return new DateTime().minusDays(tooOldThreshold);
    }

    public Collection<String> annotationsToLookFor() {
        return annotationsToLookFor;
    }

}
