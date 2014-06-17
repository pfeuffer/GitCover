package de.pfeufferweb.gitcover;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.lib.AbbreviatedObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class ChangedLinesBuilder
{
    private final Repository repository;

    public static void main(String[] args) throws Exception
    {
        new ChangedLinesBuilder("w:/S42_Production").build();
    }

    public ChangedLinesBuilder(String repoFolder) throws Exception
    {
        this.repository = new RepositoryBuilder().findGitDir(new File(repoFolder)).build();
    }

    public ChangedLines build() throws Exception
    {
        ChangedLines changedLines = new ChangedLines();
        Git git = new Git(repository);

        ObjectId headId = repository.resolve("HEAD^{tree}");
        ObjectId oldId = repository.resolve("origin/integration/05.02.02^{tree}");

        ObjectReader reader = repository.newObjectReader();

        CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
        newTreeIter.reset(reader, headId);
        CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
        oldTreeIter.reset(reader, oldId);

        List<DiffEntry> diffs = git.diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();

        for (DiffEntry diff : diffs)
        {
            if (diff.getChangeType() == ChangeType.MODIFY && diff.getNewPath().endsWith(".java"))
            {
                System.out.println("diffs in " + diff.getNewPath());
                Collection<Integer> lines = process(diff);
                changedLines.addFile(diff.getNewPath(), lines);
            }
        }
        return changedLines;
    }

    private Collection<Integer> process(DiffEntry diff) throws Exception
    {
        Patch patch = DiffUtils.diff(load(diff.getOldId()), load(diff.getNewId()));
        Collection<Integer> lines = new HashSet<Integer>();
        for (Delta delta : patch.getDeltas())
        {
            lines.add(delta.getOriginal().getPosition());
        }
        return lines;
    }

    private List<String> load(AbbreviatedObjectId objectId) throws Exception
    {
        ObjectLoader loader = repository.open(objectId.toObjectId());

        // and then one can use either
        BufferedReader in = new BufferedReader(new InputStreamReader(loader.openStream()));
        String line;
        List<String> lines = new ArrayList<String>();
        while ((line = in.readLine()) != null)
        {
            lines.add(line);
        }
        return lines;
    }
}
