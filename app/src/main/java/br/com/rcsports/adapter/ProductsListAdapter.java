package br.com.rcsports.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.rcsports.MainActivity;
import br.com.rcsports.R;
import br.com.rcsports.dao.DAO;
import br.com.rcsports.dialog.exclusion.ProductExclusionDialogFragment;
import br.com.rcsports.dialog.register.ProductDialogFragment;
import br.com.rcsports.listener.IOnClickListener;
import br.com.rcsports.model.Product;

/**
 * Created by Pedro on 15/12/2014.
 */
public class ProductsListAdapter extends BaseAdapter implements Filterable {

    private Fragment fragment;
    private Context context;
    private IOnClickListener callback;

    private List<Product> products;
    private List<Product> productsFiltered;
    private LayoutInflater mInflater;
    private ItemFilter mFilter;
    private boolean isSearching;

    public ProductsListAdapter(Fragment fragment, Context context, List<Product> products) {
        this.context = context;

        this.products = products;
        this.productsFiltered = products;
        this.mInflater = LayoutInflater.from(context);

        this.fragment = fragment;
        this.callback = (IOnClickListener) fragment;
        this.isSearching = false;
    }

    @Override
    public int getCount() {
        return productsFiltered.size();
    }

    @Override
    public Object getItem(int position) {
        return productsFiltered.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ItemFilter();
        }
        return mFilter;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_products, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        final Product product = productsFiltered.get(position);

        holder.btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (DAO.open(context).findSalesWithProductId(product.getId()).size() > 0) {
                    ProductExclusionDialogFragment productExclusionDialogFragment = new ProductExclusionDialogFragment();
                    productExclusionDialogFragment.setTargetFragment(fragment, 0);
                    FragmentManager fragmentManager = fragment.getFragmentManager();

                    Bundle bundle = new Bundle();
                    bundle.putString("product_id", product.getId());
                    bundle.putInt("product_position", position);

                    productExclusionDialogFragment.setArguments(bundle);
                    productExclusionDialogFragment.show(fragmentManager, "list_dialog_exclusion_product");

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                            .setTitle("Deseja excluir " + product.getName() + "?")
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    // Se estiver contiver algo no SearchView é preciso deletar o item na posição do salesFiltered
                                    // e remover o item no vetor original localizado pelo ID porque nem sempre as posições dos dois
                                    // vetores são compatíveis
                                    if (isSearching) {
                                        // Remove a venda da tabela original com base na venda nova encontrada pelo search
                                        // Utilizei Iterator para escapar da ConcurrentModificationException
                                        for (Iterator<Product> i = products.iterator(); i.hasNext(); ) {
                                            Product oldProduct = i.next();
                                            if (oldProduct.getId().equals(product.getId())) {
                                                i.remove();
                                            }
                                        }

                                        productsFiltered.remove(position);

                                    } else {
                                        // Caso contrário basta remover o item na mesma posição dos dois vetores
                                        products.remove(position);
                                        productsFiltered = products;
                                    }

                                    DAO.open(context).delete(product);

                                    if (products.size() <= 0) {
                                        MainActivity main = (MainActivity) context;
                                        main.redraw(2);
                                    }

                                    callback.onCLickDelete(null);
                                }
                            })
                            .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    builder.show();
                }
            }
        });

        holder.btEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProductDialogFragment productDialogFragment = new ProductDialogFragment();
                productDialogFragment.setTargetFragment(fragment, 0);
                FragmentManager fragmentManager = fragment.getFragmentManager();

                Bundle bundle = new Bundle();
                bundle.putString("product_id", product.getId());
                bundle.putString("product_name", product.getName());
                bundle.putDouble("product_price", product.getPrice());

                productDialogFragment.setArguments(bundle);
                productDialogFragment.show(fragmentManager, "dialog_product_update");
            }
        });

        holder.btMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final PopupWindow popupWindow = new PopupWindow(v.getContext());

                LayoutInflater layoutInflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ScrollView scrollView = (ScrollView) layoutInflater.inflate(R.layout.popup_menu_product, null);

                // Creating the PopupWindow
                popupWindow.setContentView(scrollView);
                popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
                popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setFocusable(true);
                //Clear the default translucent background
                popupWindow.setBackgroundDrawable(v.getContext().getResources().getDrawable(android.R.color.transparent));
                popupWindow.showAsDropDown(v, -280, -90);

                // EDITAR PRODUTO
                TextView opEdit = (TextView) scrollView.findViewById(R.id.popup_product_edit);
                opEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ProductDialogFragment productDialogFragment = new ProductDialogFragment();
                        productDialogFragment.setTargetFragment(fragment, 0);
                        FragmentManager fragmentManager = fragment.getFragmentManager();

                        Bundle bundle = new Bundle();
                        bundle.putString("product_id", product.getId());
                        bundle.putString("product_name", product.getName());
                        bundle.putDouble("product_price", product.getPrice());

                        productDialogFragment.setArguments(bundle);
                        productDialogFragment.show(fragmentManager, "dialog_product_update");

                        popupWindow.dismiss();
                    }
                });

                // EXCLUIR PRODUTO
                TextView opDelete = (TextView) scrollView.findViewById(R.id.popup_product_delete);
                opDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (DAO.open(context).findSalesWithProductId(product.getId()).size() > 0) {
                            ProductExclusionDialogFragment productExclusionDialogFragment = new ProductExclusionDialogFragment();
                            productExclusionDialogFragment.setTargetFragment(fragment, 0);
                            FragmentManager fragmentManager = fragment.getFragmentManager();

                            Bundle bundle = new Bundle();
                            bundle.putString("product_id", product.getId());
                            bundle.putInt("product_position", position);

                            productExclusionDialogFragment.setArguments(bundle);
                            productExclusionDialogFragment.show(fragmentManager, "list_dialog_exclusion_product");

                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                                    .setTitle("Deseja excluir " + product.getName() + "?")
                                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            // Se estiver contiver algo no SearchView é preciso deletar o item na posição do salesFiltered
                                            // e remover o item no vetor original localizado pelo ID porque nem sempre as posições dos dois
                                            // vetores são compatíveis
                                            if (isSearching) {
                                                // Remove a venda da tabela original com base na venda nova encontrada pelo search
                                                // Utilizei Iterator para escapar da ConcurrentModificationException
                                                for (Iterator<Product> i = products.iterator(); i.hasNext(); ) {
                                                    Product oldProduct = i.next();
                                                    if (oldProduct.getId().equals(product.getId())) {
                                                        i.remove();
                                                    }
                                                }

                                                productsFiltered.remove(position);

                                            } else {
                                                // Caso contrário basta remover o item na mesma posição dos dois vetores
                                                products.remove(position);
                                                productsFiltered = products;
                                            }

                                            DAO.open(context).delete(product);

                                            if (products.size() <= 0) {
                                                MainActivity main = (MainActivity) context;
                                                main.redraw(2);
                                            }

                                            callback.onCLickDelete(null);
                                        }
                                    })
                                    .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                            builder.show();
                        }
                        popupWindow.dismiss();
                    }
                });
            }
        });

        // Adiciona os nome e o preço com base no resultado do filtro
        holder.txtProductName.setText(productsFiltered.get(position).getName());
        holder.txtProductPrice.setText(String.format("%.2f", productsFiltered.get(position).getPrice()) + " R$");

        return convertView;
    }

    private class ViewHolder {

        private TextView txtProductName;
        private TextView txtProductPrice;

        private Button btEdit;
        private Button btDelete;

        ImageButton btMenu;

        public ViewHolder(View v) {
            txtProductName = (TextView) v.findViewById(R.id.product_name);
            txtProductPrice = (TextView) v.findViewById(R.id.product_price);

            btEdit = (Button) v.findViewById(R.id.product_map_edit);
            btDelete = (Button) v.findViewById(R.id.product_map_delete);

            btMenu = (ImageButton) v.findViewById(R.id.product_menu_button);
        }
    }

    private class ItemFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            if (constraint.length() > 0) isSearching = true;
            else isSearching = false;

            FilterResults results = new FilterResults();

            String filterString = constraint.toString().toLowerCase();
            final int stringLength = filterString.length();
            List<Product> tempProducts = new ArrayList<>();

            for (Product product : products) {
                if (stringLength < product.getName().length()) {
                    if (product.getName().toLowerCase().contains(filterString.toLowerCase())) {
                        tempProducts.add(product);
                    }
                }
            }

            results.values = tempProducts;
            results.count = tempProducts.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            productsFiltered = (List<Product>) results.values;
            notifyDataSetChanged();
        }
    }
}
