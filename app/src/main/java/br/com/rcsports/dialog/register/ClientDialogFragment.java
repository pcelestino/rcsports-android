package br.com.rcsports.dialog.register;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import br.com.rcsports.R;
import br.com.rcsports.custom.MaskPhone;
import br.com.rcsports.dao.DAO;
import br.com.rcsports.listener.IOnClickListener;
import br.com.rcsports.model.Client;

/**
 * Created by Pedro on 14/12/2014.
 */
public class ClientDialogFragment extends DialogFragment {

    private String clientId;
    private EditText editTextName;
    private EditText editTextPhone;
    private EditText editTextAddress;

    private IOnClickListener callback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_fragment_client, container);
        getDialog().setTitle(R.string.registering_new_client);

        editTextName = (EditText) view.findViewById(R.id.dialog_client_name);
        editTextPhone = (EditText) view.findViewById(R.id.dialog_client_phone);
        editTextAddress = (EditText) view.findViewById(R.id.dialog_client_address);
        Button btSave = (Button) view.findViewById(R.id.dialog_client_button_save);
        Button btLeave = (Button) view.findViewById(R.id.dialog_client_button_leave);

        editTextPhone.addTextChangedListener(MaskPhone.insert("(##)####-####", editTextPhone));

        // Recupera informações do cliente para edição
        final Bundle bundle = getArguments();

        if (bundle != null) {
            getDialog().setTitle(R.string.edit_existing_client);
            clientId = bundle.getString("client_id");
            String clientName = bundle.getString("client_name");
            String clientPhone = bundle.getString("client_phone");
            String clientAddress = bundle.getString("client_address");

            editTextName.setText(clientName);
            editTextPhone.setText(clientPhone);
            editTextAddress.setText(clientAddress);
            btSave.setText(R.string.update);
        }

        callback = (IOnClickListener) getTargetFragment();

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String clientPhone = editTextPhone.getText().toString();
                if (!editTextName.getText().toString().isEmpty() &&
                        (clientPhone.isEmpty() || clientPhone.length() == 13)) {
                    Client client = new Client();
                    client.setId(clientId);
                    client.setName(editTextName.getText().toString());
                    client.setPhone(editTextPhone.getText().toString());
                    client.setAddress(editTextAddress.getText().toString());

                    if (bundle == null) {
                        // Salvar
                        DAO.open(getActivity()).insert(client);
                    } else {
                        //Editar
                        DAO.open(getActivity()).update(client);
                    }

                    callback.onClickSave();
                    dismiss();
                } else {
                    if (clientPhone.length() != 13 && clientPhone.length() > 0) {
                        Toast.makeText(getActivity(), "Telefone inválido", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "Preencha o nome do cliente", Toast.LENGTH_LONG).show();
                    }

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
