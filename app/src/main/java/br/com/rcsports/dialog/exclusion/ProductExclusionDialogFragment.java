package br.com.rcsports.dialog.exclusion;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import br.com.rcsports.R;
import br.com.rcsports.adapter.SalesExclusionListAdapter;
import br.com.rcsports.dao.DAO;
import br.com.rcsports.listener.IOnClickListener;
import br.com.rcsports.model.Payment;
import br.com.rcsports.model.Product;
import br.com.rcsports.model.Sale;
import br.com.rcsports.model.Transaction;

/**
 * Created by mrped_000 on 18/12/2014.
 */
public class ProductExclusionDialogFragment extends DialogFragment implements IOnClickListener {

    private IOnClickListener callback;
    private String productId;
    private List<Sale> sales;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_fragment_exclusion, container);
        getDialog().setTitle("Vendas associadas");

        callback = (IOnClickListener) getTargetFragment();

        productId = getArguments().getString("product_id");
        sales = DAO.open(getActivity()).findSalesWithProductId(productId);

        ListView listSaleExclusion = (ListView) view.findViewById(R.id.list_dialog_exclusion);
        listSaleExclusion.setAdapter(new SalesExclusionListAdapter(getActivity(), sales));
        if (listSaleExclusion.getCount() <= 0) {
            getDialog().setTitle("Excluir cliente " + DAO.open(getActivity()).findProductById(productId).getName());
        }

        Button btExcludes = (Button) view.findViewById(R.id.list_dialog_exclusion_button_excludes);
        btExcludes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product product = new Product();
                product.setId(productId);
                DAO.open(getActivity()).delete(product);

                // Exclui as vendas e suas transações no banco de dados
                for (Sale sale : sales) {
                    DAO.open(getActivity()).delete(sale);

                    List<Transaction> transactions = DAO.open(getActivity()).findTransactionsWithSaleId(sale.getId());

                    for (Transaction transaction : transactions) {
                        DAO.open(getActivity()).delete(transaction);
                    }

                    List<Payment> payments = DAO.open(getActivity()).findPaymentsWithSaleId(sale.getId());

                    // Remove os pagamentos relacionados a tabela venda
                    for (Payment payment : payments) {
                        DAO.open(getActivity()).delete(payment);
                    }
                }

                callback.onCLickDelete(getArguments().getInt("product_position"));

                dismiss();
            }
        });

        Button btLeave = (Button) view.findViewById(R.id.list_dialog_exclusion_button_leave);
        btLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onClickSave() {

    }

    @Override
    public void onCLickDelete(Object object) {

    }
}
