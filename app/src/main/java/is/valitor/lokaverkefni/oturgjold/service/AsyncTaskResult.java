package is.valitor.lokaverkefni.oturgjold.service;

/**
 * Created by eggert on 21/03/15.
 */
public class AsyncTaskResult<T> {
    private T result;
    private Exception error = null;

    public T getResult() {
        return result;
    }
    public Exception getError() {
        return error;
    }


    public AsyncTaskResult(T result) {
        super();
        this.result = result;
    }


    public AsyncTaskResult(Exception error) {
        super();
        this.error = error;
    }
}