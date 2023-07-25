package filesorter.invariants.byInputStream;

import filesorter.DirectoryTreeWalker;
import filesorter.OkResult;
import filesorter.Result;

import filesorter.invariants.Invariant;
import filesorter.invariants.Listeners.ImageDimensionsListener;


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageDimensionDistribution implements Invariant<Map<String, Integer>> {

    private final Map<String, Integer> dimensionStringToCount = new HashMap<>();
    private final List<String> listOfPanoramicImages = new ArrayList<>();
    private final List<FileThatNeedsCompressing> listOfFilesThatNeedCompressing = new ArrayList<>();

    private final ImageDimensionsListener imageDimensionsListener;

    public ImageDimensionDistribution(DirectoryTreeWalker walker) {
        imageDimensionsListener = new ImageDimensionsListener() {
            @Override
            protected void notifyImageDimensions(Path path, int width, int height) {
                addImageToDistributionStats(path, width, height);
                checkImageIsPanoramic(path, width, height);
                checkImageNeedsCompressing(path, width, height);
            }
        };
        walker.addListener(imageDimensionsListener);
    }

    private void checkImageNeedsCompressing(Path path, int width, int height)
    {
        if(!path.toString().toLowerCase().endsWith(".jpg")) return;

        long sizeInBytes = path.toFile().length();
        if(sizeInBytes <= 150_000) return;

        // pixels/byte = 6.663321
        // Typical photo  3265 x 4898 (15'991'970) = 2.4Mb = 2'400'000
        // @ 4MB = 4.0 pixels / byte
        double pixelsPerByte = (double)width * (double)height / (double)sizeInBytes;
        if(pixelsPerByte < 4.0)
        {
            listOfFilesThatNeedCompressing.add(new FileThatNeedsCompressing(path, pixelsPerByte));
        }
    }

    private void checkImageIsPanoramic(Path path, int width, int height)
    {
        final double ratio = (double)width / (double)height;
        if(ratio > 3.0)
        {
            listOfPanoramicImages.add(path.toString());
        }
    }

    private void addImageToDistributionStats(Path path, int width, int height) {
        final String dimensionString = String.format("%d x %d", width, height);
//        if (width == -1 || height == -1) {
//        } else {
//            System.out.printf("Dimensions: %s, File: %s\n", dimensionString, path);
//        }
        dimensionStringToCount.compute(dimensionString,
                (k, v) -> null == v ? 1 : v + 1);
    }

    public List<String> getErrors() {
        return imageDimensionsListener.getErrors();
    }

    @Override
    public Result<Map<String, Integer>> check() {
        return new OkResult<>(dimensionStringToCount);
    }

    public List<String> getPanoramicImages() {
        return listOfPanoramicImages;
    }

    public List<FileThatNeedsCompressing> getListOfFilesThatNeedCompressing() {
        return listOfFilesThatNeedCompressing;
    }

    public class FileThatNeedsCompressing
    {
        private final Path path;
        private final double pixelsPerByte;

        public FileThatNeedsCompressing(Path path, double pixelsPerByte) {
            this.path = path;
            this.pixelsPerByte = pixelsPerByte;
        }

        @Override
        public String toString() {
            return "FileThatNeedsCompressing{" +
                    "path=xdg-open '" + path +
                    "', pixelsPerByte=" + pixelsPerByte +
                    '}';
        }
    }
}
