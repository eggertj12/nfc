package is.valitor.lokaverkefni.oturgjold.models;

import java.io.Serializable;

/**
 * Created by eggert on 09/03/15.
 */
public class User {

    //socialsecurity number
    private String ssn;
    private String name;
    //device id
    private String dev_id;
    //from data table
    private int id;

    public String getDev_id() {
        return dev_id;
    }

    public void setDev_id(String dev_id) {
        this.dev_id = dev_id;
    }


    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
