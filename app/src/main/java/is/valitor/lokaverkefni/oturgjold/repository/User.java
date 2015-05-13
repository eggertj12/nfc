package is.valitor.lokaverkefni.oturgjold.repository;

/**
 * POJO for software representation of account owner.
 * Created by eggert on 09/03/15.
 */
public class User {

    //socialsecurity number
    private String ssn;
    private String name;
    //device id
    private String device_id;
    //from data table
    private int usr_id;

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
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

    public int getUsr_id() {
        return usr_id;
    }

    public void setUsr_id(int usr_id) {
        this.usr_id = usr_id;
    }


}
