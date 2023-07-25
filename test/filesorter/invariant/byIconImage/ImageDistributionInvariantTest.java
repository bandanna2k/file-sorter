package filesorter.invariant.byIconImage;

import filesorter.CachedDirectoryTreeWalker;
import filesorter.DirectoryTreeWalker;
import filesorter.Result;
import filesorter.invariants.byIconImage.ImageDimensionDistribution;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static filesorter.DurationConverter.getDurationToSeconds;

public class ImageDistributionInvariantTest //extends AssumptionTestBase {
{
    private static final String DIR = "/home/davidnorth/temp";

    @Test
    public void shouldCheckImageDimensionDistribution() throws IOException {
        shouldCheckImageDimensionDistribution(DIR + "/test/master");
    }
    @Test
    public void shouldCheckImageDimensionDistributionWithLargeTree() throws IOException {
        //assumeThisUsesALotOfResources();

        shouldCheckImageDimensionDistribution("/mnt/ntfs/Photos 2020/");
    }
    private void shouldCheckImageDimensionDistribution(String masterDir) throws IOException {

        DirectoryTreeWalker walker = new CachedDirectoryTreeWalker(new File(masterDir));
        ImageDimensionDistribution imageDimensionDistribution = new ImageDimensionDistribution(walker);

        walker.walkTree();

        Result<Map<String, Integer>> check = imageDimensionDistribution.check();
        imageDimensionDistribution.getErrors().forEach(System.out::println);
        check.value().ifPresent(map -> map.forEach((key, value) -> System.out.println(key + ": " + value)));

        System.out.printf("Time taken: %f\n", getDurationToSeconds(walker));
    }
}
