package com.put.Chatterbox.DAO;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@ComponentScan
public class AppConfig {
  @Bean
  public JdbcTemplate jdbcTemplate() {
      DriverManagerDataSource ds = new DriverManagerDataSource();
      ds.setDriverClassName(com.mysql.jdbc.Driver.class.getName());
      ds.setUrl("jdbc:mysql://localhost:3306/word_category_data");
      ds.setUsername("root");
      ds.setPassword("");
      return new JdbcTemplate(ds);
  }
  
  
}