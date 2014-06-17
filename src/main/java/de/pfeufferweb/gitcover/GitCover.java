package de.pfeufferweb.gitcover;

import java.io.File;
import java.util.Map;
import java.util.NoSuchElementException;

public class GitCover
{
    public static void main(String[] args) throws Exception
    {
        if (args.length == 0)
        {
            args = new String[]
            { "w:/S42_Production" };
        }
        ChangedLines changedLines = new ChangedLinesBuilder(args[0]).build();
        Coverage coverage = new CoverageBuilder().computeAll(new File(args[0]));
        System.out.println(changedLines.getFileNames());
        System.out.println(coverage.getFileNames());
        for (String changedFile : changedLines.getFileNames())
        {
            System.out.println(changedFile);
            try
            {
                Map<Integer, Integer> lineCoverage = coverage.getCoverage(changedFile);
                for (int line : changedLines.getChangedLines(changedFile))
                {
                    if (lineCoverage.containsKey(line))
                    {
                        System.out.println(line + " > " + lineCoverage.get(line));
                    }
                }
            }
            catch (NoSuchElementException e)
            {
                for (int line : changedLines.getChangedLines(changedFile))
                {
                    System.out.println(line + " > " + 0);
                }
            }
        }
    }
}
