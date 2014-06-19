package de.pfeufferweb.gitcover;

class OverallCoverage
{
    private int relevantLines = 0;
    private int coveredLines = 0;

    void add(FileCoverage fileCoverage)
    {
        relevantLines += fileCoverage.relevantLines;
        coveredLines += fileCoverage.coveredLines;
    }

    public int getCoverage()
    {
        return relevantLines == 0 ? 100 : (100 * coveredLines / relevantLines);
    }
}
