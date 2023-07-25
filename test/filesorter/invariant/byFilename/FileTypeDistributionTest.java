package filesorter.invariant.byFilename;


import filesorter.DirectoryTreeWalker;
import filesorter.Result;
import filesorter.invariants.byFilename.FileTypeDistribution;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class FileTypeDistributionTest //extends AssumptionTestBase
{
    private static final String DIR = "/home/davidnorth/temp";

    @Test
    public void shouldCheckFileDistribution() throws IOException {
        String masterDir = DIR + "/test/master";
        DirectoryTreeWalker walker = new DirectoryTreeWalker(new File(masterDir));
        FileTypeDistribution invariant = new FileTypeDistribution(walker);
        walker.walkTree();

        Result<Map<String, Integer>> check = invariant.check();
        assertTrue(check.isOk());

        check.value().ifPresent(map -> {
            map.forEach((key, value) -> System.out.println(key + ": " + value));
        });
    }

    @Test
    public void shouldCheckFileDistributionOfLargeFolder() throws IOException {
        

        String masterDir = "/mnt/ntfs/Photos 2020/";
        DirectoryTreeWalker walker = new DirectoryTreeWalker(new File(masterDir));
        FileTypeDistribution invariant = new FileTypeDistribution(walker);
        walker.walkTree();

        Result<Map<String, Integer>> check = invariant.check();
        assertTrue(check.isOk());

        check.value().ifPresent(map -> {
            map.forEach((key, value) -> System.out.println(key + ": " + value));
        });
    }
}
