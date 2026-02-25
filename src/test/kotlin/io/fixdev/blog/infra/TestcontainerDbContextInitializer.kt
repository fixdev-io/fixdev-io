package io.fixdev.blog.infra

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.support.TestPropertySourceUtils
import org.testcontainers.containers.PostgreSQLContainer

class TestcontainerDbContextInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    companion object {
        private val container: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:17")
            .withDatabaseName("fixdev_blog")
            .withUsername("fixdev")
            .withPassword("fixdev_test")
            .withTmpFs(mapOf("/var/lib/postgresql/data" to "rw"))
            .withReuse(true)

        init {
            container.start()
        }
    }

    override fun initialize(ctx: ConfigurableApplicationContext) {
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
            ctx,
            "spring.datasource.url=${container.jdbcUrl}",
            "spring.datasource.username=${container.username}",
            "spring.datasource.password=${container.password}",
            "spring.flyway.default-schema=blog"
        )
    }
}
