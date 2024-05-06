package com.advance.crs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<Product> products;
    private OnProductClickListener listener;
    private Context context;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public ProductAdapter(Context context, List<Product> products, OnProductClickListener listener) {
        this.context = context;
        this.products = products;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView productImage1, productImage2;
        public TextView productName1, productName2;
        public TextView productPrice1, productPrice2;
        public TextView pax1, pax2;
        public TextView productDescription1, productDescription2;
        RelativeLayout rl1, rl2;
        CardView card_view_2;

        public ViewHolder(View itemView) {
            super(itemView);
            productImage1 = itemView.findViewById(R.id.product_image_1);
            productImage2 = itemView.findViewById(R.id.product_image_2);
            productName1 = itemView.findViewById(R.id.product_name_1);
            productName2 = itemView.findViewById(R.id.product_name_2);
            productPrice1 = itemView.findViewById(R.id.product_price_1);
            productPrice2 = itemView.findViewById(R.id.product_price_2);
            productDescription1 = itemView.findViewById(R.id.product_description_1);
            productDescription2 = itemView.findViewById(R.id.product_description_2);
            pax1 = itemView.findViewById(R.id.product_pax_1);
            pax2 = itemView.findViewById(R.id.product_pax_2);
            rl2 = itemView.findViewById(R.id.product_layout_2);
            rl1 = itemView.findViewById(R.id.product_layout_1);
            card_view_2 = itemView.findViewById(R.id.card_view_2);




            productImage1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onProductClick(products.get(position * 2));
                    }
                }
            });

            productImage2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onProductClick(products.get(position * 2 + 1));
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int firstProductIndex = position * 2;
        Product product1 = products.get(firstProductIndex);
        holder.productName1.setText(product1.getName());
        holder.productPrice1.setText("₱" + product1.getBasePrice());
        holder.productDescription1.setText(product1.getDescription());
        holder.pax1.setText(String.valueOf("PAX: " + product1.getPax())); // Convert Integer to String
        Glide.with(context)
                .load(product1.getImage())
                .placeholder(R.drawable.default_product_image)
                .into(holder.productImage1);

        if (firstProductIndex + 1 < products.size()) {
            // If there is a second product available, display it
            Product product2 = products.get(firstProductIndex + 1);
            holder.productName2.setText(product2.getName());
            holder.productPrice2.setText("₱" + product2.getBasePrice());
            holder.productDescription2.setText(product2.getDescription());
            holder.pax2.setText(String.valueOf(product2.getPax())); // Convert Integer to String
            Glide.with(context)
                    .load(product2.getImage())
                    .placeholder(R.drawable.default_product_image)
                    .into(holder.productImage2);
            // Make sure the second layout is visible
            holder.productImage2.setVisibility(View.VISIBLE);
            holder.productName2.setVisibility(View.VISIBLE);
            holder.productPrice2.setVisibility(View.VISIBLE);
            holder.productDescription2.setVisibility(View.VISIBLE);
            holder.pax2.setVisibility(View.VISIBLE);
        } else {
            // If there is no second product, hide the second layout
            holder.productImage2.setVisibility(View.INVISIBLE);
            holder.productName2.setVisibility(View.INVISIBLE);
            holder.productPrice2.setVisibility(View.INVISIBLE);
            holder.productDescription2.setVisibility(View.INVISIBLE);
            holder.pax2.setVisibility(View.INVISIBLE);
            holder.rl2.setVisibility(View.INVISIBLE);
            holder.card_view_2.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return (int) Math.ceil(products.size() / 2.0);
    }
}
