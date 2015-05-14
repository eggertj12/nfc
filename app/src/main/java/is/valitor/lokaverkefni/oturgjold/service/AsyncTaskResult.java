package is.valitor.lokaverkefni.oturgjold.service;

/**
 * For unified handling of results from AsyncTasks
 * Created by eggert on 21/03/15.
 */

public class AsyncTaskResult<T> {
    private T result;
    private Exception error = null;

    /**
     * One contructor for carrying successful result
     *
     * @param result The received result
     */
    public AsyncTaskResult(T result) {
        super();
        this.result = result;
    }

    /**
     * Another for carrying an exception
     *
     * @param error The caught exception
     */
    public AsyncTaskResult(Exception error) {
        super();
        this.error = error;
    }

    public T getResult() {
        return result;
    }

    public Exception getError() {
        return error;
    }
}