package is.valitor.lokaverkefni.oturgjold.service;

/**
 * A POJO for passing the raw result of network request from RequestTask to its subclasses
 * <p/>
 * Created by kla on 8.5.2015.
 */
public class RequestResult {

    private int resultCode;
    private String resultMessage;
    private String resultContent;
    private Exception exception = null;

    public RequestResult() {
    }

    public RequestResult(Exception e) {
        this.exception = e;
    }

    public String getResultContent() {
        return resultContent;
    }

    public void setResultContent(String resultContent) {
        this.resultContent = resultContent;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception e) {
        this.exception = e;
    }
}
