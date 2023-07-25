package filesorter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DirectoryTreeWalker {
    private final List<Listener> listeners = new ArrayList<>();
    private final File path;
    private Instant start = Instant.EPOCH;
    private Instant ends = Instant.EPOCH;
    private int counter;

    public DirectoryTreeWalker(File path) {
        this.path = path;
    }

    public void walkTree() throws IOException {
        resetCounter();
        startTimeTaken();
        Files.walk(Paths.get(path.getPath()))
                .filter(Files::isRegularFile)
                .forEach(this::notifyFile);
        stopTimeTaken();
    }

    protected void resetCounter()
    {
        counter = 0;
    }

    protected void startTimeTaken() {
        start = Instant.now();
    }

    protected void stopTimeTaken() {
        ends = Instant.now();
    }

    protected void notifyFile(Path file)
    {
        //synchronized (listeners)
        {
            counter++;
            listeners.forEach(listener -> listener.notifyFile(file));
        }
    }

    public void addListener(Listener listener) {
        //synchronized (listeners)
        {
            listeners.add(listener);
        }
    }

    public void removeListener(Listener listener) {
        //synchronized (listeners)
        {
            listeners.remove(listener);
        }
    }

    public Duration getTimeTaken() {
        return Duration.between(start, ends);
    }

    public int getCount() {
        return counter;
    }

    public interface Listener {
        void notifyFile(Path file);
    }
}