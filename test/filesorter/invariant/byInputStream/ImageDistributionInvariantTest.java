package filesorter.invariant.byInputStream;

import filesorter.CachedDirectoryTreeWalker;
import filesorter.DirectoryTreeWalker;
import filesorter.Result;
import filesorter.invariants.byInputStream.ImageDimensionDistribution;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
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

        imageDimensionDistribution.getPanoramicImages().forEach(filename -> System.out.println("Panoramic image: xdg-open '" + filename + "'"));

        double timeTakenSeconds = getDurationToSeconds(walker);
        int countOfFiles = walker.getCount();
        System.out.printf("Time taken: %f\n", timeTakenSeconds);
        System.out.printf("Files: %d\n", countOfFiles);
        System.out.printf("Time taken/file: %f\n", timeTakenSeconds / (double) countOfFiles);
    }

    @Test
    public void shouldFindPanoramicImages() throws IOException {

        //assumeThisUsesALotOfResources();

        shouldFindPanoramicImages("/mnt/ntfs/Photos 2020/");
    }

    private void shouldFindPanoramicImages(String path) throws IOException {
        DirectoryTreeWalker walker = new CachedDirectoryTreeWalker(new File(path));
        ImageDimensionDistribution imageDimensionDistribution = new ImageDimensionDistribution(walker);

        walker.walkTree();

        imageDimensionDistribution.getPanoramicImages().forEach(filename -> System.out.println("Panoramic image: xdg-open '" + filename + "'"));

        double timeTakenSeconds = getDurationToSeconds(walker);
        int countOfFiles = walker.getCount();
        System.out.printf("Time taken: %f\n", timeTakenSeconds);
        System.out.printf("Files: %d\n", countOfFiles);
        System.out.printf("Time taken/file: %f\n", timeTakenSeconds / (double) countOfFiles);
    }

    @Test
    public void shouldFilesThatNeedCompressing() throws IOException {

        //assumeThisUsesALotOfResources();

        shouldFilesThatNeedCompressing("/mnt/ntfs/Photos 2020/");
    }

    private void shouldFilesThatNeedCompressing(String path) throws IOException {
        DirectoryTreeWalker walker = new CachedDirectoryTreeWalker(new File(path));
        ImageDimensionDistribution imageDimensionDistribution = new ImageDimensionDistribution(walker);

        walker.walkTree();

        List<ImageDimensionDistribution.FileThatNeedsCompressing> listOfFilesThatNeedCompressing = imageDimensionDistribution.getListOfFilesThatNeedCompressing();
        System.out.println("Count of images that need compressing: " + listOfFilesThatNeedCompressing.size());
        listOfFilesThatNeedCompressing.forEach(fileThatNeedsCompressing ->
                System.out.println("Image that needs compressing: " + fileThatNeedsCompressing));

        double timeTakenSeconds = getDurationToSeconds(walker);
        int countOfFiles = walker.getCount();
        System.out.printf("Time taken: %f\n", timeTakenSeconds);
        System.out.printf("Files: %d\n", countOfFiles);
        System.out.printf("Time taken/file: %f\n", timeTakenSeconds / (double) countOfFiles);
    }
}
