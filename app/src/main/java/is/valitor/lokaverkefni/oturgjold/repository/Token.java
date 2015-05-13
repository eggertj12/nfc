package is.valitor.lokaverkefni.oturgjold.repository;

/**
 * POJO for credit card tokens
 * Created by kla on 12.3.2015.
 */
public class Token {

    private int usr_id;
    private String device_id;
    private int card_id;
    private String tokenitem;

    public int getCard_id() {
        return card_id;
    }

    public void setCard_id(int card_id) {
        this.card_id = card_id;
    }


    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }


    public int getUsr_id() {
        return usr_id;
    }

    public void setUsr_id(int usr_id) {
        this.usr_id = usr_id;
    }

    public String getTokenitem() {
        return tokenitem;
    }

    public void setTokenitem(String tokenitem) {
        this.tokenitem = tokenitem;
    }
}
