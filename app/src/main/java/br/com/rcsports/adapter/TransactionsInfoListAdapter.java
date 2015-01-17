package br.com.rcsports.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.rcsports.R;
import br.com.rcsports.dao.DAO;
import br.com.rcsports.model.Product;
import br.com.rcsports.model.Transaction;

/**
 * Created by Pedro on 15/12/2014.
 */
public class TransactionsInfoListAdapter extends BaseAdapter {

    private Context context;
    private List<Transaction> transactions;

    public TransactionsInfoListAdapter(Context context, List<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @Override
    public int getCount() {
        return transactions.size();
    }

    @Override
    public Object getItem(int position) {
        return transactions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_transactions, null);
        }

        TextView txtProductAmount = (TextView) convertView.findViewById(R.id.transaction_product_amount);
        TextView txtProductName = (TextView) convertView.findViewById(R.id.transaction_product_name);
        TextView txtProductPrice = (TextView) convertView.findViewById(R.id.transaction_product_price);

        Transaction transaction = transactions.get(position);

        txtProductAmount.setText(String.valueOf(transaction.getAmount()));

        Product product = DAO.open(context).findProductById(transaction.getProduct_id());

        txtProductName.setText(product.getName());
        txtProductPrice.setText(String.format("%.2f", product.getPrice()) + " R$");

        return convertView;
    }
}
