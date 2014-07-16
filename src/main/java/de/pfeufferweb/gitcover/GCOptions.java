package de.pfeufferweb.gitcover;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

class GCOptions
{
    private boolean excludeAdded = false;
    private boolean excludeModified = false;
    private String ignoreFile = null;
    private String repository = null;
    private String reference = null;
    private boolean failed = false;

    public void parse(String[] args)
    {
        Options options = buildOptions();

        CommandLineParser parser = new GnuParser();
        try
        {
            parse(args, options, parser);
        }
        catch (ParseException e)
        {
            System.err.println(e.getLocalizedMessage());
            printHelp(options);
            failed = true;
        }
    }

    private void parse(String[] args, Options options, CommandLineParser parser) throws ParseException
    {
        CommandLine line = parser.parse(options, args);
        excludeAdded = line.hasOption("ea");
        excludeModified = line.hasOption("em");
        if (line.hasOption("ignore"))
        {
            ignoreFile = line.getOptionValue("ignore");
        }
        if (line.getArgs().length == 2)
        {
            repository = line.getArgs()[0];
            reference = line.getArgs()[1];
        }
        else
        {
            printHelp(options);
            failed = true;
        }
    }

    @SuppressWarnings("static-access")
    private Options buildOptions()
    {
        Options options = new Options();
        Option ignoreFileOption = OptionBuilder.withArgName("ignore file").withLongOpt("ignore").hasArg()
                .withDescription("file containing patterns of files to be ignored").create("i");
        Option excludeModifiedOption = OptionBuilder.withLongOpt("exclude-modified")
                .withDescription("use this to ignore files that have been modified").create("em");
        Option excludeAddedOption = OptionBuilder.withLongOpt("exclude-added")
                .withDescription("use this to ignore files that have been modified").create("ea");
        options.addOption(ignoreFileOption);
        options.addOption(excludeModifiedOption);
        options.addOption(excludeAddedOption);
        return options;
    }

    private void printHelp(Options options)
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(GitCover.class.getSimpleName() + " <git directory> <commit to compare with>", options);
    }

    public boolean isExcludeAdded()
    {
        return excludeAdded;
    }

    public boolean isExcludeModified()
    {
        return excludeModified;
    }

    public boolean hasIgnoreFile()
    {
        return ignoreFile != null;
    }

    public String getIgnoreFile()
    {
        return ignoreFile;
    }

    public String getRepository()
    {
        return repository;
    }

    public String getReference()
    {
        return reference;
    }

    public boolean isFailed()
    {
        return failed;
    }
}
