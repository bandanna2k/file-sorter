package filesorter.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ReplaceFileCommand implements Command {
    private final String file1;
    private final String file2;
    private String error;

    public ReplaceFileCommand(String file1, String file2) {
        this.file1 = file1;
        this.file2 = file2;
    }

    @Override
    public void execute() {
        try {
            System.out.printf("Replacing file '%s' with file '%s'. ", file1, file2);
            Files.copy(Path.of(file1), Path.of(file2), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Done.");
        } catch (IOException e) {
            System.out.println("Failed.");
            error = String.format("Failed to copy file. Source: %s, Dest: %s", file1, file2);
        }
    }

    @Override
    public String toString() {
        return "ReplaceFileCommand{" +
                "file1='" + file1 + '\'' +
                ", file2='" + file2 + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}
