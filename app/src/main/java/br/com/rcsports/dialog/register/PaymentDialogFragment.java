package br.com.rcsports.dialog.register;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Stack;

import br.com.rcsports.MainActivity;
import br.com.rcsports.R;
import br.com.rcsports.adapter.PaymentsListAdapter;
import br.com.rcsports.dao.DAO;
import br.com.rcsports.fragment.SalesFragment;
import br.com.rcsports.listener.IOnClickListener;
import br.com.rcsports.model.Payment;
import br.com.rcsports.model.Sale;

/**
 * Created by Pedro on 25/12/2014.
 */
public class PaymentDialogFragment extends DialogFragment implements IOnClickListener {

    private Stack<Payment> stkPayments;
    private Stack<Double> stkPaidPrice;
    private Double totalPrice;
    private Double fixPaidPrice;
    private Double paidPrice;
    private Double debitPrice;
    private TextView txtTotalPrice;
    private TextView txtPaidPrice;
    private TextView txtDebitPrice;
    private TextView txtDebitPriceDescription;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_fragment_payment, container, false);

        stkPayments = new Stack<>();
        stkPaidPrice = new Stack<>();

        final String saleId = getArguments().getString("sale_id");
        String clientName = getArguments().getString("client_name");

        getDialog().setTitle(clientName);

        List<Payment> payments = DAO.open(getActivity()).findPaymentsWithSaleId(saleId);

        PaymentsListAdapter paymentsListAdapter = new PaymentsListAdapter(this, getActivity(), payments);

        ListView listViewPayments = (ListView) view.findViewById(R.id.list_payments);
        listViewPayments.setAdapter(paymentsListAdapter);

        Button btLeave = (Button) view.findViewById(R.id.payment_button_leave);
        Button btUpdate = (Button) view.findViewById(R.id.payment_button_update);

        final EditText edAddPayment = (EditText) view.findViewById(R.id.add_payment_price);
        ImageButton btSubPayment = (ImageButton) view.findViewById(R.id.sub_payment_button);

        txtTotalPrice = (TextView) view.findViewById(R.id.payment_total_price);
        txtPaidPrice = (TextView) view.findViewById(R.id.payment_paid_price);
        txtDebitPrice = (TextView) view.findViewById(R.id.payment_debit_price);
        txtDebitPriceDescription = (TextView) view.findViewById(R.id.payment_debit_text_price);

        Sale sale = DAO.open(getActivity()).findSaleById(saleId);

        totalPrice = sale.getTotal();

        fixPaidPrice = 0.0;
        for (Payment payment : payments) {
            fixPaidPrice += payment.getPaid();
        }

        debitPrice = totalPrice - fixPaidPrice;

        txtTotalPrice.setText(getDecimal(totalPrice));
        txtPaidPrice.setText(getDecimal(fixPaidPrice));
        checkDebit();

        paidPrice = 0.0;

        // Analisa se o botão de conclusão do teclado foi clicado para atualizar o PaidPrice
        edAddPayment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    if (edAddPayment.getText().length() > 0) {
                        paidPrice += Double.parseDouble(edAddPayment.getText().toString());
                        debitPrice = totalPrice - (fixPaidPrice + paidPrice);

                        stkPaidPrice.push(paidPrice);

                        txtTotalPrice.setText(getDecimal(totalPrice));
                        txtPaidPrice.setText(getDecimal(fixPaidPrice + paidPrice));
                        checkDebit();
                        edAddPayment.setText("");
                    }

                    // Esconde o Keyboard
                    MainActivity.hideSoftKeyboard(getActivity(), v);

                    return true;
                }
                return false;
            }
        });

        btSubPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // paidPrice = 0.0;
                if (!stkPaidPrice.empty()) {
                    stkPaidPrice.pop();
                    if (stkPaidPrice.empty()) {
                        paidPrice = 0.0;
                    } else {
                        paidPrice = stkPaidPrice.peek();
                    }
                } else {
                    paidPrice = 0.0;
                }

                debitPrice = totalPrice - (fixPaidPrice + paidPrice);
                txtTotalPrice.setText(getDecimal(totalPrice));
                txtPaidPrice.setText(getDecimal(fixPaidPrice + paidPrice));
                checkDebit();

                MainActivity.hideSoftKeyboard(getActivity(), v);
            }
        });

        btLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Payment payment : stkPayments) {
                    DAO.open(getActivity()).delete(payment);
                }
                stkPayments.clear();

                Double totalPaid = 0.0;
                for (Double paid : stkPaidPrice) {
                    totalPaid += paid;
                }

                if (totalPaid > 0.0) {

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String paymentDate = simpleDateFormat.format(Calendar.getInstance().getTime());

                    SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm a");
                    String paymentTime = simpleTimeFormat.format(Calendar.getInstance().getTime());

                    Payment payment = new Payment();
                    payment.setPaid(totalPaid);
                    payment.setDate(paymentDate);
                    payment.setTime(paymentTime);
                    payment.setSale_id(saleId);

                    DAO.open(getActivity()).insert(payment);
                }

                // Pede para atualizar a lista de vendas
                ((SalesFragment) getTargetFragment()).onCLickDelete(null);

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
        Payment payment = (Payment) object;
        stkPayments.push(payment);

        fixPaidPrice -= payment.getPaid();
        debitPrice = totalPrice - (fixPaidPrice + paidPrice);

        txtPaidPrice.setText(getDecimal(fixPaidPrice + paidPrice));
        checkDebit();
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

    public Double getDebitPrice() {
        return debitPrice;
    }

    private String getDecimal(Double value) {
        return String.format("%.2f", value);
    }
}
