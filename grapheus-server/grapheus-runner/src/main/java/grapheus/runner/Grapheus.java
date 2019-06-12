/**
 * 
 */
package grapheus.runner;

import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.grapheus.common.config.ConfigurationAccumulator;
import org.grapheus.common.config.HumanReadableConfigProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

import com.google.common.cache.CacheBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author black
 *
 */
@Configuration
@EnableCaching
@ComponentScan(basePackages = "grapheus")
@EnableAspectJAutoProxy
@PropertySource({ "/rx-default.properties", "file:///${grapheus.settings.properties:.}" })
@Slf4j
public class Grapheus {

    private static final String COMMAND_START = "start";
    private static final String COMMAND_STOP = "stop";
    private static final String COMMAND_STATUS = "status";
    private static final int SHUTDOWN_PORT = Integer.getInteger("grapheus.shutdown.port", 1212);

    @Value("${general.cache.size}")
    private long cacheSize;

    @Value("${general.cache.expiration.secs}")
    private long cacheExpire;

    @Bean
    public ShutdownLatch getShutdownLatch() {
        return new ShutdownLatch(SHUTDOWN_PORT, COMMAND_STOP, COMMAND_STATUS);
    }

    @Bean
    @Primary
    public CacheManager cacheManager() {
        GuavaCacheManager cacheManager = new GuavaCacheManager();
        cacheManager.setCacheBuilder(CacheBuilder.newBuilder().maximumSize(cacheSize)
                .expireAfterAccess(cacheExpire, TimeUnit.SECONDS).expireAfterWrite(cacheExpire, TimeUnit.SECONDS));
        return cacheManager;
    }

    @Bean
    public CacheManager cacheManagerShortMultiple() {
        GuavaCacheManager cacheManager = new GuavaCacheManager();
        cacheManager.setCacheBuilder(CacheBuilder.newBuilder().//
                maximumSize(1000).//
                concurrencyLevel(1).//
//                expireAfterAccess(5, TimeUnit.SECONDS).
                expireAfterWrite(5, TimeUnit.SECONDS));
        return cacheManager;
    }

    @Bean
    public CacheManager cacheManagerShortSingle() {
        GuavaCacheManager cacheManager = new GuavaCacheManager();
        cacheManager.setCacheBuilder(CacheBuilder.newBuilder().//
                maximumSize(1).//
                concurrencyLevel(1).//
//                expireAfterAccess(5, TimeUnit.SECONDS).
                expireAfterWrite(5, TimeUnit.SECONDS));
        return cacheManager;
    }

    /**
     * @param args
     * @throws IOException
     * @throws UnknownHostException
     */
    public static void main(String[] args) throws UnknownHostException, IOException {
        String command = args.length == 0 ? COMMAND_START : args[0];
        switch (command) {
        case COMMAND_START: {
            try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Grapheus.class)) {
                if (log.isDebugEnabled()) {
                    log.debug("Beans are loaded:\n{}", String.join("\n", Arrays.asList(ctx.getBeanDefinitionNames())
                            .stream().filter((name) -> !name.startsWith("org.springframework.")).collect(toList())));
                }

                printConfiguration(ctx);

                ctx.getBean(ShutdownLatch.class).waitForShutdown();
                log.info("Server has been stopped");
            }
            break;
        }
        case COMMAND_STOP: {
            log.info("Stopping server...");
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new Socket(InetAddress.getLocalHost(), SHUTDOWN_PORT).getOutputStream()))) {
                writer.write(COMMAND_STOP);
            }
            break;
        }
        case COMMAND_STATUS: {
            try (Socket s = new Socket(InetAddress.getLocalHost(), SHUTDOWN_PORT);
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
                writer.write(COMMAND_STATUS);
                writer.newLine();
                writer.flush();
                System.out.println(reader.readLine());
            } catch (Exception e) {
                if (args.length > 1 && "-v".equals(args[1])) {
                    log.error("Error getting status using port {}", SHUTDOWN_PORT, e);
                } else {
                    System.out.println("unknown:" + e.getMessage());
                }
            }
            break;
        }
        }
    }

    private static void printConfiguration(AnnotationConfigApplicationContext ctx) {
        Collection<HumanReadableConfigProvider> configurations = ctx.getBeansOfType(HumanReadableConfigProvider.class)
                .values();
        ConfigurationAccumulator configConsumer = new ConfigurationAccumulator();
        configurations.forEach(config -> config.provideConfig(configConsumer));
        log.info("Effective configuration:\n{}", configConsumer);
    }
}
