package de.pfeufferweb.gitcover;

import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class Coverage
{
    private final Map<String, Map<Integer, Integer>> coverage = new HashMap<String, Map<Integer, Integer>>();

    void addFile(String fileName)
    {
        coverage.put(fileName, new HashMap<Integer, Integer>());
    }

    void addLine(String fileName, int line, int hits)
    {
        coverage.get(fileName).put(line, hits);
    }

    public void addAll(Coverage subCoverage)
    {
        this.coverage.putAll(subCoverage.coverage);
    }

    /**
     * @throws NoSuchElementException
     *             whenever there is no coverage for the given file.
     */
    public Map<Integer, Integer> getCoverage(String name)
    {
        for (String candidate : getFileNames())
        {
            if (name.endsWith(candidate) || candidate.endsWith(name))
            {
                return coverage.get(candidate);
            }
        }
        throw new NoSuchElementException("no coverage found for file " + name);
    }

    public Collection<String> getFileNames()
    {
        return unmodifiableCollection(coverage.keySet());
    }
}
