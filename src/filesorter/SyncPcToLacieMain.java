package filesorter;

import filesorter.invariants.byFilename.FoldersInSyncInvariant.FoldersInSyncInvariant;
import filesorter.invariants.byInputStream.PossibleDuplicateFilesInvariant;
import filesorter.commands.Command;

import java.io.File;
import java.util.List;
import java.util.Map;

import static filesorter.DurationConverter.getDurationToSeconds;

public class SyncPcToLacieMain
{
    public static void main(String[] args) {
        new SyncPcToLacieMain().go();
    }

    private void go()
    {
        try {
            sync("/mnt/ntfs/Photos 2020/Rich", "/run/media/davidnorth/LaCie/Photos 2020/Rich");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sync(String masterDir, String slaveDir) {

        File masterRoot = new File(masterDir);
        File slaveRoot = new File(slaveDir);
        DirectoryTreeWalker masterWalker = new CachedDirectoryTreeWalker(masterRoot);
        DirectoryTreeWalker slaveWalker = new DirectoryTreeWalker(slaveRoot);

//        masterWalker.addListener(file -> System.out.println("Processing master: " + file));
//        slaveWalker.addListener(file -> System.out.println("Processing slave: " + file));
        masterWalker.addListener(file -> System.out.print("."));
        slaveWalker.addListener(file -> System.out.print(","));

        FoldersInSyncInvariant foldersInSyncInvariant = new FoldersInSyncInvariant(masterRoot, masterWalker, slaveRoot, slaveWalker);

        // Check folders out of sync + fix
        {
            Result<List<Command>> check = foldersInSyncInvariant.check();

            // Copy master -> slave
            check.value().ifPresent(actions -> actions.forEach(System.out::println));
            check.value().ifPresent(v -> v.forEach(Command::execute));

            double timeTakenSeconds = getDurationToSeconds(masterWalker);
            int countOfFiles = masterWalker.getCount();
            System.out.println("Action count: " + check.value());
            System.out.printf("Time taken: %f\n", timeTakenSeconds);
            System.out.printf("Files: %d\n", countOfFiles);
            System.out.printf("Time taken/file: %f\n", timeTakenSeconds / (double)countOfFiles);
        }

        // Add other filesorter.invariants
        PossibleDuplicateFilesInvariant possibleDuplicateFilesInvariant = new PossibleDuplicateFilesInvariant(masterWalker);

        // Check folders in sync
        {
            Result<List<Command>> check = foldersInSyncInvariant.check();
            check.value().ifPresent(actions -> actions.forEach(System.out::println));

            double timeTakenSeconds = getDurationToSeconds(masterWalker);
            int countOfFiles = masterWalker.getCount();
            System.out.println("Action count: " + check.value());
            System.out.printf("Time taken: %f\n", timeTakenSeconds);
            System.out.printf("Files: %d\n", countOfFiles);
            System.out.printf("Time taken/file: %f\n", timeTakenSeconds / (double)countOfFiles);
        }

        {
            Result<Map<Long, List<String>>> check = possibleDuplicateFilesInvariant.check();

            System.out.println("Possible Duplicates");
            check.value().ifPresent(actions -> actions
                    .forEach((hash, filenames) -> {
                        if(filenames.size() > 1)
                        {
                            System.out.println("Possible duplicates: " + filenames);
                        }
                    }));
        }
    }
}
