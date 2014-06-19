package de.pfeufferweb.gitcover;

import java.util.List;
import java.util.Map;

class FileCoverage
{
    private final int changesLines;
    private final int coveredLines;

    private FileCoverage(int changesLines, int coveredLines)
    {
        this.changesLines = changesLines;
        this.coveredLines = coveredLines;
    }

    public static FileCoverage buildFrom(Map<Integer, Integer> lineCoverage, List<Integer> lines)
    {
        int changedLines = 0;
        int coveredLines = 0;
        for (int line : lines)
        {
            ++changedLines;
            if (lineCoverage.containsKey(line) && lineCoverage.get(line) > 0)
            {
                ++coveredLines;
            }
        }
        return new FileCoverage(changedLines, coveredLines);
    }

    boolean completelyCovered()
    {
        return changesLines == coveredLines;
    }

    @Override
    public String toString()
    {
        return changesLines + " Zeilen geändert, Testabdeckung: " + getCoverage() + "%";
    }

    int getCoverage()
    {
        return 100 * coveredLines / changesLines;
    }
}
