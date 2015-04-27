package is.valitor.lokaverkefni.oturgjold.repository;

import java.util.Date;

/**
 * Created by kla on 26.4.2015.
 */
public class Transactions {

    private int card_id;
    private Date date;
    private String vendor;
    private String price;

    public int getCard_id() {
        return card_id;
    }

    public void setCard_id(int card_id) {
        this.card_id = card_id;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

}
