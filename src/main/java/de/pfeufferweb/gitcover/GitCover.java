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
        System.out.println("<html>");
        System.out.println("<!--");
        ChangedLines changedLines = new ChangedLinesBuilder(args[0]).build(args[1]);
        Coverage coverage = new CoverageBuilder().computeAll(new File(args[0]));
        System.out.println(changedLines.getFileNames());
        System.out.println(coverage.getFileNames());
        System.out.println("-->");
        System.out.println("<body>");
        for (String changedFile : changedLines.getFileNames())
        {
            System.out.println("<p>");
            System.out.println(changedFile);
            System.out.println("<table><tr><th>Art</th><th>Zeile</th><th>Abdeckung</th><th>Code</th></tr>");

            List<Integer> lines = new ArrayList<Integer>(changedLines.getChangedLines(changedFile));
            sort(lines);
            try
            {
                Map<Integer, Integer> lineCoverage = coverage.getCoverage(changedFile);
                for (int line : lines)
                {
                    if (lineCoverage.containsKey(line))
                    {
                        System.out.println("<tr style='background: " + (lineCoverage.get(line) == 0 ? "red" : "green")
                                + ";'><td>C</td><td>" + line + "</td><td>" + lineCoverage.get(line)
                                + "</td><td style='font-family: monospace;'>" + changedLines.getLine(changedFile, line)
                                + "</td></tr>");
                    }
                    else
                    {
                        System.out.println("<tr><td>I</td><td>" + line + "</td><td>-</td><td>"
                                + changedLines.getLine(changedFile, line) + "</td></tr>");
                    }
                }
            }
            catch (NoSuchElementException e)
            {
                for (int line : lines)
                {
                    System.out.println("<tr style='background: 'orange'><td>N</td><td>" + line
                            + "</td><td>0</td><td style='font-family: monospace;'>"
                            + changedLines.getLine(changedFile, line) + "</td></tr>");
                }
            }
            System.out.println("</table>");
        }
        System.out.println("</body>");
        System.out.println("</html>");
    }
}
