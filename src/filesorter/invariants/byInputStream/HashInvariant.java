package filesorter.invariants.byInputStream;

import filesorter.DirectoryTreeWalker;
import filesorter.OkResult;
import filesorter.Result;
import filesorter.invariants.Invariant;
import filesorter.invariants.Listeners.InputStreamListener;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HashInvariant implements Invariant<Map<String, byte[]>> {

    private final MessageDigest md = MessageDigest.getInstance("MD5");

    private final List<String> errors = new ArrayList<>();
    private final Map<String, byte[]> filenameToHash = new TreeMap<>();

    public HashInvariant(DirectoryTreeWalker walker) throws NoSuchAlgorithmException {

        walker.addListener(new InputStreamListener() {
            @Override
            public void notifyInputStream(Path file, InputStream inputStream) {

                md.reset();
                final DigestInputStream dis = new DigestInputStream(inputStream, md);
                String filename = file.toString();
                try
                {
                    while(-1 != dis.read())
                    {
                    }
                } catch (IOException e) {
                    errors.add("Failed to read from file. " + filename);
                } finally {
                    closeQuietly(filename, inputStream);
                    closeQuietly(filename, dis);
                }
                filenameToHash.put(file.toString(), md.digest());
            }
        });
    }

    private void closeQuietly(String filename, InputStream inputStream)
    {
        try
        {
            inputStream.close();
        } catch (IOException e) {
            errors.add("Failed to close stream. " + filename);
        }
    }

    @Override
    public Result<Map<String, byte[]>> check() {
        return new OkResult<>(filenameToHash);
    }

    public List<String> getErrors()
    {
        return errors;
    }
}
