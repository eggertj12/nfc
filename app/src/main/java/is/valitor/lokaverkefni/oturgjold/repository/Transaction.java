package is.valitor.lokaverkefni.oturgjold.repository;

import java.util.Date;

/**
 * POJO for payment transactions.
 * Created by kla on 26.4.2015.
 */
public class Transaction {

    private int card_id;
    private Date date;
    private String vendor;
    private int price;

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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

}
