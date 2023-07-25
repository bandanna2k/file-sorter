package filesorter;

import java.util.Optional;

public class Result<T>
{
    private final String error;
    private final T value;


    protected Result(String error, T value)
    {
        this.error = error;
        this.value = value;
    }

    public static Result ok()
    {
        return new Result(null, null);
    }

    public static Result error(final String error)
    {
        return new Result(error, null);
    }

    public boolean isOk() {
        return null == error;
    }

    public Optional<T> value() {
        return Optional.ofNullable(value);
    }

    @Override
    public String toString() {
        return "Result{" +
                "error='" + error + '\'' +
                '}';
    }
}
