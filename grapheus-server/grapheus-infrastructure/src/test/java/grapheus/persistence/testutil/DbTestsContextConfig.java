/**
 * 
 */
package grapheus.persistence.testutil;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import grapheus.TimeServiceImpl;
import grapheus.it.util.TestDbConfig;
import grapheus.persistence.DatabaseInitializer;
import grapheus.persistence.conpool.DBConnectionPoolImpl;


/**
 * @author black
 *
 */
@Configuration
@ComponentScan(
        basePackages={"grapheus"},
        useDefaultFilters=false,
        includeFilters={
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = TestDbConfig.class),
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = DBConnectionPoolImpl.class),
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = TimeServiceImpl.class),
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = DatabaseInitializer.class)
            })
public class DbTestsContextConfig {

}
