package de.pfeufferweb.gitcover;

import java.util.List;
import java.util.Map;

class FileCoverage
{
    final int changesLines;
    final int relevantLines;
    final int coveredLines;

    private FileCoverage(int changesLines, int relevantLines, int coveredLines)
    {
        this.changesLines = changesLines;
        this.relevantLines = relevantLines;
        this.coveredLines = coveredLines;
    }

    public static FileCoverage buildFrom(Map<Integer, Integer> lineCoverage, List<Integer> lines)
    {
        int changedLines = 0;
        int coveredLines = 0;
        int relevantLines = 0;
        for (int line : lines)
        {
            ++changedLines;
            if (lineCoverage.containsKey(line))
            {
                ++relevantLines;
                if (lineCoverage.get(line) > 0)
                {
                    ++coveredLines;
                }
            }
        }
        return new FileCoverage(changedLines, relevantLines, coveredLines);
    }

    boolean completelyCovered()
    {
        return changesLines == coveredLines;
    }

    @Override
    public String toString()
    {
        return changesLines + " Zeile" + (changesLines == 1 ? "" : "n") + " geändert, " + relevantLines + " Zeile"
                + (relevantLines == 1 ? "" : "n") + " testrelevant" + ", Testabdeckung: " + getCoverage() + "%";
    }

    int getCoverage()
    {
        return relevantLines == 0 ? 100 : (100 * coveredLines / relevantLines);
    }
}
