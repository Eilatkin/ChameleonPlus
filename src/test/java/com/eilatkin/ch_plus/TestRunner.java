package com.eilatkin.ch_plus;

import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.platform.suite.api.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@Suite
@SuiteDisplayName("my-product-ui-tests")
@IncludeEngines("cucumber")
@SelectDirectories("src/test/resources/features")
@CucumberContextConfiguration
@ContextConfiguration("classpath:spring.xml")
@TestExecutionListeners(inheritListeners = false, listeners = {DependencyInjectionTestExecutionListener.class})
@ExcludeTags("IGNORE")
public class TestRunner {
}
