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

public class CRC64Invariant implements Invariant<Map<String, Long>> {

    private final List<String> errors = new ArrayList<>();
    private final Map<String, Long> filenameToCRC = new TreeMap<>();

    public CRC64Invariant(DirectoryTreeWalker walker) {

        walker.addListener(new InputStreamListener() {
            @Override
            public void notifyInputStream(Path file, InputStream inputStream) {

                try {
                    CRC64 crc64 = CRC64.fromInputStream(inputStream);
                    filenameToCRC.put(file.toString(), crc64.getValue());
                } catch (IOException e) {
                    errors.add("Failed to find CRC64. File: " + file.toString());
                }
            }
        });
    }

    @Override
    public Result<Map<String, Long>> check() {
        return new OkResult<>(filenameToCRC);
    }

    public List<String> getErrors() {
        return errors;
    }
}
