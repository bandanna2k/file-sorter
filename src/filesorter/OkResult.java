package filesorter;

public class OkResult<T> extends Result<T>
{
    public OkResult()
    {
        super(null, null);
    }

    public OkResult(T value)
    {
        super(null, value);
    }
}
