package br.com.rcsports.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.rcsports.R;
import br.com.rcsports.dao.DAO;
import br.com.rcsports.dialog.register.PaymentDialogFragment;
import br.com.rcsports.listener.IOnClickListener;
import br.com.rcsports.model.Payment;
import br.com.rcsports.model.Sale;

/**
 * Created by Pedro on 25/12/2014.
 */
public class PaymentsListAdapter extends BaseAdapter {

    private Context context;
    private List<Payment> payments;
    private IOnClickListener callback;
    private Fragment fragment;

    public PaymentsListAdapter(Fragment fragment, Context context, List<Payment> payments) {
        this.fragment = fragment;
        this.callback = (IOnClickListener) fragment;
        this.context = context;
        this.payments = payments;
    }

    @Override
    public int getCount() {
        return payments.size();
    }

    @Override
    public Object getItem(int position) {
        return payments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_payments, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Payment payment = payments.get(position);

        holder.btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("Deseja Excluir o Pagamento?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                payments.remove(position);
                                callback.onCLickDelete(payment);
                                notifyDataSetChanged();
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

        holder.txtPaymentDate.setText(payment.getDate());
        holder.txtPaymentTime.setText(payment.getTime());
        holder.txtPaymentPaid.setText(getDecimal(payment.getPaid()) + " R$");

        return convertView;
    }

    private String getDecimal(Double value) {
        return String.format("%.2f", value);
    }

    private class ViewHolder {
        private TextView txtPaymentDate;
        private TextView txtPaymentTime;
        private TextView txtPaymentPaid;
        private ImageButton btDelete;

        private ViewHolder(View v) {
            txtPaymentDate = (TextView) v.findViewById(R.id.payment_date);
            txtPaymentTime = (TextView) v.findViewById(R.id.payment_time);
            txtPaymentPaid = (TextView) v.findViewById(R.id.payment_paid);
            btDelete = (ImageButton) v.findViewById(R.id.payment_delete_button);
        }
    }
}
