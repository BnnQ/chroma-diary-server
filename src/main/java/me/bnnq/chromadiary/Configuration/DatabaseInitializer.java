package me.bnnq.chromadiary.Configuration;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
@AllArgsConstructor
public class DatabaseInitializer
{
    private DataSource dataSource;

    @Bean
    public DataSourceInitializer dataSourceInitializer()
    {
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        resourceDatabasePopulator.addScript(new ClassPathResource("events.sql"));

        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(dataSource);

        dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("SET GLOBAL event_scheduler = ON;");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dataSourceInitializer;
    }
}
