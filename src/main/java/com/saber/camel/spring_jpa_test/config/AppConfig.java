package com.saber.camel.spring_jpa_test.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.camel.component.jpa.JpaComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@Configuration
@EnableTransactionManagement
public class AppConfig {

    private EntityManagerFactory entityManagerFactory;
    private final Environment environment;

    public AppConfig(Environment environment) {
        this.environment = environment;
        if (getEnvironment().equals("prod")) {
            entityManagerFactory = Persistence.createEntityManagerFactory("camel-persistence2");
        } else {
            entityManagerFactory = Persistence.createEntityManagerFactory("camel-persistence");
        }
    }

    @Bean
    public JpaComponent jpaComponent() {
        JpaComponent jpaComponent = new JpaComponent();
        jpaComponent.setEntityManagerFactory(entityManagerFactory);
        jpaComponent.setTransactionManager(transactionManager());
        return jpaComponent;
    }


    @Bean
    public PlatformTransactionManager transactionManager()  {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    @Bean
    public ObjectMapper objectMapper(){
        ObjectMapper objectMapper= new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
        objectMapper.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES,false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE,false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    private String getEnvironment() {
        if (environment.getActiveProfiles().length > 0) {
            String env = environment.getActiveProfiles()[0];
            return env.trim().toLowerCase();
        }
        return "default";
    }

}