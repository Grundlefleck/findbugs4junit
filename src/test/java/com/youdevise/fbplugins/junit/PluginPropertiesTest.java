package com.youdevise.fbplugins.junit;

import static com.youdevise.fbplugins.junit.PluginProperties.PROJECT_BASE_DIR_NAME_ERROR;
import static com.youdevise.fbplugins.junit.PluginProperties.TOO_OLD_THRESHOLD_ERROR;
import static com.youdevise.fbplugins.junit.PluginProperties.VERSION_CONTROL_PROJECT_ROOT_ERROR;
import static com.youdevise.fbplugins.junit.PluginProperties.fromArguments;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import com.youdevise.fbplugins.junit.PluginProperties;

public class PluginPropertiesTest {

	private PluginProperties properties;

	@Test public void
	reportsAnErrorWhenTheHostNameDoesNotBeginWithHttp() {
		properties = fromArguments("invalid host name", "", "");
		assertThat(properties.errors(), hasItem(VERSION_CONTROL_PROJECT_ROOT_ERROR));
	}
	
	@Test public void
	doesNotReportsAnErrorWhenProjectBaseDirIsNotTheSameAsLastSegmentOfVersionControlProjectRoot() {
		properties = fromArguments("http://src/svn/trunk/MyProject", "DifferentProject", "10");
		assertThat(properties.errors().iterator().hasNext(),is(false));
		assertThat(properties.areValid(), is(true));
	}
	
	@Test public void
	reportsAnErrorWhenEachMandatoryFieldIsMissing() {
		properties = fromArguments(null, null, null);
		Iterable<String> errors = properties.errors();
		
		assertThat(errors, hasItem(VERSION_CONTROL_PROJECT_ROOT_ERROR));
		assertThat(errors, hasItem(PROJECT_BASE_DIR_NAME_ERROR));
		assertThat(errors, hasItem(TOO_OLD_THRESHOLD_ERROR));
		assertThat(properties.areValid(), is(false));
	}
	
	@Test public void
	reportsAnErrorWhenEachMandatoryFieldIsBlank() {
		properties = fromArguments("", "", "");
		Iterable<String> errors = properties.errors();
		
		assertThat(errors, hasItem(VERSION_CONTROL_PROJECT_ROOT_ERROR));
		assertThat(errors, hasItem(PROJECT_BASE_DIR_NAME_ERROR));
		assertThat(errors, hasItem(TOO_OLD_THRESHOLD_ERROR));
		assertThat(properties.areValid(), is(false));
	}
	
	@Test public void
	propertiesContainMandatoryValues() {
		properties = PluginProperties.fromArguments("http://syntacticallvalidhostname/version/control/root/trunk/project", "project", "12");
		assertThat(properties.versionControlProjectRoot(), is("http://syntacticallvalidhostname/version/control/root/trunk/project"));
		assertThat(properties.projectBaseDirName(), is("project"));
		assertThat(properties.tooOldThreshold(), is(12));
		assertThat(properties.areValid(), is(true));
	}
	
	@Test public void
	noErrorsWhenAllFieldsAreValid() {
		properties = PluginProperties.fromArguments("http://syntacticallvalidhostname/version/control/root/trunk/project", "project", "4");
		assertThat(properties.errors().iterator().hasNext(), is(false));
		assertThat(properties.areValid(), is(true));
	}
	
//	@Test
//    public void reportsAnErrorWhenTheIgnoreDurationIsMissing() throws Exception {
//        properties = PluginProperties.fromArguments("http://src/svn/trunk/MyProject", "/home/me/project", null);
//    }
	
}
