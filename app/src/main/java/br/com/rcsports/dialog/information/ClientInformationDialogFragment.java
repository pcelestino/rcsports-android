package br.com.rcsports.dialog.information;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import br.com.rcsports.R;

/**
 * Created by Pedro on 19/12/2014.
 */
public class ClientInformationDialogFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_fragment_client_information, container);

        String name = getArguments().getString("client_name");
        final String phone = getArguments().getString("client_phone");
        String clientAddress = getArguments().getString("client_address");

        TextView txtPhone = (TextView) view.findViewById(R.id.client_information_phone);
        TextView txtAddress = (TextView) view.findViewById(R.id.client_information_address);
        Button btOk = (Button) view.findViewById(R.id.client_information_button_ok);

        getDialog().setTitle(name);
        txtPhone.setText(phone);
        txtAddress.setText(clientAddress);

        final ImageButton btCall = (ImageButton) view.findViewById(R.id.client_information_button_call);

        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (!tabletSize) {

            btCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!phone.isEmpty()) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:+55" + phone));
                        startActivity(callIntent);
                    } else {
                        btCall.setVisibility(View.GONE);
                    }
                }
            });
        } else {

            btCall.setVisibility(View.GONE);
        }

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }
}
