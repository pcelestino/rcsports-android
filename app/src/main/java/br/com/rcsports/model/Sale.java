package br.com.rcsports.model;

/**
 * Created by mrped_000 on 15/12/2014.
 */
public class Sale {

    private String id;
    private Double total;
    private String date;
    private String time;
    private String client_id;

    public Sale() {
    }

    public Sale(String id, Double total, String date, String time, String client_id) {
        this.id = id;
        this.total = total;
        this.date = date;
        this.time = time;
        this.client_id = client_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
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

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }
}
