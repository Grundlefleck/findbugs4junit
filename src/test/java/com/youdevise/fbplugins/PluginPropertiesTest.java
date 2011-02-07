package com.youdevise.fbplugins;

import static com.youdevise.fbplugins.junit.PluginProperties.PROJECT_BASE_DIR_NAME_ERROR;
import static com.youdevise.fbplugins.junit.PluginProperties.VERSION_CONTROL_HTTP_HOST_ERROR;
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
		assertThat(properties.errors(), hasItem(VERSION_CONTROL_HTTP_HOST_ERROR));
	}
	
	@Test public void
	reportsAnErrorWhenProjectBaseDirIsNotTheSameAsLastSegmentOfVersionControlProjectRoot() {
		properties = fromArguments("", "/svn/trunk/MyProject", "DifferentProject");
		assertThat(properties.errors(), hasItem(PROJECT_BASE_DIR_NAME_ERROR));
	}
	
	@Test public void
	reportsAnErrorWhenEachMandatoryFieldIsMissing() {
		properties = fromArguments(null, null, null);
		Iterable<String> errors = properties.errors();
		
		assertThat(errors, hasItem(VERSION_CONTROL_HTTP_HOST_ERROR));
		assertThat(errors, hasItem(VERSION_CONTROL_PROJECT_ROOT_ERROR));
		assertThat(errors, hasItem(PROJECT_BASE_DIR_NAME_ERROR));
	}
	
	@Test public void
	reportsAnErrorWhenEachMandatoryFieldIsBlank() {
		properties = fromArguments("", "", "");
		Iterable<String> errors = properties.errors();
		
		assertThat(errors, hasItem(VERSION_CONTROL_HTTP_HOST_ERROR));
		assertThat(errors, hasItem(VERSION_CONTROL_PROJECT_ROOT_ERROR));
		assertThat(errors, hasItem(PROJECT_BASE_DIR_NAME_ERROR));
	}
	
	@Test public void
	propertiesContainMandatoryValues() {
		properties = PluginProperties.fromArguments("http://syntacticallvalidhostname", "/version/control/root/trunk/project", "project");
		assertThat(properties.versionControlHttpHost(), is("http://syntacticallvalidhostname"));
		assertThat(properties.versionControlProjectRoot(), is("/version/control/root/trunk/project"));
		assertThat(properties.projectBaseDirName(), is("project"));
	}
	
	@Test public void
	noErrorsWhenAllFieldsAreValid() {
		properties = PluginProperties.fromArguments("http://syntacticallvalidhostname", "/version/control/root/trunk/project", "project");
		assertThat(properties.errors().iterator().hasNext(), is(false));
	}
	
}
