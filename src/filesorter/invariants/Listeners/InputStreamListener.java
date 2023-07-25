package filesorter.invariants.Listeners;

import filesorter.DirectoryTreeWalker;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class InputStreamListener implements DirectoryTreeWalker.Listener
{
    private final List<String> errors = new ArrayList<>();

    @Override
    public void notifyFile(Path file)
    {
        try {
            notifyInputStream(file, new FileInputStream(file.toFile()));
        } catch (FileNotFoundException e) {
            errors.add("File not found. " + file);
        }
    }

    public abstract void notifyInputStream(Path file, InputStream inputStream);
}
