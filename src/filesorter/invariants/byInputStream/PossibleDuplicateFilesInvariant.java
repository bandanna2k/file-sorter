package filesorter.invariants.byInputStream;

import filesorter.DirectoryTreeWalker;
import filesorter.OkResult;
import filesorter.Result;
import filesorter.invariants.Invariant;
import filesorter.invariants.Listeners.InputStreamListener;
import hashing.CRC64;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Collections.singletonList;

public class PossibleDuplicateFilesInvariant implements Invariant<Map<Long, List<String>>> {
    private final List<String> errors = new ArrayList<>();
    private final Map<Long, List<String>> crc64ToListOfFiles = new TreeMap<>();

    public PossibleDuplicateFilesInvariant(DirectoryTreeWalker walker) {
        walker.addListener(new InputStreamListener() {
            @Override
            public void notifyInputStream(Path file, InputStream inputStream) {
                String filename = file.toString();
                try {
                    CRC64 crc64 = CRC64.fromInputStream(inputStream);
                    crc64ToListOfFiles.compute(crc64.getValue(), (crc, listOfFilenames) -> {
                        if (listOfFilenames == null) {
                            return new ArrayList<>(singletonList(filename));
                        }
                        else {
                            listOfFilenames.add(filename);
                            return listOfFilenames;
                        }
                    });
                } catch (IOException e) {
                    errors.add("Failed to get CRC from file. " + filename);
                }
            }
        });
    }

    public List<String> getErrors() {
        return errors;
    }

    @Override
    public Result<Map<Long, List<String>>> check() {
        return new OkResult<>(crc64ToListOfFiles);
    }
}
