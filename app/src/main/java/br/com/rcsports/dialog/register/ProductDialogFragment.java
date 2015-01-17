package br.com.rcsports.dialog.register;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import br.com.rcsports.R;
import br.com.rcsports.dao.DAO;
import br.com.rcsports.listener.IOnClickListener;
import br.com.rcsports.model.Product;

/**
 * Created by Pedro on 14/12/2014.
 */
public class ProductDialogFragment extends DialogFragment {

    private String productId;
    private EditText editTextName;
    private EditText editTextPrice;
    private Button btSave;

    private IOnClickListener callback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_fragment_product, container);
        getDialog().setTitle(R.string.registering_new_product);

        editTextName = (EditText) view.findViewById(R.id.dialog_product_name);
        editTextPrice = (EditText) view.findViewById(R.id.dialog_product_price);
        btSave = (Button) view.findViewById(R.id.dialog_product_button_save);
        Button btLeave = (Button) view.findViewById(R.id.dialog_product_button_leave);

        // Recupera informações do cliente para edição
        final Bundle bundle = getArguments();

        if (bundle != null) {
            getDialog().setTitle(R.string.edit_existing_product);
            productId = bundle.getString("product_id");
            String productName = bundle.getString("product_name");
            Double productPrice = bundle.getDouble("product_price");

            editTextName.setText(productName);
            editTextPrice.setText(String.valueOf(productPrice));
            btSave.setText(R.string.update);
        }

        callback = (IOnClickListener) getTargetFragment();

        editTextPrice.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    btSave.callOnClick();

                    return true;
                }
                return false;
            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Analisa se os campos estão preenchidos
                if (!editTextName.getText().toString().isEmpty() && !editTextPrice.getText().toString().isEmpty()) {

                    Product product = new Product();
                    product.setId(productId);
                    product.setName(editTextName.getText().toString());
                    product.setPrice(Double.parseDouble(editTextPrice.getText().toString()));

                    if (bundle == null) {
                        // Salvar
                        DAO.open(getActivity()).insert(product);
                    } else {
                        //Editar
                        DAO.open(getActivity()).update(product);
                    }

                    callback.onClickSave();
                    dismiss();

                } else {

                    Toast.makeText(getActivity(), "Preencha todos os campos", Toast.LENGTH_LONG).show();
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
}
