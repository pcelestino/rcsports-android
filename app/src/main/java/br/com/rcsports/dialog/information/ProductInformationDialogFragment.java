package br.com.rcsports.dialog.information;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import br.com.rcsports.R;

/**
 * Created by Pedro on 19/12/2014.
 */
public class ProductInformationDialogFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_fragment_product_information, container);

        String name = getArguments().getString("product_name");
        Double price = getArguments().getDouble("product_price");

        TextView txtPrice = (TextView) view.findViewById(R.id.product_information_price);
        Button btOk = (Button) view.findViewById(R.id.product_information_button_ok);

        getDialog().setTitle(name);

        txtPrice.setText(String.format("%.2f", price) + " R$");

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }
}
