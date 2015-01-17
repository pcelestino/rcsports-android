package br.com.rcsports.dialog.register;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Stack;

import br.com.rcsports.MainActivity;
import br.com.rcsports.R;
import br.com.rcsports.adapter.ClientSpinnerAdapter;
import br.com.rcsports.adapter.ProductSpinnerAdapter;
import br.com.rcsports.adapter.TransactionsListAdapter;
import br.com.rcsports.dao.DAO;
import br.com.rcsports.listener.IOnClickListener;
import br.com.rcsports.model.Client;
import br.com.rcsports.model.Payment;
import br.com.rcsports.model.Product;
import br.com.rcsports.model.Sale;
import br.com.rcsports.model.Transaction;

/**
 * Created by Pedro on 14/12/2014.
 */
public class SaleDialogFragment extends DialogFragment implements IOnClickListener {

    // Number Picker Horizontal
    private EditText numberPicker;
    private ListView listViewProducts;
    private List<Transaction> transactionProducts;
    private TransactionsListAdapter transactionsListAdapter;
    private Transaction transactionProduct;
    private Product product;
    private EditText edPaidPrice;
    private TextView txtTotalPrice;
    private TextView txtPaidPrice;
    private TextView txtDebitPrice;
    private TextView txtDebitPriceDescription;
    private Stack<Double> stkPaidPrice;
    private Stack<Transaction> stkTransactionsToDelete;
    private String saleId;
    private Double totalPrice;
    private Double fixPaidPrice;
    private Double paidPrice;
    private Double debitPrice;
    private String saleDate;
    private String saleTime;
    private IOnClickListener callback;
    private Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_fragment_sale, container);
        getDialog().setTitle(R.string.registering_new_sale);

        callback = (IOnClickListener) getTargetFragment();

        final Spinner spinnerClients = (Spinner) view.findViewById(R.id.dialog_sale_clients);
        spinnerClients.setAdapter(new ClientSpinnerAdapter(getActivity(), DAO.open(getActivity()).getListClients()));

        final Spinner spinnerProducts = (Spinner) view.findViewById(R.id.dialog_sale_products);
        spinnerProducts.setAdapter(new ProductSpinnerAdapter(getActivity(), DAO.open(getActivity()).getListProducts()));

        ImageButton btAddProduct = (ImageButton) view.findViewById(R.id.dialog_sale_add_product_button);
        ImageButton btClearPrepayment = (ImageButton) view.findViewById(R.id.dialog_sale_sub_prepayment_button);
        edPaidPrice = (EditText) view.findViewById(R.id.dialog_sale_add_prepayment_price);
        txtPaidPrice = (TextView) view.findViewById(R.id.dialog_sale_paid_price);
        txtTotalPrice = (TextView) view.findViewById(R.id.dialog_sale_total_price);
        txtDebitPrice = (TextView) view.findViewById(R.id.dialog_sale_debit_price);
        txtDebitPriceDescription = (TextView) view.findViewById(R.id.dialog_sale_debit_text_price);

        // Acumula os valores
        stkPaidPrice = new Stack<>();
        stkTransactionsToDelete = new Stack<>();

        Button btSave = (Button) view.findViewById(R.id.dialog_sale_button_save);
        Button btLeave = (Button) view.findViewById(R.id.dialog_sale_button_leave);

        // Horizontal Number Picker
        numberPicker = (EditText) view.findViewById(R.id.sale_number_picker);

        listViewProducts = (ListView) view.findViewById(R.id.dialog_sale_list_products);
        transactionProducts = new ArrayList<>();

        transactionsListAdapter = new TransactionsListAdapter(this, getActivity(), transactionProducts);
        listViewProducts.setAdapter(transactionsListAdapter);

        totalPrice = 0.0;
        paidPrice = 0.0;
        debitPrice = 0.0;
        fixPaidPrice = 0.0;

        txtTotalPrice.setText(getDecimal(totalPrice));
        txtPaidPrice.setText(getDecimal(paidPrice));
        txtDebitPrice.setText(getDecimal(debitPrice));

        btAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product = (Product) spinnerProducts.getAdapter().getItem(spinnerProducts.getSelectedItemPosition());

                int amount = 1;
                if ((numberPicker.getText().toString() != null) &&
                        (numberPicker.getText().toString().length() > 0) &&
                        (Integer.parseInt(numberPicker.getText().toString()) > 0)) {
                    amount = Integer.valueOf(numberPicker.getText().toString());
                    numberPicker.setText(String.valueOf(amount));
                }

                for (Transaction transaction : transactionProducts) {
                    // Se o id do transaction for igual ao do produto, significa que ele já esta na
                    // lista, bastanto addim apenas aumentar sua quantidade
                    if (transaction.getProduct_id().equals(product.getId())) {
                        transaction.setAmount(transaction.getAmount() + amount);
                        transactionsListAdapter.notifyDataSetChanged();

                        // Calcula o valor total dos produtos
                        totalPrice += (product.getPrice() * amount);

                        txtTotalPrice.setText(getDecimal(totalPrice));

                        debitPrice = totalPrice - paidPrice;

                        checkDebit();

                        return;
                    }
                }

                transactionProduct = new Transaction();
                transactionProduct.setAmount(amount);
                transactionProduct.setProduct_id(product.getId());

                transactionProducts.add(transactionProduct);
                transactionsListAdapter.notifyDataSetChanged();

                // Calcula o valor total dos produtos
                totalPrice += (product.getPrice() * amount);

                txtTotalPrice.setText(getDecimal(totalPrice));

                debitPrice = totalPrice - paidPrice;

                checkDebit();
            }
        });

        // Analisa se o botão de conclusão do teclado foi clicado para atualizar o PaidPrice
        edPaidPrice.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    if (edPaidPrice.getText().length() > 0) {
                        paidPrice += Double.parseDouble(edPaidPrice.getText().toString());
                        debitPrice = totalPrice - (fixPaidPrice + paidPrice);

                        stkPaidPrice.push(paidPrice);

                        txtTotalPrice.setText(getDecimal(totalPrice));
                        txtPaidPrice.setText(getDecimal(fixPaidPrice + paidPrice));
                        checkDebit();
                        edPaidPrice.setText("");
                    }

                    // Esconde o Keyboard
                    MainActivity.hideSoftKeyboard(getActivity(), v);

                    // Esconde o Keyboard
                    MainActivity.hideSoftKeyboard(getActivity(), v);

                    return true;
                }
                return false;
            }
        });

        btClearPrepayment.setOnClickListener(new View.OnClickListener() {
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

        listViewProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final Transaction transaction = (Transaction) parent.getAdapter().getItem(position);
                Product product = (DAO.open(getActivity()).findProductById(transaction.getProduct_id()));

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                        .setTitle("Deseja excluir " + product.getName() + "?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                transactionProducts.remove(position);

                                Double productValue = (DAO.open(getActivity()).findProductById(transaction.getProduct_id())).getPrice();
                                totalPrice -= productValue * transaction.getAmount();
                                debitPrice = totalPrice - paidPrice;

                                txtTotalPrice.setText(getDecimal(totalPrice));

                                // Adiciona o id da transação a um pilha para ser removido do banco posteriormente
                                stkTransactionsToDelete.push(transaction);

                                transactionsListAdapter.notifyDataSetChanged();
                                checkDebit();
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

        listViewProducts.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_DEL) {

                    ListView listViewProducts = (ListView) v;
                    final int position = listViewProducts.getSelectedItemPosition();
                    final Transaction transaction = transactionProducts.get(position);
                    Product product = (DAO.open(getActivity()).findProductById(transaction.getProduct_id()));

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                            .setTitle("Deseja excluir " + product.getName() + "?")
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    transactionProducts.remove(position);

                                    Double productValue = (DAO.open(getActivity()).findProductById(transaction.getProduct_id())).getPrice();
                                    totalPrice -= productValue * transaction.getAmount();
                                    debitPrice = totalPrice - paidPrice;

                                    txtTotalPrice.setText(getDecimal(totalPrice));

                                    // Adiciona o id da transação a um pilha para ser removido do banco posteriormente
                                    stkTransactionsToDelete.push(transaction);

                                    transactionsListAdapter.notifyDataSetChanged();
                                    checkDebit();
                                }
                            })
                            .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    builder.show();

                    return true;
                }
                return false;
            }
        });

        // Recupera informações do cliente para edição
        bundle = getArguments();

        if (bundle != null) {
            getDialog().setTitle(R.string.edit_new_sale);

            saleId = bundle.getString("sale_id");
            saleDate = bundle.getString("sale_date");
            saleTime = bundle.getString("sale_time");

            String clientId = bundle.getString("sale_client_id");
            Client client = DAO.open(getActivity()).findClientById(clientId);

            List<Client> clients = new ArrayList<>();
            clients.add(client);
            spinnerClients.setAdapter(new ClientSpinnerAdapter(getActivity(), clients));

            fixPaidPrice = bundle.getDouble("sale_paid");
            totalPrice = bundle.getDouble("sale_total");
            debitPrice = totalPrice - fixPaidPrice;

            //stkPaidPrice.push(paidPrice);

            txtTotalPrice.setText(getDecimal(totalPrice));

            txtPaidPrice.setText(getDecimal(fixPaidPrice));

            checkDebit();

            transactionProducts = DAO.open(getActivity()).findTransactionsWithSaleId(saleId);

            transactionsListAdapter = new TransactionsListAdapter(SaleDialogFragment.this, getActivity(), transactionProducts);
            listViewProducts.setAdapter(transactionsListAdapter);

            btSave.setText(R.string.update);
        }

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listViewProducts.getCount() > 0) {

                    Client client = (Client) spinnerClients.getAdapter().getItem(spinnerClients.getSelectedItemPosition());

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String saleDateSave = simpleDateFormat.format(Calendar.getInstance().getTime());

                    SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");
                    String saleTimeSave = simpleTimeFormat.format(Calendar.getInstance().getTime());

                    Sale sale = new Sale();
                    sale.setId(saleId);
                    sale.setDate(saleDate);
                    sale.setTime(saleTime);
                    sale.setTotal(totalPrice);
                    sale.setClient_id(client.getId());

                    if (bundle == null) {
                        // Dessa forma mantém a data e a hora de criação da venda
                        sale.setDate(saleDateSave);
                        sale.setTime(saleTimeSave);

                        // Salvar
                        saleId = String.valueOf(DAO.open(getActivity()).insert(sale));

                        // Adiciona o id da venda na tabela Transaction
                        for (Transaction transaction : transactionProducts) {
                            transaction.setSale_id(saleId);
                            DAO.open(getActivity()).insert(transaction);
                        }
                    } else {
                        //Editar
                        DAO.open(getActivity()).update(sale);

                        // Atualiza a tabela de Transaction no banco de dados
                        for (Transaction transaction : transactionProducts) {

                            // Transação já existe no banco de dados
                            if (transaction.getSale_id() != null) {

                                DAO.open(getActivity()).update(transaction);

                            } else {

                                // Foi inserido um novo produto e ainda não existe no banco de dados
                                transaction.setSale_id(saleId);
                                DAO.open(getActivity()).insert(transaction);
                            }
                        }

                        // Remove do banco de dados os produtos deletados da lista de Transaction
                        while (!stkTransactionsToDelete.empty()) {
                            DAO.open(getActivity()).delete(stkTransactionsToDelete.pop());
                        }
                    }

                    if (paidPrice > 0) {

                        SimpleDateFormat simplePayTimeFormat = new SimpleDateFormat("hh:mm a");
                        String paymentTime = simplePayTimeFormat.format(Calendar.getInstance().getTime());

                        Payment payment = new Payment();
                        payment.setPaid(paidPrice);
                        payment.setDate(saleDateSave);
                        payment.setTime(paymentTime);
                        payment.setSale_id(saleId);

                        DAO.open(getActivity()).insert(payment);
                    }

                    callback.onClickSave();
                    stkTransactionsToDelete.clear();
                    dismiss();
                } else {
                    Toast.makeText(getActivity(), "Adicione um produto à venda", Toast.LENGTH_LONG).show();
                }

            }
        });

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

        Transaction trProduct = (Transaction) object;
        transactionsListAdapter.notifyDataSetChanged();
        Double productValue = (DAO.open(getActivity()).findProductById(trProduct.getProduct_id())).getPrice();
        totalPrice -= productValue * trProduct.getAmount();
        debitPrice = totalPrice - paidPrice;

        txtTotalPrice.setText(getDecimal(totalPrice));

        // Adiciona o id da transação a um pilha para ser removido do banco posteriormente
        stkTransactionsToDelete.push(trProduct);

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

    private String getDecimal(Double value) {
        return String.format("%.2f", value);
    }
}
