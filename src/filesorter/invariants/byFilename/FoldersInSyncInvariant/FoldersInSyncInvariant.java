package filesorter.invariants.byFilename.FoldersInSyncInvariant;

import filesorter.DirectoryTreeWalker;
import filesorter.ErrorResult;
import filesorter.OkResult;
import filesorter.Result;
import filesorter.invariants.Invariant;
import filesorter.commands.Command;
import filesorter.commands.CopyFileCommand;
import filesorter.commands.DeleteFileCommand;
import filesorter.commands.ReplaceFileCommand;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FoldersInSyncInvariant implements Invariant<List<Command>> {
    private final List<SyncFile> masterFiles = new ArrayList<>();
    private final List<SyncFile> slaveFiles = new ArrayList<>();

    private final DirectoryTreeWalker masterWalker;
    private final DirectoryTreeWalker slaveWalker;

    private final List<Command> commands = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();
    private final File masterRootFile;
    private final File slaveRootFile;

    @Deprecated
    public FoldersInSyncInvariant(String masterRoot, String slaveRoot)
    {
        masterRootFile = new File(masterRoot);
        slaveRootFile = new File(slaveRoot);

        masterWalker = new DirectoryTreeWalker(masterRootFile);
        slaveWalker = new DirectoryTreeWalker(slaveRootFile);

        addListeners();
    }

    public FoldersInSyncInvariant(File masterRoot, DirectoryTreeWalker masterWalker, File slaveRoot, DirectoryTreeWalker slaveWalker)
    {
        this.masterRootFile = masterRoot;
        this.slaveRootFile = slaveRoot;

        this.masterWalker = masterWalker;
        this.slaveWalker = slaveWalker;

        addListeners();
    }

    private void addListeners() {
        masterWalker.addListener(new CRC64InputStreamListener(masterRootFile, masterFiles, errors::add));
        slaveWalker.addListener(new CRC64InputStreamListener(slaveRootFile, slaveFiles, errors::add));
    }

    public Result<List<Command>> check() {

        errors.clear();
        commands.clear();

        /*if(masterFiles.isEmpty())*/ {
            try {
                masterFiles.clear();
                masterWalker.walkTree();
            } catch (IOException ex) {
                return new ErrorResult<>("Failed to walk master directory.", null);
            }
        }

        try {
            slaveFiles.clear();
            slaveWalker.walkTree();
        }
        catch(IOException ex)
        {
            return new ErrorResult<>("Failed to walk slave directory.", null);
        }

        masterFiles.forEach(masterFile -> {
            boolean found = false;
            boolean hashEquals = false;

            for (SyncFile slaveFile : slaveFiles) {
                if (masterFile.contentEquals(slaveFile)) {
                    found = true;
                    if(masterFile.hashEquals(slaveFile))
                    {
                        hashEquals = true;
                    }
                    break;
                }
            }

            String masterFilename = masterRootFile + masterFile.fileNoPath;
            String slaveFilename = slaveRootFile + masterFile.fileNoPath;
            if (found) {
                if(!hashEquals) {
                    commands.add(new ReplaceFileCommand(masterFilename, slaveFilename));
                }
                else
                {
                    // Do nothing. Already matching on slave
                }
            }
            else
            {
                commands.add(new CopyFileCommand(masterFilename, slaveFilename));
            }
        });

        slaveFiles.forEach(slaveFile ->
        {
            boolean found = false;
            for (SyncFile masterFile : masterFiles) {
                if (slaveFile.contentEquals(masterFile)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                String slaveFilename = slaveRootFile + slaveFile.fileNoPath;
                commands.add(new DeleteFileCommand(slaveFilename));
            }
        });

        if(commands.isEmpty())
        {
            return new OkResult<>();
        }
        else
        {
            return new ErrorResult("Master and slave not equal.", commands);
        }
    }

}
