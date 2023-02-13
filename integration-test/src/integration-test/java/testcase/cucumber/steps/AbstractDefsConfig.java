package testcase.cucumber.steps;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import testcase.SpringTestConfig;

@ContextConfiguration(classes = {SpringTestConfig.class})
@DirtiesContext
public abstract class AbstractDefsConfig {

}