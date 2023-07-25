package filesorter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

public abstract class DurationConverter
{
    public static double getDurationToSeconds(DirectoryTreeWalker walker) {
        Duration timeTaken = walker.getTimeTaken();
        BigDecimal nanosTaken = new BigDecimal(timeTaken.getNano());
        BigDecimal convertToSeconds = new BigDecimal("1000000000.0");
        return timeTaken.getSeconds() + nanosTaken.divide(convertToSeconds, 6, RoundingMode.UP).doubleValue();
    }
}
