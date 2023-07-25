package filesorter.invariants.Listeners;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class ImageDimensionsListener extends InputStreamListener
{
    private final List<String> errors = new ArrayList<>();

    @Override
    public void notifyInputStream(Path path, InputStream inputStream)
    {
        File file = path.toFile();
        try {
            int pos = file.getName().lastIndexOf(".");
            if (pos == -1)
                throw new IOException("No extension for file.");
            String suffix = file.getName().substring(pos + 1);
            Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
            while (iter.hasNext()) {
                ImageReader reader = iter.next();
                try {
                    FileImageInputStream stream = new FileImageInputStream(file);
                    reader.setInput(stream);
                    int width = reader.getWidth(reader.getMinIndex());
                    int height = reader.getHeight(reader.getMinIndex());
                    notifyImageDimensions(path, width, height);

                } catch (IOException e) {
                    throw new IOException("Could not read dimensions.", e);
                } finally {
                    reader.dispose();
                }
            }
        } catch (IOException e) {
            errors.add(String.format("Error get image dimensions. File: %s, Error: %s", file.getAbsolutePath(), e.getMessage()));
        }
    }

    protected abstract void notifyImageDimensions(Path path, int width, int height);

    public List<String> getErrors() {
        return errors;
    }
}
