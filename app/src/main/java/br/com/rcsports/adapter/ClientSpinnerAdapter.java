package br.com.rcsports.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.rcsports.R;
import br.com.rcsports.model.Client;

/**
 * Created by Pedro on 16/12/2014.
 */
public class ClientSpinnerAdapter extends BaseAdapter {

    private Context context;
    private List<Client> clients;

    public ClientSpinnerAdapter(Context context, List<Client> clients) {
        this.context = context;
        this.clients = clients;
    }

    @Override
    public int getCount() {
        return clients.size();
    }

    @Override
    public Object getItem(int position) {
        return clients.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Client client = clients.get(position);

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.row_spinner_client, parent, false);
        }

        TextView label = (TextView) convertView.findViewById(R.id.client_row_spinner);
        label.setText(client.getName());

        return convertView;
    }
}
