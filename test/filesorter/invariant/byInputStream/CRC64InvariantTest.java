package filesorter.invariant.byInputStream;

import filesorter.CachedDirectoryTreeWalker;
import filesorter.DirectoryTreeWalker;
import filesorter.Result;
import filesorter.invariants.byInputStream.CRC64Invariant;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static filesorter.DurationConverter.getDurationToSeconds;

public class CRC64InvariantTest //extends AssumptionTestBase
{

    private static final String DIR = "/home/davidnorth/temp";

    @Test
    public void shouldInvariant() throws IOException {
        shouldInvariant(DIR + "/test/master");
    }
    @Test
    public void shouldInvariantWithLargeTree() throws IOException
    {
        //assumeThisUsesALotOfResources();

        shouldInvariant("/mnt/ntfs/Photos 2020/Rich");
    }
    private void shouldInvariant(String masterDir) throws IOException {

        DirectoryTreeWalker walker = new CachedDirectoryTreeWalker(new File(masterDir));
        CRC64Invariant invariant = new CRC64Invariant(walker);

        walker.walkTree();

        Result<Map<String, Long>> check = invariant.check();
        invariant.getErrors().forEach(System.out::println);
        check.value().ifPresent(map -> map.forEach((key, value) -> System.out.println(key + ": " + value)));

        double timeTakenSeconds = getDurationToSeconds(walker);
        int countOfFiles = walker.getCount();
        System.out.printf("Time taken: %f\n", timeTakenSeconds);
        System.out.printf("Files: %d\n", countOfFiles);
        System.out.printf("Time taken/file: %f\n", timeTakenSeconds / (double)countOfFiles);
    }
}
