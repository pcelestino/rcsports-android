package br.com.rcsports.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.rcsports.R;
import br.com.rcsports.dao.DAO;
import br.com.rcsports.listener.IOnClickListener;
import br.com.rcsports.model.Product;
import br.com.rcsports.model.Transaction;

/**
 * Created by Pedro on 15/12/2014.
 */
public class TransactionsListAdapter extends BaseAdapter {

    private Context context;
    private List<Transaction> transactions;
    private IOnClickListener callback;
    private Product product;
    private Transaction transaction;

    public TransactionsListAdapter(Fragment fragment, Context context, List<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
        callback = (IOnClickListener) fragment;
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

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_transactions, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        transaction = transactions.get(position);
        product = DAO.open(context).findProductById(transaction.getProduct_id());
        holder.txtProductAmount.setText(String.valueOf(transaction.getAmount()));
        holder.txtProductName.setText(product.getName());
        holder.txtProductPrice.setText(String.format("%.2f", product.getPrice()) + " R$");

        return convertView;
    }

    private class ViewHolder {

        private TextView txtProductAmount;
        private TextView txtProductName;
        private TextView txtProductPrice;

        public ViewHolder(View v) {
            txtProductAmount = (TextView) v.findViewById(R.id.transaction_product_amount);
            txtProductName = (TextView) v.findViewById(R.id.transaction_product_name);
            txtProductPrice = (TextView) v.findViewById(R.id.transaction_product_price);
        }
    }
}
