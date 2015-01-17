package br.com.rcsports.model;

/**
 * Created by mrped_000 on 15/12/2014.
 */
public class Product {

    private String id;
    private String name;
    private Double price;
    private Integer amount;
    private String client_id;

    public Product() {
    }

    public Product(String id, String name, Double price, Integer amount, String client_id) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.amount = amount;
        this.client_id = client_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }
}
