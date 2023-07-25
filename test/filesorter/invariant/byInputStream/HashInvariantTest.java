package filesorter.invariant.byInputStream;

import filesorter.CachedDirectoryTreeWalker;
import filesorter.DirectoryTreeWalker;
import filesorter.Result;
import filesorter.invariants.byInputStream.HashInvariant;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;

import static filesorter.DurationConverter.getDurationToSeconds;

public class HashInvariantTest //extends AssumptionTestBase {
{
    private static final String DIR = "/home/davidnorth/temp";

    @Test
    public void shouldInvariant() throws IOException, NoSuchAlgorithmException {
        shouldInvariant(DIR + "/test/master");
    }
    @Test
    public void shouldInvariantWithLargeTree() throws IOException, NoSuchAlgorithmException {
        //assumeThisUsesALotOfResources();

        shouldInvariant("/mnt/ntfs/Photos 2020/Rich");
    }
    private void shouldInvariant(String masterDir) throws IOException, NoSuchAlgorithmException {

        DirectoryTreeWalker walker = new CachedDirectoryTreeWalker(new File(masterDir));
        HashInvariant invariant = new HashInvariant(walker);

        walker.walkTree();

        Result<Map<String, byte[]>> check = invariant.check();
        invariant.getErrors().forEach(System.out::println);
        Base64.Encoder encoder = Base64.getEncoder();
        check.value().ifPresent(map -> map.forEach((key, value) -> {
            System.out.println(key + ": " + encoder.encodeToString(value));
        }));

        double timeTakenSeconds = getDurationToSeconds(walker);
        int countOfFiles = walker.getCount();
        System.out.printf("Time taken: %f\n", timeTakenSeconds);
        System.out.printf("Files: %d\n", countOfFiles);
        System.out.printf("Time taken/file: %f\n", timeTakenSeconds / (double)countOfFiles);
    }
}
