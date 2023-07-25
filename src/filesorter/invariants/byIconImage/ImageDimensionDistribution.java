package filesorter.invariants.byIconImage;

import filesorter.DirectoryTreeWalker;
import filesorter.OkResult;
import filesorter.Result;
import filesorter.invariants.Invariant;


import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class ImageDimensionDistribution implements Invariant<Map<String, Integer>>
{
    private final Map<String, Integer> dimensionStringToCount = new HashMap<>();
    private final List<String> errors = new ArrayList<>();

    public ImageDimensionDistribution(DirectoryTreeWalker walker)
    {
        walker.addListener(file -> {
            ImageIcon image = new ImageIcon(file.toString());
            int width = image.getIconWidth();
            int height = image.getIconHeight();
            final String dimensionString = String.format("%d x %d", width, height);
            if(width == -1 || height == -1)
            {
            }
            else
            {
                System.out.printf("Dimensions: %s, File: %s\n", dimensionString, file);
            }
            dimensionStringToCount.compute(dimensionString,
                    (k, v) -> null == v ? 1 : v + 1);
        });
    }

    public List<String> getErrors()
    {
        return errors;
    }

    @Override
    public Result<Map<String, Integer>> check()
    {
        return new OkResult<>(dimensionStringToCount);
    }
}
