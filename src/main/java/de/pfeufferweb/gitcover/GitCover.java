package de.pfeufferweb.gitcover;

import static java.util.Collections.sort;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class GitCover
{
    public static void main(String[] args) throws Exception
    {
        if (args.length == 0)
        {
            args = new String[]
            { "w:/S42_Production", "origin/integration/05.02.00" };
        }
        ChangedLines changedLines = new ChangedLinesBuilder(args[0]).build(args[1]);
        Coverage coverage = new CoverageBuilder().computeAll(new File(args[0]));
        System.out.println(changedLines.getFileNames());
        System.out.println(coverage.getFileNames());
        for (String changedFile : changedLines.getFileNames())
        {
            System.out.println(changedFile);
            List<Integer> lines = new ArrayList<Integer>(changedLines.getChangedLines(changedFile));
            sort(lines);
            try
            {
                Map<Integer, Integer> lineCoverage = coverage.getCoverage(changedFile);
                for (int line : lines)
                {
                    if (lineCoverage.containsKey(line))
                    {
                        System.out.println("C: " + line + " > " + lineCoverage.get(line));
                    }
                    else
                    {
                        System.out.println("I: " + line);
                    }
                }
            }
            catch (NoSuchElementException e)
            {
                for (int line : lines)
                {
                    System.out.println("N: " + line);
                }
            }
        }
    }
}
