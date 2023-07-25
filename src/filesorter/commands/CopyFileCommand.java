package filesorter.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class CopyFileCommand implements Command {
    private final String file1;
    private final String file2;
    private String error;

    public CopyFileCommand(String file1, String file2) {
        this.file1 = file1;
        this.file2 = file2;
    }

    @Override
    public void execute() {
        try {
            System.out.printf("Copying file '%s' to file '%s'. ", file1, file2);
            Files.createDirectories(Path.of(file2).getParent());
            Files.copy(Path.of(file1), Path.of(file2), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Done");
        } catch (IOException e) {
            error = String.format("Failed to copy file. Source: %s, Dest: %s", file1, file2);
        }
    }

    @Override
    public String toString() {
        return "CopyFileCommand{" +
                "file1='" + file1 + '\'' +
                ", file2='" + file2 + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}
