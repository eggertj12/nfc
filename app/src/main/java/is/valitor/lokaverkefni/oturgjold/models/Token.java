package is.valitor.lokaverkefni.oturgjold.models;

/**
 * Created by kla on 12.3.2015.
 */
public class Token {

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }



    public String getUsr_id() {
        return usr_id;
    }

    public void setUsr_id(String usr_id) {
        this.usr_id = usr_id;
    }

    private String usr_id;
    private String device_id;
    private String token;


}
