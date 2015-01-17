package br.com.rcsports.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import br.com.rcsports.MainActivity;
import br.com.rcsports.R;
import br.com.rcsports.adapter.ProductsListAdapter;
import br.com.rcsports.dao.DAO;
import br.com.rcsports.dialog.information.ProductInformationDialogFragment;
import br.com.rcsports.dialog.register.ProductDialogFragment;
import br.com.rcsports.listener.IOnClickListener;
import br.com.rcsports.model.Product;

/**
 * Created by Pedro on 14/12/2014.
 */
public class ProductsFragment extends Fragment implements IOnClickListener {

    private ListView productList;
    private List<Product> products;
    private Button btRegister;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, null);

        products = DAO.open(getActivity()).getListProducts();

        productList = (ListView) view.findViewById(R.id.list_products);
        productList.setAdapter(new ProductsListAdapter(this, getActivity(), products));
        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager fragmentManager = getFragmentManager();
                ProductInformationDialogFragment productInformation = new ProductInformationDialogFragment();

                Product product = (Product) parent.getAdapter().getItem(position);

                Bundle bundle = new Bundle(2);
                bundle.putString("product_name", product.getName());
                bundle.putDouble("product_price", product.getPrice());

                productInformation.setArguments(bundle);
                productInformation.show(fragmentManager, "dialog_product_information");
            }
        });

        productList.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_DEL) {

                    Button btDelete = (Button) v.findViewById(R.id.product_map_delete);
                    btDelete.callOnClick();

                    return true;
                }
                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_INSERT) {

                    Button btEdit = (Button) v.findViewById(R.id.product_map_edit);
                    btEdit.callOnClick();

                    return true;
                }
                return false;
            }
        });

        productList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Button btDelete = (Button) view.findViewById(R.id.product_map_delete);
                btDelete.callOnClick();

                return true;
            }
        });

        // Botão Cadastrar
        btRegister = (Button) view.findViewById(R.id.add_product_button);
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                ProductDialogFragment productDialogFragment = new ProductDialogFragment();
                productDialogFragment.setTargetFragment(ProductsFragment.this, 0);
                productDialogFragment.show(fragmentManager, "dialog_product_register");
            }
        });

        return view;
    }

    @Override
    public void onClickSave() {
        productList.setAdapter(new ProductsListAdapter(this, getActivity(), DAO.open(getActivity()).getListProducts()));
    }

    @Override
    public void onCLickDelete(Object object) {
        if (object != null && products.size() > 0) {
            int position = (int) object;
            products.remove(position);
        }
        productList.invalidateViews();
        ((MainActivity) getActivity()).redraw(2);
    }

    @Override
    public void onDestroy() {
        if (DAO.open(getActivity()) != null) {
            DAO.open(getActivity()).close();
        }
        super.onDestroy();
    }

    @Override
    public String toString() {
        return "Produto";
    }

    // Necessário para o SearchView
    public ListView getProductList() {
        return productList;
    }

    public List<Product> getProducts() {
        return products;
    }
}
