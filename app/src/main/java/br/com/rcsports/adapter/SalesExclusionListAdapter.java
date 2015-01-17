package br.com.rcsports.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.rcsports.R;
import br.com.rcsports.dao.DAO;
import br.com.rcsports.model.Client;
import br.com.rcsports.model.Payment;
import br.com.rcsports.model.Sale;

/**
 * Created by Pedro on 15/12/2014.
 */
public class SalesExclusionListAdapter extends BaseAdapter {

    private Context context;
    private List<Sale> sales;

    public SalesExclusionListAdapter(Context context, List<Sale> sales) {
        this.context = context;
        this.sales = sales;
    }

    @Override
    public int getCount() {
        return sales.size();
    }

    @Override
    public Object getItem(int position) {
        return sales.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_sales, null);
        }

        ImageView imgSale = (ImageView) convertView.findViewById(R.id.sale_image);
        TextView txtSaleClient = (TextView) convertView.findViewById(R.id.sale_client);
        TextView txtSaleDebit = (TextView) convertView.findViewById(R.id.sale_debit);

        final Sale sale = sales.get(position);

        List<Payment> payments = DAO.open(context).findPaymentsWithSaleId(sale.getId());

        Double totalPaid = 0.0;
        for (Payment payment : payments) {
            totalPaid += payment.getPaid();
        }

        Client client = DAO.open(context).findClientById(sale.getClient_id());

        if (client != null) {
            imgSale.setImageResource(R.drawable.ic_list_item);
            txtSaleClient.setText(client.getName());
            txtSaleDebit.setText(String.format("%.2f", sale.getTotal() - totalPaid) + " R$");
        }

        return convertView;
    }
}
