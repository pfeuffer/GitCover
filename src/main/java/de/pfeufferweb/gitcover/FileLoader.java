package de.pfeufferweb.gitcover;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

class FileLoader
{
    private final InputStream stream;

    public FileLoader(InputStream stream)
    {
        this.stream = stream;
    }

    List<String> load() throws IOException
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        String line;
        List<String> lines = new ArrayList<String>();
        while ((line = in.readLine()) != null)
        {
            lines.add(line);
        }
        return lines;
    }
}
