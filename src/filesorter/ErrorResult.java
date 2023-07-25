package filesorter;

public class ErrorResult<T> extends Result<T>
{
    public ErrorResult(String error, T value)
    {
        super(error, value);
    }
}
