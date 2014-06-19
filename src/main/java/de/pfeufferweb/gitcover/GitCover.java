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
        out.println("<!-- Style by http://quhno.internetstrahlen.de/myopera/csstests/collapsible-paragraph.html -->");
        out.println("<style type='text/css'>");
        out.println("a.exp {display:block;margin: 1em 0 0 0;text-decoration:none;border:1px solid black;border-radius:4px;background:#CDF;padding:10px;}");
        out.println("a.exp::after {content:\"»\";float:right;}");
        out.println("a.exp:focus {border-width: 1px 1px 0 1px;border-radius:4px 4px 0 0}");
        out.println("a.exp + div {display:none;}");
        out.println("a.exp:focus + div {display:block;border-width: 0 1px 1px 1px;border-style:solid; border-radius:0 0 4px 4px;border-color:black;}");
        out.println("a.exp:focus::after {content:\"\";}");
        out.println("div.exp *{padding:0.3em 10px 0em 10px;}");
        out.println("div.exp table:last-child::after {content:\"«\";float:right;}");
        out.println("div.exp *:first-child {margin-top:0;}");
        out.println(".notCovered {background: red;}");
        out.println(".covered {background: green;}");
        out.println(".ignored {background: white;}");
        out.println(".notChecked {background: orange;}");
        out.println("</style>");
        ChangedLines changedLines = new ChangedLinesBuilder(directory).build(branch);
        Coverage coverage = new CoverageBuilder().computeAll(new File(directory));
        out.println("<body>");
        out.println("<h1>Unittestabdeckung der Änderungen bzgl. Branch " + branch + "</h1>");
        List<String> fileNames = new ArrayList<String>(changedLines.getFileNames());
        sort(fileNames);
        for (String changedFile : fileNames)
        {
            out.println("<a class='exp' href='#url'><h2>" + changedFile + "</h2></a>");
            out.println("<div class='exp'><table><tr><th>Art</th><th>Zeile</th><th>Abdeckung</th><th>Code</th></tr>");

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
                                .toString(), lineCoverage.get(line) == 0 ? "notCovered" : "covered", "C");
                    }
                    else
                    {
                        writeResutlLine(line, changedLines.getLine(changedFile, line), "-", "ignored", "I");
                    }
                }
            }
            catch (NoSuchElementException e)
            {
                for (int line : lines)
                {
                    writeResutlLine(line, changedLines.getLine(changedFile, line), "0", "notChecked", "N");
                }
            }
            out.println("</table></div>");
        }
        out.println("</body>");
        out.println("</html>");
    }

    private void writeResutlLine(int line, String content, String c, String status, String type)
    {
        out.println("<tr class='" + status + "'><td>" + type + "</td><td>" + line + "</td><td>" + c
                + "</td><td style='font-family: monospace;'>" + fixWhitespaces(content) + "</td></tr>");
    }

    private String fixWhitespaces(String content)
    {
        return content.replaceAll("\t", " ").replaceAll(" ", "&nbsp;");
    }
}
