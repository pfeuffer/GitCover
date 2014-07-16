package de.pfeufferweb.gitcover;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class GCOptionsTest
{
    @Test
    public void parsesDirectoryAndRelease()
    {
        GCOptions options = new GCOptions();
        options.parse(new String[]
        { "/my/dir", "some/release" });
        assertEquals("/my/dir", options.getRepository());
        assertEquals("some/release", options.getReference());
        assertNull(options.getIgnoreFile());
        assertFalse(options.isExcludeAdded());
        assertFalse(options.isExcludeModified());
    }

    @Test
    public void parsesDirectoryAndReleaseWithIgnoreOptionOnly()
    {
        GCOptions options = new GCOptions();
        options.parse(new String[]
        { "/my/dir", "some/release", "-i", "myIgnore" });
        assertEquals("/my/dir", options.getRepository());
        assertEquals("some/release", options.getReference());
        assertEquals("myIgnore", options.getIgnoreFile());
        assertFalse(options.isExcludeAdded());
        assertFalse(options.isExcludeModified());
    }

    @Test
    public void parsesDirectoryAndReleaseWithIgnoreOptionOnlyInOtherOrder()
    {
        GCOptions options = new GCOptions();
        options.parse(new String[]
        { "-i", "myIgnore", "/my/dir", "some/release" });
        assertEquals("/my/dir", options.getRepository());
        assertEquals("some/release", options.getReference());
        assertEquals("myIgnore", options.getIgnoreFile());
        assertFalse(options.isExcludeAdded());
        assertFalse(options.isExcludeModified());
    }

    @Test
    public void parsesDirectoryAndReleaseWithIgnoreAndEclupseOptions()
    {
        GCOptions options = new GCOptions();
        options.parse(new String[]
        { "/my/dir", "some/release", "-i", "myIgnore", "-ea", "--exclude-modified" });
        assertEquals("/my/dir", options.getRepository());
        assertEquals("some/release", options.getReference());
        assertEquals("myIgnore", options.getIgnoreFile());
        assertTrue(options.isExcludeAdded());
        assertTrue(options.isExcludeModified());
    }

    @Test
    public void parsesDirectoryAndReleaseWithIgnoreAndEclupseOptionsInOtherOrder()
    {
        GCOptions options = new GCOptions();
        options.parse(new String[]
        { "/my/dir", "-i", "myIgnore", "-ea", "some/release", "--exclude-modified" });
        assertEquals("/my/dir", options.getRepository());
        assertEquals("some/release", options.getReference());
        assertEquals("myIgnore", options.getIgnoreFile());
        assertTrue(options.isExcludeAdded());
        assertTrue(options.isExcludeModified());
    }

    @Test
    public void throwsExceptionForIllegalOption()
    {
        GCOptions options = new GCOptions();
        options.parse(new String[]
        { "-xyz" });
        assertTrue(options.isFailed());
    }
}
