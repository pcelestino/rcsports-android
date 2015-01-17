package br.com.rcsports.fragment;

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
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.rcsports.R;
import br.com.rcsports.adapter.SalesListAdapter;
import br.com.rcsports.dao.DAO;
import br.com.rcsports.dialog.information.SaleInformationDialogFragment;
import br.com.rcsports.dialog.register.SaleDialogFragment;
import br.com.rcsports.listener.IOnClickListener;
import br.com.rcsports.model.Client;
import br.com.rcsports.model.Payment;
import br.com.rcsports.model.Sale;

/**
 * Created by Pedro on 14/12/2014.
 */
public class SalesFragment extends Fragment implements IOnClickListener {

    private SalesListAdapter salesListAdapter;
    public ListView salesList;
    public List<Sale> sales;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sales, null);

        salesList = (ListView) view.findViewById(R.id.list_sales);
        sales = DAO.open(getActivity()).getListSales();

        salesListAdapter = new SalesListAdapter(this, getActivity(), sales);
        salesList.setAdapter(salesListAdapter);


        salesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                FragmentManager fragmentManager = getFragmentManager();
                SaleInformationDialogFragment saleInformation = new SaleInformationDialogFragment();

                Sale sale = (Sale) parent.getAdapter().getItem(position);
                Client client = DAO.open(getActivity()).findClientById(sale.getClient_id());
                List<Payment> payments = DAO.open(getActivity()).findPaymentsWithSaleId(sale.getId());

                Double totalPaid = 0.0;
                for (Payment payment : payments) {
                    totalPaid += payment.getPaid();
                }

                Bundle bundle = new Bundle(6);
                bundle.putString("sale_id", sale.getId());
                bundle.putString("sale_name", client.getName());
                bundle.putString("sale_phone", client.getPhone());
                bundle.putString("sale_address", client.getAddress());
                bundle.putDouble("sale_total", sale.getTotal());
                bundle.putDouble("sale_paid", totalPaid);

                saleInformation.setArguments(bundle);
                saleInformation.show(fragmentManager, "dialog_sale_information");
            }
        });

        salesList.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_MINUS) {

                    Button btPayments = (Button) v.findViewById(R.id.sale_map_payments);
                    btPayments.callOnClick();

                    return true;
                }
                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_DEL) {

                    Button btDelete = (Button) v.findViewById(R.id.sale_map_delete);
                    btDelete.callOnClick();

                    return true;
                }
                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_INSERT) {

                    Button btEdit = (Button) v.findViewById(R.id.sale_map_edit);
                    btEdit.callOnClick();

                    return true;
                }
                return false;
            }
        });

        salesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Button btDelete = (Button) view.findViewById(R.id.sale_map_delete);
                btDelete.callOnClick();

                return true;
            }
        });

        // Botão Cadastrar
        Button registerSale = (Button) view.findViewById(R.id.add_sale_button);
        registerSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DAO.open(getActivity()).getListClients().size() > 0 && DAO.open(getActivity()).getListProducts().size() > 0) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    SaleDialogFragment saleDialogFragment = new SaleDialogFragment();
                    saleDialogFragment.setTargetFragment(SalesFragment.this, 0);
                    saleDialogFragment.show(fragmentManager, "dialog_sale_register");
                } else {
                    Toast.makeText(getActivity(), "Cadastre Clientes e Produtos", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        if (DAO.open(getActivity()) != null) {
            DAO.open(getActivity()).close();
        }
        super.onDestroyView();
    }

    @Override
    public void onClickSave() {
        salesList.setAdapter(new SalesListAdapter(this, getActivity(), DAO.open(getActivity()).getListSales()));
    }

    @Override
    public void onCLickDelete(Object object) {
        salesList.invalidateViews();
    }

    @Override
    public String toString() {
        return "Venda";
    }

    // Necessário para o SearchView
    public ListView getSalesList() {
        return salesList;
    }

    public List<Sale> getSales() {
        return sales;
    }
}
