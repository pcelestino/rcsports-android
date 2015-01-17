package br.com.rcsports.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.com.rcsports.R;

/**
 * Created by Pedro on 17/12/2014.
 */
public class HorizontalNumberPicker extends LinearLayout {

    private Integer value;
    private TextView txtValue;

    public HorizontalNumberPicker(final Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.number_picker_horizontal, this);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.HorizontalNumberPicker);

        String myValue = attributes.getString(R.styleable.HorizontalNumberPicker_value);
        txtValue = (TextView) findViewById(R.id.number_picker_value);
        txtValue.setText(myValue);

        attributes.recycle();

        value = getValue();
        Button btMinus = (Button) findViewById(R.id.number_picker_btn_minus);
        btMinus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getValue() <= 1) {
                    setValue(value = 1);
                } else {
                    setValue(--value);
                }
            }
        });

        Button btPlus = (Button) findViewById(R.id.number_picker_btn_plus);
        btPlus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setValue(++value);
            }
        });
    }

    public Integer getValue() {
        if (txtValue.getText().toString().equals("")) {
            return 1;
        }
        return Integer.valueOf(txtValue.getText().toString());
    }

    public void setValue(Integer value) {
        txtValue.setText(String.valueOf(value));
    }
}
