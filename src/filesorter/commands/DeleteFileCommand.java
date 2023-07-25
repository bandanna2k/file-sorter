package filesorter.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DeleteFileCommand implements Command {
    private final String filename;
    private String error;

    public DeleteFileCommand(String filename) {

        this.filename = filename;
    }

    @Override
    public String toString() {
        return "DeleteFileCommand{" +
                "filename='" + filename + '\'' +
                ", error='" + error + '\'' +
                '}';
    }

    @Override
    public void execute()
    {
        try {
            Files.delete(Path.of(filename));
            System.out.print("File deleted: " + filename + " ");
            System.out.println("Done.");
        } catch (IOException e) {
            System.out.println("Failed.");
            error = String.format("Failed to delete file. File: %s, Error: %s", filename, e.getMessage());
        }
    }
}
