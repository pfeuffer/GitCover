package de.pfeufferweb.gitcover;

import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ChangedLines
{
    private final Map<String, Collection<Integer>> changedLines = new HashMap<String, Collection<Integer>>();

    public void addFile(String fileName, Collection<Integer> lines)
    {
        changedLines.put(fileName, new HashSet<Integer>(lines));
    }

    public Collection<String> getFileNames()
    {
        return unmodifiableCollection(changedLines.keySet());
    }

    public Collection<Integer> getChangedLines(String changedFile)
    {
        return unmodifiableCollection(changedLines.get(changedFile));
    }

}
