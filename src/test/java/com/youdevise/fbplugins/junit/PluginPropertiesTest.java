package com.youdevise.fbplugins.junit;

import static com.youdevise.fbplugins.junit.PluginProperties.PROJECT_BASE_DIR_NAME_ERROR;
import static com.youdevise.fbplugins.junit.PluginProperties.TOO_OLD_THRESHOLD_ERROR;
import static com.youdevise.fbplugins.junit.PluginProperties.VERSION_CONTROL_PROJECT_ROOT_ERROR;
import static com.youdevise.fbplugins.junit.PluginProperties.fromArguments;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionContaining.hasItem;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.Test;

public class PluginPropertiesTest {

	private PluginProperties properties;
	private static final String ONLY_LOOK_FOR_IGNORE = "";

	@Test public void
	reportsAnErrorWhenTheHostNameDoesNotBeginWithHttp() {
		properties = fromArguments("invalid host name", "", "", ONLY_LOOK_FOR_IGNORE);
		assertThat(properties.errors(), hasItem(VERSION_CONTROL_PROJECT_ROOT_ERROR));
	}

	@Test public void
	doesNotReportsAnErrorWhenProjectBaseDirIsNotTheSameAsLastSegmentOfVersionControlProjectRoot() {
		properties = fromArguments("http://src/svn/trunk/MyProject", "DifferentProject", "10", ONLY_LOOK_FOR_IGNORE);
		assertThat(properties.errors().iterator().hasNext(),is(false));
		assertThat(properties.areValid(), is(true));
	}

	@Test public void
	reportsAnErrorWhenEachMandatoryFieldIsMissing() {
		properties = fromArguments(null, null, null, ONLY_LOOK_FOR_IGNORE);
		Iterable<String> errors = properties.errors();

		assertThat(errors, hasItem(VERSION_CONTROL_PROJECT_ROOT_ERROR));
		assertThat(errors, hasItem(PROJECT_BASE_DIR_NAME_ERROR));
		assertThat(errors, hasItem(TOO_OLD_THRESHOLD_ERROR));
		assertThat(properties.areValid(), is(false));
	}

	@Test public void
	reportsAnErrorWhenEachMandatoryFieldIsBlank() {
		properties = fromArguments("", "", "", ONLY_LOOK_FOR_IGNORE);
		Iterable<String> errors = properties.errors();

		assertThat(errors, hasItem(VERSION_CONTROL_PROJECT_ROOT_ERROR));
		assertThat(errors, hasItem(PROJECT_BASE_DIR_NAME_ERROR));
		assertThat(errors, hasItem(TOO_OLD_THRESHOLD_ERROR));
		assertThat(properties.areValid(), is(false));
	}

	@Test public void
	propertiesContainMandatoryValues() {
		properties = PluginProperties.fromArguments("http://syntacticallvalidhostname/version/control/root/trunk/project", "project", "12", ONLY_LOOK_FOR_IGNORE);
		assertThat(properties.versionControlProjectRoot(), is("http://syntacticallvalidhostname/version/control/root/trunk/project"));
		assertThat(properties.projectBaseDirName(), is("project"));
		assertThat(properties.tooOldThreshold(), is(12));
		assertThat(properties.areValid(), is(true));
	}

	@Test public void
	noErrorsWhenAllFieldsAreValid() {
		properties = PluginProperties.fromArguments("http://syntacticallvalidhostname/version/control/root/trunk/project", "project", "4", ONLY_LOOK_FOR_IGNORE);
		assertThat(properties.errors().iterator().hasNext(), is(false));
		assertThat(properties.areValid(), is(true));
	}

	@Test public void
	reportsAnErrorWhenTheIgnoreDurationIsMissing() throws Exception {
        properties = PluginProperties.fromArguments("http://src/svn/trunk/MyProject", "/home/me/project", null, ONLY_LOOK_FOR_IGNORE);
        assertThat(properties.errors(), hasItem(TOO_OLD_THRESHOLD_ERROR));
        assertThat(properties.areValid(), is(false));
    }

	@Test public void
	reportsAnErrorWhenTheIgnoreDurationIsBlank() throws Exception {
        properties = PluginProperties.fromArguments("http://src/svn/trunk/MyProject", "/home/me/project", "", ONLY_LOOK_FOR_IGNORE);
        assertThat(properties.errors(), hasItem(TOO_OLD_THRESHOLD_ERROR));
        assertThat(properties.areValid(), is(false));
    }

	@Test public void
	reportsAnErrorWhenTheIgnoreDurationIsNotAnInteger() throws Exception {
        properties = PluginProperties.fromArguments("http://src/svn/trunk/MyProject", "/home/me/project", "fourteen", ONLY_LOOK_FOR_IGNORE);
        assertThat(properties.errors(), hasItem(TOO_OLD_THRESHOLD_ERROR));
        assertThat(properties.areValid(), is(false));
    }

	@Test public void
    providesADateThatIsTheCurrentDateMinusTheNumberOfDaysSpecifiedByTooOldThreshold() throws Exception {
        properties = PluginProperties.fromArguments("http://src/svn/trunk/MyProject", "/home/me/project", "3", ONLY_LOOK_FOR_IGNORE);
        assertThat(properties.areValid(), is(true));

        DateTime propertiesDate = properties.tooOldThresholdDate();


        DateTime currentDate = new DateTime();
        assertThat(Days.daysBetween(propertiesDate, currentDate).getDays(), is(3));
    }

	@Test public void
	annotationsToLookForContainsOnlyJUnitIgnoreIfNoExtraAreSpecified() throws Exception {
	    properties = PluginProperties.fromArguments("http://src/svn/trunk/MyProject", "/home/me/project", "3", ONLY_LOOK_FOR_IGNORE);
        assertThat(properties.areValid(), is(true));
        assertThat(properties.annotationsToLookFor().size(), is(1));
        assertThat(properties.annotationsToLookFor(), hasItem("org.junit.Ignore"));
    }

	@Test public void
    annotationsToLookForContainsJUnitIgnoreAndSpecifiedAnnotations() throws Exception {
        properties = PluginProperties.fromArguments("http://src/svn/trunk/MyProject", "/home/me/project", "3", "org.my.FirstAnnotation:org.my.SecondAnnotation:");
        assertThat(properties.areValid(), is(true));
        assertThat(properties.annotationsToLookFor().size(), is(3));
        assertThat(properties.annotationsToLookFor(), hasItem("org.junit.Ignore"));
        assertThat(properties.annotationsToLookFor(), hasItem("org.my.FirstAnnotation"));
        assertThat(properties.annotationsToLookFor(), hasItem("org.my.SecondAnnotation"));
    }



}
