package de.pfeufferweb.gitcover;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.DiffCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

public class Test
{
    public static void main(String[] args) throws IOException, GitAPIException
    {
        new Test().test();
    }

    private void test() throws IOException, AmbiguousObjectException, IncorrectObjectTypeException, GitAPIException
    {
        Repository repo = new RepositoryBuilder().setGitDir(new File("w:/S42_Production/.git")).build();
        Git git = new Git(repo);

        ObjectId headId = repo.resolve("HEAD^{tree}");
        ObjectId oldId = repo.resolve("origin/integration/05.02.02^{tree}");

        ObjectReader reader = repo.newObjectReader();

        CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
        newTreeIter.reset(reader, headId);
        CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
        oldTreeIter.reset(reader, oldId);

        DiffCommand diff = git.diff().setNewTree(newTreeIter).setOldTree(oldTreeIter);
        List<DiffEntry> diffs = diff.call();

        for (DiffEntry entry : diffs)
        {
            System.out.println("-------------------------------------------------------");
            System.out.println(entry.getNewPath());
            System.out.println(entry.getChangeType());
            System.out.println(entry.getNewId() + " -> " + entry.getOldId());

        }
    }
}
