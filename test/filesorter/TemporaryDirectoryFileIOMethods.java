package filesorter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class TemporaryDirectoryFileIOMethods
{
    public static void copyFolder(Path src, Path dest) {
        try {
            Files.walk(src).forEach(s -> {
                try {
                    Path d = dest.resolve(src.relativize(s));
                    if (Files.isDirectory(s)) {
                        if (!Files.exists(d))
                            Files.createDirectory(d);
                        return;
                    }
                    Files.copy(s, d);// use flag to override existing
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void deleteDirectoryRecursively(Path dir) throws IOException {
        if (dir.toFile().exists()) {
            Files.walk(dir)
                    .map(Path::toFile)
                    .sorted((o1, o2) -> -o1.compareTo(o2))
                    .forEach(File::delete);
        }
    }
}
