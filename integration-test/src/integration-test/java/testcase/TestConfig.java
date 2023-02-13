package testcase;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectDirectories;
import org.junit.platform.suite.api.Suite;
import org.springframework.context.annotation.ComponentScan;
import testcase.cucumber.steps.data.TestDataComponent;

@Suite
@IncludeEngines("cucumber")
@ComponentScan(basePackageClasses = TestDataComponent.class)
@SelectDirectories("src/integration-test/resources/features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "testcase.cucumber.steps")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty, json:build/cucumber.json, rerun:target/rerun.txt")
public class TestConfig {

}
