package br.com.rcsports.dialog.information;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import br.com.rcsports.MainActivity;
import br.com.rcsports.R;
import br.com.rcsports.adapter.TransactionsInfoListAdapter;
import br.com.rcsports.dao.DAO;
import br.com.rcsports.dialog.register.PaymentDialogFragment;
import br.com.rcsports.model.Client;
import br.com.rcsports.model.Transaction;

/**
 * Created by Pedro on 19/12/2014.
 */
public class SaleInformationDialogFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_fragment_sale_information, container);
        getDialog().setTitle("Produtos comprados");

        final String saleId = getArguments().getString("sale_id");
        final String clientName = getArguments().getString("sale_name");
        final String clientPhone = getArguments().getString("sale_phone");
        final String clientAddress = getArguments().getString("sale_address");
        Double totalPrice = getArguments().getDouble("sale_total");
        Double paidPrice = getArguments().getDouble("sale_paid");
        Double debitPrice = totalPrice - paidPrice;

        List<Transaction> transactions = DAO.open(getActivity()).findTransactionsWithSaleId(saleId);

        ListView listProducts = (ListView) view.findViewById(R.id.sale_information_list_products);
        TextView txtTotalPrice = (TextView) view.findViewById(R.id.sale_information_total_price);
        TextView txtPaidPrice = (TextView) view.findViewById(R.id.sale_information_paid_price);
        TextView txtDebitPrice = (TextView) view.findViewById(R.id.sale_information_debit_price);
        TextView txtDebitPriceText = (TextView) view.findViewById(R.id.sale_information_debit_text_price);

        Button btOk = (Button) view.findViewById(R.id.sale_information_button_ok);



        listProducts.setAdapter(new TransactionsInfoListAdapter(getActivity(), transactions));

        txtTotalPrice.setText(String.format("%.2f", totalPrice));
        txtPaidPrice.setText(String.format("%.2f", paidPrice));

        if (debitPrice >= 0) {
            txtDebitPriceText.setText(getString(R.string.remaining_amount));
            txtDebitPrice.setText(getDecimal(debitPrice));
        } else {
            txtDebitPriceText.setText(getString(R.string.change_amount));
            txtDebitPrice.setText(getDecimal(Math.abs(debitPrice)));
        }

        ImageButton btCall = (ImageButton) view.findViewById(R.id.sale_information_button_call);

        btCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                ClientInformationDialogFragment clientInformation = new ClientInformationDialogFragment();

                Bundle bundle = new Bundle(3);
                bundle.putString("client_name", clientName);
                bundle.putString("client_phone", clientPhone);
                bundle.putString("client_address", clientAddress);

                clientInformation.setArguments(bundle);
                clientInformation.show(fragmentManager, "dialog_client_information");
            }
        });

        ImageButton btListPayments = (ImageButton) view.findViewById(R.id.sale_information_button_payments);

        btListPayments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                PaymentInformationDialogFragment paymentInformation = new PaymentInformationDialogFragment();

                Bundle bundle = new Bundle(1);
                bundle.putString("sale_id", saleId);

                paymentInformation.setArguments(bundle);
                paymentInformation.show(fragmentManager, "payment_information_dialog_fragment");
            }
        });

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    private String getDecimal(Double value) {
        return String.format("%.2f", value);
    }
}
