package filesorter.invariants.byFilename;

import filesorter.DirectoryTreeWalker;
import filesorter.ErrorResult;
import filesorter.Result;
import filesorter.invariants.Invariant;


import java.io.File;
import java.nio.file.Path;
import java.util.*;

public class DirectoriesWithMissingMonths implements Invariant<Collection<String>> {

    private static final String[] MONTHS = new String[]
            {
                    "January", "February", "March", "April", "May", "June",
                    "July", "August", "September", "October", "November", "December"
            };

    private final Map<String, List<String>> directoriesToMonths = new HashMap<>();

    public DirectoriesWithMissingMonths(DirectoryTreeWalker walker)
    {
        walker.addListener(file -> {
            Path dirOfFile = file.getParent();
            Optional<String> matchedMonth = Arrays.stream(MONTHS).filter(month -> dirOfFile.getFileName().toString().endsWith(month)).findAny();
            if(matchedMonth.isPresent())
            {
                String dirOfDirectory = dirOfFile.getParent().toString();
                if(!directoriesToMonths.containsKey(dirOfDirectory))
                {
                    directoriesToMonths.put(dirOfDirectory, new ArrayList<>(Arrays.asList(MONTHS.clone())));
                }
                directoriesToMonths.get(dirOfDirectory).removeIf(month -> month.contentEquals(matchedMonth.get()));
            }
        });
    }

    @Override
    public Result<Collection<String>> check()
    {
        Set<String> missingMonths = new TreeSet<>();

        directoriesToMonths.forEach((dirOfDirectory, months) -> {
            if(months.size() > 0) {
                months.forEach(month -> {
                    missingMonths.add(dirOfDirectory + File.separator + month);
                });
            }
        });

        return missingMonths.isEmpty() ? Result.ok() : new ErrorResult<>("Found missing months.", missingMonths);
    }
}
