package br.com.rcsports.dialog.information;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Stack;

import br.com.rcsports.MainActivity;
import br.com.rcsports.R;
import br.com.rcsports.adapter.PaymentsInfoListAdapter;
import br.com.rcsports.adapter.PaymentsListAdapter;
import br.com.rcsports.dao.DAO;
import br.com.rcsports.fragment.SalesFragment;
import br.com.rcsports.listener.IOnClickListener;
import br.com.rcsports.model.Payment;
import br.com.rcsports.model.Sale;

/**
 * Created by Pedro on 25/12/2014.
 */
public class PaymentInformationDialogFragment extends DialogFragment {

    private Double debitPrice;
    private TextView txtDebitPrice;
    private TextView txtDebitPriceDescription;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_fragment_payment_information, container, false);
        getDialog().setTitle("Pagamentos efetuados");

        final String saleId = getArguments().getString("sale_id");

        List<Payment> payments = DAO.open(getActivity()).findPaymentsWithSaleId(saleId);

        ListView listViewPayments = (ListView) view.findViewById(R.id.list_information_payments);
        listViewPayments.setAdapter(new PaymentsInfoListAdapter(getActivity(), payments));

        Button btOk = (Button) view.findViewById(R.id.payment_information_button_ok);

        TextView txtTotalPrice = (TextView) view.findViewById(R.id.payment_information_total_price);
        TextView txtPaidPrice = (TextView) view.findViewById(R.id.payment_information_paid_price);
        txtDebitPrice = (TextView) view.findViewById(R.id.payment_information_debit_price);
        txtDebitPriceDescription = (TextView) view.findViewById(R.id.payment_information_debit_text_price);

        Sale sale = DAO.open(getActivity()).findSaleById(saleId);

        Double totalPrice = sale.getTotal();

        Double fixPaidPrice = 0.0;
        for (Payment payment : payments) {
            fixPaidPrice += payment.getPaid();
        }

        debitPrice = totalPrice - fixPaidPrice;

        txtTotalPrice.setText(getDecimal(totalPrice));
        txtPaidPrice.setText(getDecimal(fixPaidPrice));
        checkDebit();

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    private void checkDebit() {
        if (debitPrice >= 0) {
            txtDebitPriceDescription.setText(getString(R.string.remaining_amount));
            txtDebitPrice.setText(getDecimal(debitPrice));
        } else {
            txtDebitPriceDescription.setText(getString(R.string.change_amount));
            txtDebitPrice.setText(getDecimal(Math.abs(debitPrice)));
        }
    }

    private String getDecimal(Double value) {
        return String.format("%.2f", value);
    }
}
