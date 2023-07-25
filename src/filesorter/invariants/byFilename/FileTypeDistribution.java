package filesorter.invariants.byFilename;

import filesorter.DirectoryTreeWalker;
import filesorter.OkResult;
import filesorter.Result;
import filesorter.invariants.Invariant;


import java.util.HashMap;
import java.util.Map;

public class FileTypeDistribution implements Invariant<Map<String, Integer>>
{
    private final Map<String, Integer> extensionToCount = new HashMap<>();

    public FileTypeDistribution(DirectoryTreeWalker walker)
    {
        walker.addListener(file -> {
            String filename = file.getFileName().toString();
            int indexOfDot = filename.lastIndexOf(".");
            if(indexOfDot >= 0)
            {
                String lowerCaseExtension = filename.substring(indexOfDot).toLowerCase();
                extensionToCount.compute(lowerCaseExtension,
                        (k, v) -> null == v ? 1 : v + 1);
            }
        });
    }

    @Override
    public Result<Map<String, Integer>> check() {
        return new OkResult<>(extensionToCount);
    }
}
