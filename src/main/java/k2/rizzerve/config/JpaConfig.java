package k2.rizzerve.config;

import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;

/**
 * Custom JPA configuration to resolve Hibernate/Spring compatibility issues
 */
@Configuration
public class JpaConfig {

    /**
     * Creates a custom EntityManagerFactory bean
     * This fixes the EntityManagerFactory interface conflict issue
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("k2.rizzerve.model");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
        properties.put("hibernate.show_sql", "false");
        em.setJpaPropertyMap(properties);
        
        // Fix for the EntityManagerFactory interface issue - THIS IS THE KEY FIX
        em.setEntityManagerFactoryInterface(EntityManagerFactory.class);
        
        return em;
    }
}
