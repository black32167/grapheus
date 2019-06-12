package org.grapheus.cli;

import java.util.List;

import javax.inject.Inject;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.spi.SubCommand;
import org.kohsuke.args4j.spi.SubCommandHandler;
import org.kohsuke.args4j.spi.SubCommands;
import org.grapheus.cli.subcommand.ListTasks;
import org.grapheus.cli.subcommand.RecreateProfile;
import org.grapheus.cli.subcommand.GrapheusCLICommand;
import org.grapheus.cli.subcommand.UploadCommand;
import org.grapheus.cli.subcommand.analytics.AnalyticsCommand;
import org.grapheus.cli.subcommand.create.CreateCommand;
import org.grapheus.cli.subcommand.delete.DeleteCommand;
import org.grapheus.cli.subcommand.processor.CommandProcessor;
import org.grapheus.client.GrapheusClientFactory;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({ @PropertySource("grapheus-cli-default.properties") })
@ComponentScan
public class GrapheusCL implements CommandLineRunner {

    private static final long SERVER_READY_TIMEOUT = 10000;

    @Argument(required = true, index = 0, metaVar = "action", usage = "", handler = SubCommandHandler.class)
    @SubCommands({
            @SubCommand(name = "create", impl = CreateCommand.class),
            @SubCommand(name = "recreate", impl = RecreateProfile.class),
            @SubCommand(name = "delete", impl = DeleteCommand.class),
            @SubCommand(name = "list", impl = ListTasks.class),
            @SubCommand(name = "upload", impl = UploadCommand.class),
            @SubCommand(name = "find", impl = AnalyticsCommand.class)})
    protected GrapheusCLICommand action;

    @Inject
    private List<CommandProcessor> commandProcessors;
    
    @Inject
    private GrapheusClientFactory rcFactory;

    @Override
    public void run(String... args) throws Exception {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            // handling of wrong arguments
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            return;
        }

        // Wait for server is ready
        if(!rcFactory.healthcheck().waitTillReady(SERVER_READY_TIMEOUT)) {
            throw new Exception("Server '" + rcFactory.getBackendURL() + "' was not ready in " + SERVER_READY_TIMEOUT + " ms.");
        }
        
        CommandProcessor processor = commandProcessors.stream().//
                filter((p) -> p.processingClass().equals(action.getClass())).//
                findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown command:" + action));

        processor.process(action);
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(GrapheusCL.class);
        app.setBannerMode(Mode.OFF);
        app.run(args);
    }

}
