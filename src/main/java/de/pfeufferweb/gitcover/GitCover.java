package de.pfeufferweb.gitcover;

import static java.util.Collections.sort;

import java.io.File;
import java.io.PrintStream;
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
        new GitCover(System.out).process(args[0], args[1]);
    }

    private final PrintStream out;

    public GitCover(PrintStream out)
    {
        this.out = out;
    }

    private void process(String directory, String branch) throws Exception
    {
        out.println("<html>");
        ChangedLines changedLines = new ChangedLinesBuilder(directory).build(branch);
        Coverage coverage = new CoverageBuilder().computeAll(new File(directory));
        out.println("<body>");
        out.println("<h1>Unittestabdeckung der Änderungen bzgl. Branch " + branch + "</h1>");
        List<String> fileNames = new ArrayList<String>(changedLines.getFileNames());
        sort(fileNames);
        for (String changedFile : fileNames)
        {
            out.println("<p>");
            out.println("<h2>" + changedFile + "</h2>");
            out.println("<table><tr><th>Art</th><th>Zeile</th><th>Abdeckung</th><th>Code</th></tr>");

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
            out.println("</table>");
        }
        out.println("</body>");
        out.println("</html>");
    }

    private void writeResutlLine(int line, String content, String c, String color, String type)
    {
        out.println("<tr style='background: " + color + ";'><td>" + type + "</td><td>" + line + "</td><td>" + c
                + "</td><td style='font-family: monospace;'>" + content + "</td></tr>");
    }
}
