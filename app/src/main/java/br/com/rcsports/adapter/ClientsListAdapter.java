package br.com.rcsports.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.rcsports.MainActivity;
import br.com.rcsports.R;
import br.com.rcsports.dao.DAO;
import br.com.rcsports.dialog.exclusion.ClientExclusionDialogFragment;
import br.com.rcsports.dialog.register.ClientDialogFragment;
import br.com.rcsports.listener.IOnClickListener;
import br.com.rcsports.model.Client;

/**
 * Created by Pedro on 15/12/2014.
 */
public class ClientsListAdapter extends BaseAdapter implements Filterable {

    private Fragment fragment;
    private Context context;
    private IOnClickListener callback;

    private List<Client> clients = null;
    private List<Client> clientsFiltered = null;
    private LayoutInflater mInflater;
    private ItemFilter mFilter;
    private boolean isSearching;

    public ClientsListAdapter(Fragment fragment, Context context, List<Client> clients) {
        this.context = context;

        this.clients = clients;
        this.clientsFiltered = clients;
        this.mInflater = LayoutInflater.from(context);

        this.fragment = fragment;
        this.callback = (IOnClickListener) fragment;
        this.isSearching = false;
    }

    @Override
    public int getCount() {
        return clientsFiltered.size();
    }

    @Override
    public Object getItem(int position) {
        return clientsFiltered.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_clients, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        final Client client = clientsFiltered.get(position);

        // Botões para mapeamento do teclado físico, btEdit, btDelete
        holder.btEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClientDialogFragment clientDialogFragment = new ClientDialogFragment();
                clientDialogFragment.setTargetFragment(fragment, 0);
                FragmentManager fragmentManager = fragment.getFragmentManager();

                Bundle bundle = new Bundle();
                bundle.putString("client_id", client.getId());
                bundle.putString("client_name", client.getName());
                bundle.putString("client_phone", client.getPhone());
                bundle.putString("client_address", client.getAddress());

                clientDialogFragment.setArguments(bundle);
                clientDialogFragment.show(fragmentManager, "dialog_client_update");
            }
        });

        holder.btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (DAO.open(context).findSalesWithClientId(client.getId()).size() > 0) {
                    ClientExclusionDialogFragment clientExclusionDialogFragment = new ClientExclusionDialogFragment();
                    clientExclusionDialogFragment.setTargetFragment(fragment, 0);
                    FragmentManager fragmentManager = fragment.getFragmentManager();

                    Bundle bundle = new Bundle();
                    bundle.putString("client_id", client.getId());
                    bundle.putInt("client_position", position);

                    clientExclusionDialogFragment.setArguments(bundle);
                    clientExclusionDialogFragment.show(fragmentManager, "list_dialog_exclusion_client");
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                            .setTitle("Excluir client " + client.getName() + "?")
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    // Se estiver contiver algo no SearchView é preciso deletar o item na posição do salesFiltered
                                    // e remover o item no vetor original localizado pelo ID porque nem sempre as posições dos dois
                                    // vetores são compatíveis
                                    if (isSearching) {
                                        // Remove a venda da tabela original com base na venda nova encontrada pelo search
                                        // Utilizei Iterator para escapar da ConcurrentModificationException
                                        for (Iterator<Client> i = clients.iterator(); i.hasNext(); ) {
                                            Client oldClient = i.next();
                                            if (oldClient.getId().equals(client.getId())) {
                                                i.remove();
                                            }
                                        }

                                        clientsFiltered.remove(position);

                                    } else {
                                        // Caso contrário basta remover o item na mesma posição dos dois vetores
                                        clients.remove(position);
                                        clientsFiltered = clients;
                                    }

                                    DAO.open(context).delete(client);

                                    if (clients.size() <= 0) {
                                        MainActivity main = (MainActivity) context;
                                        main.redraw(1);
                                    }

                                    callback.onCLickDelete(null);
                                }
                            })
                            .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    builder.show();
                }
            }
        });

        holder.btMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final PopupWindow popupWindow = new PopupWindow(v.getContext());

                LayoutInflater layoutInflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ScrollView scrollView = (ScrollView) layoutInflater.inflate(R.layout.popup_menu_client, null);

                // Creating the PopupWindow
                popupWindow.setContentView(scrollView);
                popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
                popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setFocusable(true);
                //Clear the default translucent background
                popupWindow.setBackgroundDrawable(v.getContext().getResources().getDrawable(android.R.color.transparent));
                popupWindow.showAsDropDown(v, -280, -90);

                // EDITAR CLIENTE
                TextView opEdit = (TextView) scrollView.findViewById(R.id.popup_client_edit);
                opEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClientDialogFragment clientDialogFragment = new ClientDialogFragment();
                        clientDialogFragment.setTargetFragment(fragment, 0);
                        FragmentManager fragmentManager = fragment.getFragmentManager();

                        Bundle bundle = new Bundle();
                        bundle.putString("client_id", client.getId());
                        bundle.putString("client_name", client.getName());
                        bundle.putString("client_phone", client.getPhone());
                        bundle.putString("client_address", client.getAddress());

                        clientDialogFragment.setArguments(bundle);
                        clientDialogFragment.show(fragmentManager, "dialog_client_update");

                        popupWindow.dismiss();
                    }
                });

                // EXCLUIR CLIENTE
                TextView opDelete = (TextView) scrollView.findViewById(R.id.popup_client_delete);
                opDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (DAO.open(context).findSalesWithClientId(client.getId()).size() > 0) {
                            ClientExclusionDialogFragment clientExclusionDialogFragment = new ClientExclusionDialogFragment();
                            clientExclusionDialogFragment.setTargetFragment(fragment, 0);
                            FragmentManager fragmentManager = fragment.getFragmentManager();

                            Bundle bundle = new Bundle();
                            bundle.putString("client_id", client.getId());
                            bundle.putInt("client_position", position);

                            clientExclusionDialogFragment.setArguments(bundle);
                            clientExclusionDialogFragment.show(fragmentManager, "list_dialog_exclusion_client");
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                                    .setTitle("Excluir client " + client.getName() + "?")
                                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            // Se estiver contiver algo no SearchView é preciso deletar o item na posição do salesFiltered
                                            // e remover o item no vetor original localizado pelo ID porque nem sempre as posições dos dois
                                            // vetores são compatíveis
                                            if (isSearching) {
                                                // Remove a venda da tabela original com base na venda nova encontrada pelo search
                                                // Utilizei Iterator para escapar da ConcurrentModificationException
                                                for (Iterator<Client> i = clients.iterator(); i.hasNext(); ) {
                                                    Client oldClient = i.next();
                                                    if (oldClient.getId().equals(client.getId())) {
                                                        i.remove();
                                                    }
                                                }

                                                clientsFiltered.remove(position);

                                            } else {
                                                // Caso contrário basta remover o item na mesma posição dos dois vetores
                                                clients.remove(position);
                                                clientsFiltered = clients;
                                            }

                                            DAO.open(context).delete(client);

                                            if (clients.size() <= 0) {
                                                MainActivity main = (MainActivity) context;
                                                main.redraw(1);
                                            }

                                            callback.onCLickDelete(null);
                                        }
                                    })
                                    .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                            builder.show();
                        }
                        popupWindow.dismiss();
                    }
                });
            }
        });

        // Adiciona os nome com base no resultado do filtro
        holder.txtClientName.setText(clientsFiltered.get(position).getName());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ItemFilter();
        }
        return mFilter;
    }

    private class ViewHolder {

        private TextView txtClientName;

        private Button btEdit;
        private Button btDelete;

        private ImageButton btMenu;

        public ViewHolder(View v) {

            txtClientName = (TextView) v.findViewById(R.id.client_name);
            btMenu = (ImageButton) v.findViewById(R.id.client_menu_button);
            btEdit = (Button) v.findViewById(R.id.client_map_edit);
            btDelete = (Button) v.findViewById(R.id.client_map_delete);
        }
    }

    private class ItemFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            if (constraint.length() > 0) isSearching = true;
            else isSearching = false;

            FilterResults results = new FilterResults();

            String filterString = constraint.toString();
            List<Client> tempClients = new ArrayList<>();
            final int stringLength = filterString.length();

            for (Client client : clients) {
                if (stringLength <= client.getName().length()) {
                    if (client.getName().toLowerCase().contains(filterString.toLowerCase())) {
                        tempClients.add(client);
                    }
                }
            }

            results.values = tempClients;
            results.count = tempClients.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clientsFiltered = (List<Client>) results.values;
            notifyDataSetChanged();
        }

    }
}
