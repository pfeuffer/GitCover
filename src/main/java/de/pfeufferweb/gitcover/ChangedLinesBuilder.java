package de.pfeufferweb.gitcover;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.lib.AbbreviatedObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.ObjectStream;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class ChangedLinesBuilder
{
    private final Repository repository;
    private boolean includeModified = true;
    private boolean includeAdded = true;

    public ChangedLinesBuilder(String repoFolder) throws Exception
    {
        this.repository = new RepositoryBuilder().findGitDir(new File(repoFolder)).build();
    }

    public ChangedLines build(String revision) throws Exception
    {
        ChangedLines changedLines = new ChangedLines();
        Git git = new Git(repository);

        ObjectId headId = repository.resolve("HEAD^{tree}");
        ObjectId oldId = repository.resolve(revision + "^{tree}");

        ObjectReader reader = repository.newObjectReader();

        CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
        newTreeIter.reset(reader, headId);
        CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
        oldTreeIter.reset(reader, oldId);

        List<DiffEntry> diffs = git.diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();

        for (DiffEntry diff : diffs)
        {
            boolean isRelevantFile = diff.getNewPath().endsWith(".java");
            if (isRelevantFile)
            {
                if (includeModified && isModified(diff))
                {
                    Map<Integer, String> lines = process(diff);
                    changedLines.addFile(diff.getNewPath(), lines);
                }
                else if (includeAdded && isAdded(diff))
                {
                    Map<Integer, String> lines = createLines(load(diff.getNewId()));
                    changedLines.addFile(diff.getNewPath(), lines);
                }
            }
        }
        return changedLines;
    }

    public void setIncludeModified(boolean includeModified)
    {
        this.includeModified = includeModified;
    }

    public void setIncludeAdded(boolean includeAdded)
    {
        this.includeAdded = includeAdded;
    }

    private boolean isModified(DiffEntry diff)
    {
        return diff.getChangeType() == ChangeType.MODIFY;
    }

    private boolean isAdded(DiffEntry diff)
    {
        return diff.getChangeType() == ChangeType.ADD;
    }

    private Map<Integer, String> createLines(List<String> content)
    {
        Map<Integer, String> lines = new HashMap<Integer, String>();
        for (int i = 0; i < content.size(); ++i)
        {
            lines.put(i + 1, content.get(i));
        }
        return lines;
    }

    private Map<Integer, String> process(DiffEntry diff) throws Exception
    {
        Patch patch = DiffUtils.diff(load(diff.getOldId()), load(diff.getNewId()));
        Map<Integer, String> lines = new HashMap<Integer, String>();
        for (Delta delta : patch.getDeltas())
        {
            int initialPosition = delta.getRevised().getPosition();
            int diffLength = delta.getRevised().getLines().size();
            for (int i = 0; i < diffLength; ++i)
            {
                lines.put(initialPosition + i + 1, delta.getRevised().getLines().get(i).toString());
            }
        }
        return lines;
    }

    private List<String> load(AbbreviatedObjectId objectId) throws Exception
    {
        ObjectLoader loader = repository.open(objectId.toObjectId());

        ObjectStream stream = loader.openStream();
        return new FileLoader(stream).load();
    }
}
