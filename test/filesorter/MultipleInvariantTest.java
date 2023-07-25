package filesorter;

import filesorter.invariants.byFilename.DirectoriesWithMissingMonths;
import filesorter.invariants.byFilename.FileTypeDistribution;
import filesorter.invariants.byInputStream.PossibleDuplicateFilesInvariant;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static filesorter.DurationConverter.getDurationToSeconds;

public class MultipleInvariantTest //extends AssumptionTestBase
{

    private static final String DIR = "/home/davidnorth/temp";

    @Test
    public void shouldWalkWithMultipleInvariants() throws IOException, NoSuchAlgorithmException {
        shouldWalkWithMultipleInvariants(DIR + "/test/master");
    }
    @Test
    public void shouldWalkWithMultipleInvariantsWithLargeTree() throws IOException, NoSuchAlgorithmException {
        

        shouldWalkWithMultipleInvariants("/mnt/ntfs/Photos 2020/Rich");
    }
    private void shouldWalkWithMultipleInvariants(String masterDir) throws IOException {

        DirectoryTreeWalker master = new CachedDirectoryTreeWalker(new File(masterDir));

        DirectoriesWithMissingMonths missingMonths = new DirectoriesWithMissingMonths(master);
        PossibleDuplicateFilesInvariant possibleDuplicateFilesInvariant = new PossibleDuplicateFilesInvariant(master);
        FileTypeDistribution fileTypeDistribution = new FileTypeDistribution(master);

        master.walkTree();

        {
            System.out.println("Missing Months");
            Result<Collection<String>> check = missingMonths.check();
            check.value().ifPresent(v -> v.forEach(System.out::println));
        }
        {
            System.out.println("File Type Distribution");
            Result<Map<String, Integer>> check = fileTypeDistribution.check();
            check.value().ifPresent(map -> map.forEach((key, value) -> System.out.println(key + ": " + value)));
        }
        {
            System.out.println("Possible Duplicates");
            Result<Map<Long, List<String>>> check = possibleDuplicateFilesInvariant.check();
            check.value().ifPresent(map -> map.forEach((key, value) -> System.out.println(key + ": " + value)));
        }

        double timeTakenSeconds = getDurationToSeconds(master);
        int countOfFiles = master.getCount();
        System.out.printf("Time taken: %f\n", timeTakenSeconds);
        System.out.printf("Files: %d\n", countOfFiles);
        System.out.printf("Time taken/file: %f\n", timeTakenSeconds / (double)countOfFiles);
    }
}
