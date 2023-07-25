package filesorter;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import static filesorter.DurationConverter.getDurationToSeconds;
import static filesorter.TemporaryDirectoryFileIOMethods.copyFolder;
import static filesorter.TemporaryDirectoryFileIOMethods.deleteDirectoryRecursively;
import static org.assertj.core.api.Assertions.assertThat;

public class CachedDirectoryWalkerTest //extends AssumptionTestBase
{
    private static final String DIR = "/home/davidnorth/temp";
    private Path tempDir;

    @Before
    public void setUp() throws Exception {
        tempDir = Files.createTempDirectory("shouldFixSyncIssues");
        System.out.println(tempDir);

        File test = new File(DIR + "/test");
        copyFolder(test.toPath(), tempDir);
    }

    @After
    public void tearDown() throws Exception {
        deleteDirectoryRecursively(tempDir);
    }

    @Test
    public void shouldCachedFilesFromWalkedTree() throws IOException
    {
        shouldCachedFiles(tempDir + "/master");
    }

    @Test
    public void shouldCachedFilesFromWalkedTreeFromLargeTree() throws IOException {
        

        shouldCachedFiles("/mnt/ntfs/Photos 2020/");
    }

    public void shouldCachedFiles(final String masterDir) throws IOException
    {
        final Set<String> files = new TreeSet<>();

        AtomicInteger counter = new AtomicInteger(0);

        DirectoryTreeWalker walker = new CachedDirectoryTreeWalker(new File(masterDir));
        DirectoryTreeWalker.Listener listener = file -> {
            files.add(file.toString());
            counter.incrementAndGet();
        };
        walker.addListener(listener);

        assertThat(getDurationToSeconds(walker)).isEqualTo(0);
        walker.walkTree();
        int counterAfterFirstWalk = counter.get();

        assertThat(counterAfterFirstWalk).isGreaterThan(0);
        System.out.printf("Time taken: %f\n", getDurationToSeconds(walker));

        deleteDirectoryRecursively(tempDir);

        walker.walkTree();
        int counterAfterSecondWalk = counter.get();
        assertThat(counterAfterSecondWalk).isGreaterThan(0);
        assertThat(counterAfterSecondWalk).isEqualTo(counterAfterFirstWalk * 2);

        assertThat(walker.getTimeTaken().getNano()).isNotEqualTo(0);
        System.out.printf("Time taken: %f\n", getDurationToSeconds(walker));
    }
}
