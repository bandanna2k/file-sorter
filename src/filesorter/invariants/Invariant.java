package filesorter.invariants;

import filesorter.Result;

public interface Invariant<T>
{
    Result<T> check();
}
