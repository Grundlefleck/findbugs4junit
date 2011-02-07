package com.youdevise.fbplugins.junit;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PluginProperties {

	
	private static final String propertyPrefix = "";
	
	
	public static final String VERSION_CONTROL_HTTP_HOST = property("versionControlHttpHost");
	public static final String VERSION_CONTROL_PROJECT_ROOT = property("versionControlProjectRoot");
	public static final String PROJECT_BASE_DIR_NAME = property("projectBaseDirName");

	
	public static final String VERSION_CONTROL_HTTP_HOST_ERROR = mandatory(VERSION_CONTROL_HTTP_HOST, ", and must begin with 'http'", "http://mysvnserver");
	public static final String VERSION_CONTROL_PROJECT_ROOT_ERROR = mandatory(VERSION_CONTROL_PROJECT_ROOT, ", and must begin with '/'", "/svn/trunk/MyProject");
	public static final String PROJECT_BASE_DIR_NAME_ERROR = mandatory(PROJECT_BASE_DIR_NAME, ", and must be the same as the last segment of the " + VERSION_CONTROL_PROJECT_ROOT, 
			"MyProject");

	private final List<String> errors;


	private final String versionControlHttpHost;


	private final String versionControlProjectRoot;


	private final String projectBaseDirName;


	private static String property(String propertyName) {
		return propertyPrefix + propertyName;
	}
	
	private static String mandatory(String propertyName, String extraRequirements, String example) {
		return propertyPrefix + propertyName + extraRequirements + " e.g. " + example;
	}
	
	public static PluginProperties fromSystemProperties() {
		String versionControlHttpHost = System.getProperty(VERSION_CONTROL_HTTP_HOST);
		String versionControlProjectRoot = System.getProperty(VERSION_CONTROL_PROJECT_ROOT);
		String projectBaseDirName = System.getProperty(PROJECT_BASE_DIR_NAME);
		return fromArguments(versionControlHttpHost, versionControlProjectRoot, projectBaseDirName);
	}

	public static PluginProperties fromArguments(String versionControlHttpHost, String versionControlProjectRoot, String projectBaseDirName) {
		List<String> errors = new ArrayList<String>();
		if(isBlank(versionControlHttpHost) || !(versionControlHttpHost.startsWith("http"))) {
			errors.add(VERSION_CONTROL_HTTP_HOST_ERROR);
		}
		
		if(isBlank(versionControlProjectRoot) || !(versionControlProjectRoot.startsWith("/"))) {
			errors.add(VERSION_CONTROL_PROJECT_ROOT_ERROR);
		} 
		
		String lastSegment = isBlank(versionControlProjectRoot) ? "" : lastSegmentOf(versionControlProjectRoot);
		if(isBlank(projectBaseDirName) || !(projectBaseDirName.equals(lastSegment))) {
			errors.add(PROJECT_BASE_DIR_NAME_ERROR);
		}
		
		return new PluginProperties(unmodifiableList(errors), versionControlHttpHost, versionControlProjectRoot, projectBaseDirName);
	}

	private static String lastSegmentOf(String path) {
		return path.substring(path.lastIndexOf("/") + 1);
	}

	private static boolean isBlank(String property) {
		return property == null || property.isEmpty();
	}
	
	public PluginProperties(List<String> errors, String versionControlHttpHost, String versionControlProjectRoot, String projectBaseDirName) {
		this.errors = errors;
		this.versionControlHttpHost = versionControlHttpHost;
		this.versionControlProjectRoot = versionControlProjectRoot;
		this.projectBaseDirName = projectBaseDirName;
	}


	public Iterable<String> errors() { return errors; }
	
	public Iterable<String> properties() {
		return Arrays.asList(VERSION_CONTROL_HTTP_HOST + "=" + versionControlHttpHost,
						     VERSION_CONTROL_PROJECT_ROOT + "=" + versionControlProjectRoot,
						     PROJECT_BASE_DIR_NAME + "=" + projectBaseDirName);
	}

	public String versionControlHttpHost() { return versionControlHttpHost; }

	public String versionControlProjectRoot() { return versionControlProjectRoot; }

	public String projectBaseDirName() { return projectBaseDirName; }

    public boolean areValid() {
        return errors.isEmpty();
    }
}
