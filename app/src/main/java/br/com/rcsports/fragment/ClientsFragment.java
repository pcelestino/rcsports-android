package br.com.rcsports.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

import br.com.rcsports.MainActivity;
import br.com.rcsports.R;
import br.com.rcsports.adapter.ClientsListAdapter;
import br.com.rcsports.dao.DAO;
import br.com.rcsports.dialog.information.ClientInformationDialogFragment;
import br.com.rcsports.dialog.register.ClientDialogFragment;
import br.com.rcsports.listener.IOnClickListener;
import br.com.rcsports.model.Client;

/**
 * Created by Pedro on 14/12/2014.
 */
public class ClientsFragment extends Fragment implements IOnClickListener {

    private ListView clientList;
    private List<Client> clients;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clients, null);

        clients = DAO.open(getActivity()).getListClients();

        clientList = (ListView) view.findViewById(R.id.list_clients);
        clientList.setAdapter(new ClientsListAdapter(this, getActivity(), clients));
        clientList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager fragmentManager = getFragmentManager();
                ClientInformationDialogFragment clientInformation = new ClientInformationDialogFragment();

                Client client = (Client) parent.getAdapter().getItem(position);

                Bundle bundle = new Bundle(2);
                bundle.putString("client_name", client.getName());
                bundle.putString("client_phone", client.getPhone());
                bundle.putString("client_address", client.getAddress());

                clientInformation.setArguments(bundle);
                clientInformation.show(fragmentManager, "dialog_client_information");
            }
        });

        clientList.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_DEL) {

                    Button btDelete = (Button) v.findViewById(R.id.client_map_delete);
                    btDelete.callOnClick();

                    return true;
                }
                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_INSERT) {

                    Button btEdit = (Button) v.findViewById(R.id.client_map_edit);
                    btEdit.callOnClick();

                    return true;
                }
                return false;
            }
        });

        clientList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Button btDelete = (Button) view.findViewById(R.id.client_map_delete);
                btDelete.callOnClick();

                return true;
            }
        });

        // Botão Cadastrar
        Button btRegister = (Button) view.findViewById(R.id.add_client_button);
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                ClientDialogFragment clientDialogFragment = new ClientDialogFragment();
                clientDialogFragment.setTargetFragment(ClientsFragment.this, 0);
                clientDialogFragment.show(fragmentManager, "dialog_client_register");
            }
        });

        return view;
    }

    @Override
    public void onClickSave() {
        clientList.setAdapter(new ClientsListAdapter(this, getActivity(), DAO.open(getActivity()).getListClients()));
    }

    @Override
    public void onCLickDelete(Object object) {
        if (object != null && clients.size() > 0) {
            int position = (int) object;
            clients.remove(position);
        }
        clientList.invalidateViews();
        ((MainActivity) getActivity()).redraw(1);
    }

    @Override
    public String toString() {
        return "Cliente";
    }

    // Necessário para o SearchView
    public List<Client> getClients() {
        return clients;
    }

    public ListView getClientList() {
        return clientList;
    }
}
