package com.atstudio.volatileweatherbot.repository

import org.springframework.boot.test.autoconfigure.data.jdbc.AutoConfigureDataJdbc
import org.springframework.context.annotation.PropertySource

@PropertySource("classpath:test.properties")
@AutoConfigureDataJdbc
class RepoConfig {
}
