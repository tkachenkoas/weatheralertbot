package com.atstudio.volatileweatherbot.services.scheduled.dao

import com.atstudio.volatileweatherbot.repository.RepoConfig
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests

@ContextConfiguration(classes = RepoConfig)
@Import([ForecastRefreshDao])
class ForecastRefreshDaoImplTest extends AbstractTestNGSpringContextTests {

    private ForecastRefreshDao underTest

}