package filesorter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CachedDirectoryTreeWalker extends DirectoryTreeWalker {

    private final List<Path> cachedFiles = new ArrayList<>();
    private boolean firstTime = true;

    public CachedDirectoryTreeWalker(File file)
    {
        super(file);

        addListener(this::cachedNotifyFile);
    }

    @Override
    public void walkTree() throws IOException {
        resetCounter();
        startTimeTaken();
        if(firstTime)
        {
            super.walkTree();
            firstTime = false;
        }
        else
        {
            cachedFiles.forEach(this::notifyFile);
        }
        stopTimeTaken();
    }

    private void cachedNotifyFile(Path file1) {
        if (firstTime) cachedFiles.add(file1);
    }
}
