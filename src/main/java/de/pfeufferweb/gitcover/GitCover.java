package de.pfeufferweb.gitcover;

import static java.util.Collections.sort;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

public class GitCover
{
    public static void main(String[] args) throws Exception
    {
        GCOptions options = new GCOptions();
        options.parse(args);
        if (options.isFailed())
        {
            return;
        }
        GitCover gitCover = new GitCover(System.out);
        if (options.hasIgnoreFile())
        {
            gitCover.ignore(new File(options.getIgnoreFile()));
        }
        gitCover.process(options);
    }

    private final PrintStream out;
    private final Collection<Pattern> ignore;

    public GitCover(PrintStream out)
    {
        this.out = out;
        this.ignore = new ArrayList<Pattern>();
    }

    private void ignore(File file) throws Exception
    {
        for (String pattern : new FileLoader(new FileInputStream(file)).load())
        {
            if (pattern.trim().length() > 0)
            {
                ignore.add(Pattern.compile(".*" + pattern + ".*"));
            }
        }
    }

    private void process(GCOptions options) throws Exception
    {
        out.println("<html>");
        out.println("<script type='text/javascript'>");
        out.println("function AppendColor(light) {\n" + "    $(\".dark\").each(function(i){\n"
                + "      // get the RGB from existing elements\n"
                + "        var color = $(this).css(\"background-color\");\n"
                + "        color = color.replace(/[^0-9,]+/g, \"\");\n" + "        var red = color.split(\",\")[0];\n"
                + "        var gre = color.split(\",\")[1];\n" + "        var blu = color.split(\",\")[2];\n" + "\n"
                + "      // convert rgb to hsv\n" + "        var hsv = RgbToHsv(red,gre,blu);\n"
                + "      // convert hsv to rgb modifing the `v` param\n"
                + "        var rgb = HsvToRgb(hsv.h, hsv.s, light);\n" + "\n"
                + "      // creates a new div and set the new background\n" + "      // then appends to the content\n"
                + "        color = \"rgb(\" + rgb.r + \",\" + rgb.g + \",\" + rgb.b + \")\";\n"
                + "        $(\"<div />\")\n" + "            .css(\"background\", color)\n"
                + "            .attr(\"title\", color)\n" + "            .appendTo($(\".light\").parent());\n"
                + "        $(\"<span />\").html(\" \").appendTo($(\".light\").parent())\n" + "    });\n"
                + "    $(\"<br />\").appendTo($(\".light\").parent())\n" + "}\n" + "\n" + "// tested values\n");
        out.println("</script>");
        out.println("<!-- Style by http://quhno.internetstrahlen.de/myopera/csstests/collapsible-paragraph.html -->");
        out.println("<style type='text/css'>");
        out.println("h2 {margin: 0 0 0 0;}");
        out.println("a.exp {display:block;margin: 1em 0 0 0;text-decoration:none;border:1px solid black;border-radius:4px;padding:10px;}");
        out.println("a.ignored {background:#CDF;}");
        out.println("a.allCovered {background:#CFC;}");
        out.println("a.coverageMissing {background:#FDC;}");
        out.println("a.exp::after {content:\"»\";float:right;}");
        out.println("a.exp:focus {border-width: 1px 1px 0 1px;border-radius:4px 4px 0 0}");
        out.println("a.exp + div {display:none;}");
        out.println("a.exp:focus + div {display:block;border-width: 0 1px 1px 1px;border-style:solid; border-radius:0 0 4px 4px;border-color:black;}");
        out.println("a.exp:focus::after {content:\"\";}");
        out.println("div.exp *{padding:0.3em 10px 0em 10px;}");
        out.println("div.exp table:last-child::after {content:\"«\";float:right;}");
        out.println("div.exp *:first-child {margin-top:0;}");
        out.println("tr.notCovered {background: orangered;}");
        out.println("tr.covered {background: lightgreen;}");
        out.println("tr.ignored {background: white;}");
        out.println("tr.notChecked {background: orange;}");
        out.println("</style>");
        ChangedLines changedLines = createChangedLinesBuilder(options, options.getRepository()).build(
                options.getReference());
        Coverage coverage = new CoverageBuilder().computeAll(new File(options.getRepository()));
        out.println("<body>");
        out.println("<h1>Unittestabdeckung der Änderungen bzgl. Branch " + options.getReference() + "</h1>");
        List<String> fileNames = new ArrayList<String>(changedLines.getFileNames());
        sort(fileNames);
        OverallCoverage overall = new OverallCoverage();
        for (String changedFile : fileNames)
        {
            if (isIgnored(changedFile))
            {
                continue;
            }
            List<Integer> lines = new ArrayList<Integer>(changedLines.getChangedLines(changedFile));
            sort(lines);
            try
            {
                Map<Integer, Integer> lineCoverage = coverage.getCoverage(changedFile);
                FileCoverage fileCoverage = FileCoverage.buildFrom(lineCoverage, lines);
                overall.add(fileCoverage);
                writeHeader(changedFile, fileCoverage.changesLines == 0 ? "ignored"
                        : fileCoverage.getCoverage() >= 80 ? "allCovered" : "coverageMissing", fileCoverage.toString());
                for (int line : lines)
                {
                    if (lineCoverage.containsKey(line))
                    {
                        writeResutlLine(line, changedLines.getLine(changedFile, line), lineCoverage.get(line)
                                .toString(), lineCoverage.get(line) == 0 ? "notCovered" : "covered");
                    }
                    else
                    {
                        writeResutlLine(line, changedLines.getLine(changedFile, line), "-", "ignored");
                    }
                }
            }
            catch (NoSuchElementException e)
            {
                writeHeader(changedFile, "ignored", "nicht im Test");
                for (int line : lines)
                {
                    writeResutlLine(line, changedLines.getLine(changedFile, line), "0", "notChecked");
                }
            }
            out.println("</table></div>");
        }
        out.println("Durchschnittliche Abdeckung aller testrelevanten Änderungen: " + overall.getCoverage() + "%");
        out.println("</body>");
        out.println("</html>");
    }

    private ChangedLinesBuilder createChangedLinesBuilder(GCOptions options, String directory) throws Exception
    {
        ChangedLinesBuilder changedLinesBuilder = new ChangedLinesBuilder(directory);
        changedLinesBuilder.setIncludeAdded(!options.isExcludeAdded());
        changedLinesBuilder.setIncludeModified(!options.isExcludeModified());
        return changedLinesBuilder;
    }

    private boolean isIgnored(String changedFile)
    {
        for (Pattern pattern : this.ignore)
        {
            if (pattern.matcher(changedFile).matches())
            {
                return true;
            }
        }
        return false;
    }

    private void writeHeader(String changedFile, String type, String label)
    {
        out.println("<a class='exp " + type + "' href='#url'><h2>" + changedFile + "</h2>" + label + "</a>");
        out.println("<div class='exp'><table><tr><th>Zeile</th><th>Abdeckung</th><th>Code</th></tr>");
    }

    private void writeResutlLine(int line, String content, String c, String status)
    {
        out.println("<tr class='" + status + "'><td>" + line + "</td><td>" + c
                + "</td><td style='font-family: monospace;'>" + fixWhitespaces(content) + "</td></tr>");
    }

    private String fixWhitespaces(String content)
    {
        return content.replaceAll("\t", " ").replaceAll(" ", "&nbsp;");
    }
}
