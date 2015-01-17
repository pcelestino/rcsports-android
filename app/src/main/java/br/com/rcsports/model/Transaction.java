package br.com.rcsports.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Pedro on 17/12/2014.
 */
public class Transaction implements Parcelable {
    public static final Parcelable.Creator<Transaction> CREATOR = new Parcelable.Creator<Transaction>() {
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };
    private String id;
    private Integer amount;
    private String product_id;
    private String sale_id;

    public Transaction() {
    }

    public Transaction(String id, Integer amount, String product_id, String sale_id) {
        this.id = id;
        this.amount = amount;
        this.product_id = product_id;
        this.sale_id = sale_id;
    }

    public Transaction(Parcel in) {
        id = in.readString();
        amount = in.readInt();
        product_id = in.readString();
        sale_id = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getSale_id() {
        return sale_id;
    }

    public void setSale_id(String sale_id) {
        this.sale_id = sale_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(amount);
        dest.writeString(product_id);
        dest.writeString(sale_id);
    }
}
