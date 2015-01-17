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
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.rcsports.MainActivity;
import br.com.rcsports.R;
import br.com.rcsports.dao.DAO;
import br.com.rcsports.dialog.register.PaymentDialogFragment;
import br.com.rcsports.dialog.register.SaleDialogFragment;
import br.com.rcsports.listener.IOnClickListener;
import br.com.rcsports.model.Client;
import br.com.rcsports.model.Payment;
import br.com.rcsports.model.Sale;
import br.com.rcsports.model.Transaction;

/**
 * Created by Pedro on 15/12/2014.
 */
public class SalesListAdapter extends BaseAdapter implements Filterable {

    private Fragment fragment;
    private Context context;
    private IOnClickListener callback;

    private List<Sale> sales;
    private List<Sale> salesFiltered;
    private Double totalPaid;
    private LayoutInflater mInflater;
    private ItemFilter mFilter;
    private boolean isSearching;

    public SalesListAdapter(Fragment fragment, Context context, List<Sale> sales) {
        this.context = context;

        this.sales = sales;
        this.salesFiltered = sales;
        this.mInflater = LayoutInflater.from(context);

        this.fragment = fragment;
        this.callback = (IOnClickListener) fragment;
        this.isSearching = false;
    }

    @Override
    public int getCount() {
        return salesFiltered.size();
    }

    @Override
    public Object getItem(int position) {
        return salesFiltered.get(position);
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
            convertView = mInflater.inflate(R.layout.list_sales, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        final Sale sale = salesFiltered.get(position);

        // Botões de mapeamento do teclado físico, btPayments, btEdit, btDelete
        holder.btPayments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                PaymentDialogFragment paymentInformation = new PaymentDialogFragment();

                Client client = DAO.open(context).findClientById(sale.getClient_id());

                Bundle bundle = new Bundle(2);
                bundle.putString("sale_id", sale.getId());
                bundle.putString("client_name", client.getName());

                // IMPORTANTE PARA MUDAR A IMAGEM DA VENDA PARA VERDE
                bundle.putInt("sale_position", position);

                paymentInformation.setArguments(bundle);
                paymentInformation.setTargetFragment(fragment, 0);
                paymentInformation.show(fragmentManager, "payment_dialog_fragment");
            }
        });

        holder.btEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SaleDialogFragment saleDialogFragment = new SaleDialogFragment();
                saleDialogFragment.setTargetFragment(fragment, 0);
                FragmentManager fragmentManager = fragment.getFragmentManager();

                List<Payment> payments = DAO.open(context).findPaymentsWithSaleId(sale.getId());

                totalPaid = 0.0;
                for (Payment payment : payments) {
                    totalPaid += payment.getPaid();
                }

                Bundle bundle = new Bundle();
                bundle.putString("sale_id", sale.getId());
                bundle.putString("sale_date", sale.getDate());
                bundle.putString("sale_time", sale.getTime());
                bundle.putDouble("sale_paid", totalPaid);
                bundle.putDouble("sale_total", sale.getTotal());
                bundle.putString("sale_client_id", sale.getClient_id());

                saleDialogFragment.setArguments(bundle);
                saleDialogFragment.show(fragmentManager, "dialog_sale_update");
            }
        });

        holder.btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("Deseja excluir " + DAO.open(context).findClientById(sale.getClient_id()).getName() + "?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                List<Transaction> transactions = DAO.open(context).findTransactionsWithSaleId(sale.getId());

                                // Remove as transações relacionadas a tabela venda
                                for (Transaction transaction : transactions) {
                                    DAO.open(context).delete(transaction);
                                }

                                List<Payment> payments = DAO.open(context).findPaymentsWithSaleId(sale.getId());

                                // Remove os pagamentos relacionados a tabela venda
                                for (Payment payment : payments) {
                                    DAO.open(context).delete(payment);
                                }

                                // Se estiver contiver algo no SearchView é preciso deletar o item na posição do salesFiltered
                                // e remover o item no vetor original localizado pelo ID porque nem sempre as posições dos dois
                                // vetores são compatíveis
                                if (isSearching) {
                                    // Remove a venda da tabela original com base na venda nova encontrada pelo search
                                    // Utilizei Iterator para escapar da ConcurrentModificationException
                                    for (Iterator<Sale> i = sales.iterator(); i.hasNext(); ) {
                                        Sale oldSale = i.next();
                                        if (oldSale.getId().equals(sale.getId())) {
                                            i.remove();
                                        }
                                    }

                                    salesFiltered.remove(position);

                                } else {
                                    // Caso contrário basta remover o item na mesma posição dos dois vetores
                                    sales.remove(position);
                                    salesFiltered = sales;
                                }

                                DAO.open(context).delete(sale);

                                if (sales.size() <= 0) {
                                    MainActivity main = (MainActivity) context;
                                    main.redraw(0);
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
        });

        holder.btMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final PopupWindow popupWindow = new PopupWindow(v.getContext());

                LayoutInflater layoutInflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ScrollView scrollView = (ScrollView) layoutInflater.inflate(R.layout.popup_menu_sale, null);

                // Creating the PopupWindow
                popupWindow.setContentView(scrollView);
                popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
                popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setFocusable(true);
                //Clear the default translucent background
                popupWindow.setBackgroundDrawable(v.getContext().getResources().getDrawable(android.R.color.transparent));
                popupWindow.showAsDropDown(v, -317, -90);

                // LISTAR PAGAMENTOS
                TextView opListPayments = (TextView) scrollView.findViewById(R.id.popup_sale_list_payments);
                opListPayments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                        PaymentDialogFragment paymentInformation = new PaymentDialogFragment();

                        Client client = DAO.open(context).findClientById(sale.getClient_id());

                        Bundle bundle = new Bundle(2);
                        bundle.putString("sale_id", sale.getId());
                        bundle.putString("client_name", client.getName());

                        // IMPORTANTE PARA MUDAR A IMAGEM DA VENDA PARA VERDE
                        bundle.putInt("sale_position", position);

                        paymentInformation.setArguments(bundle);
                        paymentInformation.setTargetFragment(fragment, 0);
                        paymentInformation.show(fragmentManager, "payment_dialog_fragment");

                        popupWindow.dismiss();
                    }
                });

                // EDITAR VENDA
                TextView opEdit = (TextView) scrollView.findViewById(R.id.popup_sale_edit);
                opEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SaleDialogFragment saleDialogFragment = new SaleDialogFragment();
                        saleDialogFragment.setTargetFragment(fragment, 0);
                        FragmentManager fragmentManager = fragment.getFragmentManager();

                        List<Payment> payments = DAO.open(context).findPaymentsWithSaleId(sale.getId());

                        totalPaid = 0.0;
                        for (Payment payment : payments) {
                            totalPaid += payment.getPaid();
                        }

                        Bundle bundle = new Bundle();
                        bundle.putString("sale_id", sale.getId());
                        bundle.putString("sale_date", sale.getDate());
                        bundle.putString("sale_time", sale.getTime());
                        bundle.putDouble("sale_paid", totalPaid);
                        bundle.putDouble("sale_total", sale.getTotal());
                        bundle.putString("sale_client_id", sale.getClient_id());

                        saleDialogFragment.setArguments(bundle);
                        saleDialogFragment.show(fragmentManager, "dialog_sale_update");

                        popupWindow.dismiss();
                    }
                });

                // EXCLUIR VENDA
                TextView opDelete = (TextView) scrollView.findViewById(R.id.popup_sale_delete);
                opDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                                .setTitle("Deseja excluir " + DAO.open(context).findClientById(sale.getClient_id()).getName() + "?")
                                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        List<Transaction> transactions = DAO.open(context).findTransactionsWithSaleId(sale.getId());

                                        // Remove as transações relacionadas a tabela venda
                                        for (Transaction transaction : transactions) {
                                            DAO.open(context).delete(transaction);
                                        }

                                        List<Payment> payments = DAO.open(context).findPaymentsWithSaleId(sale.getId());

                                        // Remove os pagamentos relacionados a tabela venda
                                        for (Payment payment : payments) {
                                            DAO.open(context).delete(payment);
                                        }

                                        // Se estiver contiver algo no SearchView é preciso deletar o item na posição do salesFiltered
                                        // e remover o item no vetor original localizado pelo ID porque nem sempre as posições dos dois
                                        // vetores são compatíveis
                                        if (isSearching) {
                                            // Remove a venda da tabela original com base na venda nova encontrada pelo search
                                            // Utilizei Iterator para escapar da ConcurrentModificationException
                                            for (Iterator<Sale> i = sales.iterator(); i.hasNext(); ) {
                                                Sale oldSale = i.next();
                                                if (oldSale.getId().equals(sale.getId())) {
                                                    i.remove();
                                                }
                                            }

                                            salesFiltered.remove(position);

                                        } else {
                                            // Caso contrário basta remover o item na mesma posição dos dois vetores
                                            sales.remove(position);
                                            salesFiltered = sales;
                                        }

                                        DAO.open(context).delete(sale);

                                        if (sales.size() <= 0) {
                                            MainActivity main = (MainActivity) context;
                                            main.redraw(0);
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

                        popupWindow.dismiss();
                    }
                });
            }
        });

        List<Payment> payments = DAO.open(context).findPaymentsWithSaleId(sale.getId());

        totalPaid = 0.0;
        for (Payment payment : payments) {
            totalPaid += payment.getPaid();
        }

        Client client = DAO.open(context).findClientById(salesFiltered.get(position).getClient_id());

        if (client != null) {
            holder.txtSaleClient.setText(client.getName());
            holder.txtSaleDebit.setText(String.format("%.2f", sale.getTotal() - totalPaid) + " R$");
            holder.txtSaleDate.setText(sale.getDate());
            holder.txtSaleTime.setText(sale.getTime());
        }

        if ((sale.getTotal() - totalPaid) <= 0) {
            holder.imageSale.setImageResource(R.drawable.ic_sale_blue);
        } else {
            holder.imageSale.setImageResource(R.drawable.ic_sale_red);
        }

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

        private ImageView imageSale;
        private TextView txtSaleClient;
        private TextView txtSaleDebit;
        private TextView txtSaleDate;
        TextView txtSaleTime;
        private ImageButton btMenu;

        // Botões de mapeamento do teclado
        private Button btPayments;
        private Button btEdit;
        private Button btDelete;

        public ViewHolder(View v) {
            imageSale = (ImageView) v.findViewById(R.id.sale_image);
            txtSaleClient = (TextView) v.findViewById(R.id.sale_client);
            txtSaleDebit = (TextView) v.findViewById(R.id.sale_debit);
            txtSaleDate = (TextView) v.findViewById(R.id.sale_date);
            txtSaleTime = (TextView) v.findViewById(R.id.sale_time);

            btPayments = (Button) v.findViewById(R.id.sale_map_payments);
            btEdit = (Button) v.findViewById(R.id.sale_map_edit);
            btDelete = (Button) v.findViewById(R.id.sale_map_delete);

            btMenu = (ImageButton) v.findViewById(R.id.sale_menu_button);
        }
    }

    private class ItemFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            if (constraint.length() > 0) isSearching = true;
            else isSearching = false;

            FilterResults results = new FilterResults();

            String filterString = constraint.toString();
            List<Sale> tempSales = new ArrayList<>();
            final int stringLength = filterString.length();

            for (Sale sale : sales) {

                Client client = DAO.open(context).findClientById(sale.getClient_id());
                if (stringLength <= client.getName().length()) {
                    if (client.getName().toLowerCase().contains(filterString.toLowerCase())) {
                        tempSales.add(sale);
                    }
                }
            }

            results.values = tempSales;
            results.count = tempSales.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            salesFiltered = (List<Sale>) results.values;
            notifyDataSetChanged();
        }
    }
}
