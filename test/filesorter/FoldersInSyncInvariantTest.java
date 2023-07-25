package filesorter;


import filesorter.commands.Command;
import filesorter.invariants.Invariant;
import filesorter.invariants.byFilename.FoldersInSyncInvariant.FoldersInSyncInvariant;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static filesorter.DurationConverter.getDurationToSeconds;
import static filesorter.TemporaryDirectoryFileIOMethods.copyFolder;
import static filesorter.TemporaryDirectoryFileIOMethods.deleteDirectoryRecursively;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FoldersInSyncInvariantTest //extends AssumptionTestBase
{
    private static final String DIR = "/home/davidnorth/temp";

    private Path tempDir;

    @Before
    public void setUp() throws Exception {
        tempDir = Files.createTempDirectory("shouldSync");
        System.out.println(tempDir);

        File test = new File(DIR + "/test");
        copyFolder(test.toPath(), tempDir);
    }

    @After
    public void tearDown() throws Exception {
        deleteDirectoryRecursively(tempDir);
    }

    @Test
    public void shouldCheckIfMasterEqualsSlave() {

        FoldersInSyncInvariant foldersInSyncInvariant = new FoldersInSyncInvariant(DIR + "/test/master", DIR + "/test/slave");
        assertFalse(foldersInSyncInvariant.check().isOk());
    }

    @Test
    public void shouldCheckIfMasterEqualsMaster() {
        Invariant<List<Command>> foldersInSyncInvariant = new FoldersInSyncInvariant(DIR + "/test/master", DIR + "/test/master");
        System.out.println(foldersInSyncInvariant.check().value());
        assertTrue(foldersInSyncInvariant.check().isOk());
    }

    @Test
    public void shouldPreviewSync() throws IOException {
        shouldPreviewSync(DIR + "/test/master", DIR + "/test/slave");
    }
    @Test
    public void shouldPreviewSyncWithLargeTree() throws IOException {
        

        shouldPreviewSync("/mnt/ntfs/Photos 2020/", "/run/media/davidnorth/LaCie/Photos 2020/");
    }

    private void shouldPreviewSync(String masterDir, String slaveDir) throws IOException {
        File masterRoot = new File(masterDir);
        File slaveRoot = new File(slaveDir);
        DirectoryTreeWalker masterWalker = new DirectoryTreeWalker(masterRoot);
        DirectoryTreeWalker slaveWalker = new DirectoryTreeWalker(slaveRoot);

        FoldersInSyncInvariant foldersInSyncInvariant = new FoldersInSyncInvariant(masterRoot, masterWalker, slaveRoot, slaveWalker);
        Result<List<Command>> check = foldersInSyncInvariant.check();

        check.value().ifPresent(actions -> actions.forEach(action -> System.out.println(action)));

        double timeTakenSeconds = getDurationToSeconds(masterWalker);
        int countOfFiles = masterWalker.getCount();
        System.out.printf("Time taken: %f\n", timeTakenSeconds);
        System.out.printf("Files: %d\n", countOfFiles);
        System.out.printf("Time taken/file: %f\n", timeTakenSeconds / (double)countOfFiles);
    }

    @Test
    public void shouldExecuteSync() {
        File masterRoot = new File(tempDir + "/master");
        File slaveRoot = new File(tempDir + "/slave");
        DirectoryTreeWalker masterWalker = new CachedDirectoryTreeWalker(masterRoot);
        DirectoryTreeWalker slaveWalker = new DirectoryTreeWalker(slaveRoot);

        FoldersInSyncInvariant foldersInSyncInvariant = new FoldersInSyncInvariant(masterRoot, masterWalker, slaveRoot, slaveWalker);
        {
            Result<List<Command>> result = foldersInSyncInvariant.check();
            assertFalse(result.isOk());
            result.value().ifPresent(actions -> actions.forEach(System.out::println));
            result.value().ifPresent(actions -> actions.forEach(Command::execute));

            double timeTakenSeconds = getDurationToSeconds(masterWalker);
            int countOfFiles = masterWalker.getCount();
            System.out.printf("Time taken: %f\n", timeTakenSeconds);
            System.out.printf("Files: %d\n", countOfFiles);
            System.out.printf("Time taken/file: %f\n", timeTakenSeconds / (double)countOfFiles);
        }
        {
            Result<List<Command>> result = foldersInSyncInvariant.check();
            assertTrue(result.isOk());
            result.value().ifPresent(actions -> actions.forEach(System.out::println));

            double timeTakenSeconds = getDurationToSeconds(masterWalker);
            int countOfFiles = masterWalker.getCount();
            System.out.printf("Time taken: %f\n", timeTakenSeconds);
            System.out.printf("Files: %d\n", countOfFiles);
            System.out.printf("Time taken/file: %f\n", timeTakenSeconds / (double)countOfFiles);
        }
    }
}
