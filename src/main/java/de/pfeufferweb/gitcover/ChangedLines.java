package de.pfeufferweb.gitcover;

import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ChangedLines
{
    private final Map<String, Map<Integer, String>> changedLines = new HashMap<String, Map<Integer, String>>();

    public void addFile(String fileName, Map<Integer, String> lines)
    {
        changedLines.put(fileName, new HashMap<Integer, String>(lines));
    }

    public Collection<String> getFileNames()
    {
        return unmodifiableCollection(changedLines.keySet());
    }

    public Collection<Integer> getChangedLines(String changedFile)
    {
        return unmodifiableCollection(changedLines.get(changedFile).keySet());
    }

    public String getLine(String changedFile, int line)
    {
        return changedLines.get(changedFile).get(line);
    }

}
