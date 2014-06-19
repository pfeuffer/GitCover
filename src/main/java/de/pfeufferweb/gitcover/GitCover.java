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
        System.out.println("<h1>Unittestabdeckung der Änderungen bzgl. Branch " + args[1] + "</h1>");
        for (String changedFile : changedLines.getFileNames())
        {
            System.out.println("<p>");
            System.out.println("<h2>" + changedFile + "</h2>");
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
                        writeResutlLine(line, changedLines.getLine(changedFile, line), lineCoverage.get(line)
                                .toString(), lineCoverage.get(line) == 0 ? "red" : "green", "C");
                    }
                    else
                    {
                        writeResutlLine(line, changedLines.getLine(changedFile, line), "-", "white", "I");
                    }
                }
            }
            catch (NoSuchElementException e)
            {
                for (int line : lines)
                {
                    writeResutlLine(line, changedLines.getLine(changedFile, line), "0", "orange", "N");
                }
            }
            System.out.println("</table>");
        }
        System.out.println("</body>");
        System.out.println("</html>");
    }

    private static void writeResutlLine(int line, String content, String c, String color, String type)
    {
        System.out.println("<tr style='background: " + color + ";'><td>" + type + "</td><td>" + line + "</td><td>" + c
                + "</td><td style='font-family: monospace;'>" + content + "</td></tr>");
    }
}
