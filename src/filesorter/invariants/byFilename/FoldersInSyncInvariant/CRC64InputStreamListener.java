package filesorter.invariants.byFilename.FoldersInSyncInvariant;

import filesorter.invariants.Listeners.InputStreamListener;
import hashing.CRC64;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public class CRC64InputStreamListener extends InputStreamListener
{
    private List<SyncFile> files;
    private File rootFile;
    private Consumer<String> errorCallback;

    public CRC64InputStreamListener(File rootFile, List<SyncFile> files, Consumer<String> errorCallback) {
        this.files = files;
        this.rootFile = rootFile;
        this.errorCallback = errorCallback;
    }

    @Override
    public void notifyInputStream(Path file, InputStream inputStream) {
        final String fileNoPath = file.toString().replace(rootFile.toString(), "");

        long hash = 0L;
        CRC64 crc64;
        try {
            crc64 = CRC64.fromInputStream(inputStream);
            hash = crc64.getValue();
        } catch (IOException e) {
            errorCallback.accept("Failed to get hash from file. " + file);
        }

        files.add(new SyncFile(fileNoPath, hash));
    }
}
