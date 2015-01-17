package br.com.rcsports.model;

/**
 * Created by Pedro on 25/12/2014.
 */
public class Payment {
    private String id;
    private Double paid;
    private String date;
    private String time;
    private String sale_id;

    public Payment() {

    }

    public Payment(String id, Double paid, String date, String time, String sale_id) {
        this.id = id;
        this.paid = paid;
        this.date = date;
        this.time = time;
        this.sale_id = sale_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getPaid() {
        return paid;
    }

    public void setPaid(Double paid) {
        this.paid = paid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSale_id() {
        return sale_id;
    }

    public void setSale_id(String sale_id) {
        this.sale_id = sale_id;
    }
}
