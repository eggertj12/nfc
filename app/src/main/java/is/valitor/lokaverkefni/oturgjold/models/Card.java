package is.valitor.lokaverkefni.oturgjold.models;

/**
 * Created by dasve_000 on 3/13/2015.
 */
public class Card {

    private int card_id;

    private String card_name;

    private String last_four;

    private String tokenized_card_number;

    private String tokenized_validation;

    private String tokenized_cvv;

    public int getCard_id() {
        return card_id;
    }

    public void setCard_id(int card_id) {
        this.card_id = card_id;
    }

    public String getCard_name() {
        return card_name;
    }

    public void setCard_name(String card_name) {
        this.card_name = card_name;
    }

    public String getLast_four() {
        return last_four;
    }

    public void setLast_four(String last_four) {
        this.last_four = last_four;
    }

    public String getTokenized_card_number() {
        return tokenized_card_number;
    }

    public void setTokenized_card_number(String tokenized_card_number) {
        this.tokenized_card_number = tokenized_card_number;
    }

    public String getTokenized_validation() {
        return tokenized_validation;
    }

    public void setTokenized_validation(String tokenized_validation) {
        this.tokenized_validation = tokenized_validation;
    }

    public String getTokenized_cvv() {
        return tokenized_cvv;
    }

    public void setTokenized_cvv(String tokenized_cvv) {
        this.tokenized_cvv = tokenized_cvv;
    }
}
