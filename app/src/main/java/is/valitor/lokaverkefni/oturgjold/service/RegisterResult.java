package is.valitor.lokaverkefni.oturgjold.service;

/**
 * Created by kla on 8.5.2015.
 */
public class RegisterResult {

    private int resultCode;
    private String resultMessage;
    private String resultContent;

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
}
