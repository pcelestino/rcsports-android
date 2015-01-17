package br.com.rcsports.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.rcsports.R;
import br.com.rcsports.model.Product;

/**
 * Created by Pedro on 16/12/2014.
 */
public class ProductSpinnerAdapter extends BaseAdapter {

    private Context context;
    private List<Product> products;

    public ProductSpinnerAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Product product = products.get(position);

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.row_spinner_product, parent, false);
        }

        TextView txtTitle = (TextView) convertView.findViewById(R.id.product_title_row_spinner);
        txtTitle.setText(product.getName());

        TextView txtSubTitle = (TextView) convertView.findViewById(R.id.product_subtitle_row_spinner);
        txtSubTitle.setText(String.format("%.2f", product.getPrice()) + " R$");

        return convertView;
    }
}
