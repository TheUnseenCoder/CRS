package com.advance.crs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<CategoryItem> categories;
    private OnCategoryClickListener listener;
    private Context context;

    public interface OnCategoryClickListener {
        void onCategoryClick(CategoryItem category);
    }

    public CategoryAdapter(Context context, List<CategoryItem> categories, OnCategoryClickListener listener) {
        this.context = context;
        this.categories = categories;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView productName1, productName2;
        public RelativeLayout categoryLayout1, categoryLayout2;

        public ViewHolder(View itemView) {
            super(itemView);
            productName1 = itemView.findViewById(R.id.product_name_1);
            productName2 = itemView.findViewById(R.id.product_name_2);
            categoryLayout1 = itemView.findViewById(R.id.category_layout_1);
            categoryLayout2 = itemView.findViewById(R.id.category_layout_2);

            categoryLayout1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onCategoryClick(categories.get(position * 2));
                    }
                }
            });

            categoryLayout2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onCategoryClick(categories.get(position * 2 + 1));
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int firstProductIndex = position * 2;
        CategoryItem category1 = categories.get(firstProductIndex);
        holder.productName1.setText(category1.getName());


        if (firstProductIndex + 1 < categories.size()) {
            // If there is a second product available, display it
            CategoryItem category2 = categories.get(firstProductIndex + 1);
            holder.productName2.setText(category2.getName());

            // Make sure the second layout is visible
            holder.productName2.setVisibility(View.VISIBLE);
        } else {
            // If there is no second product, hide the second layout
            holder.productName2.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return (int) Math.ceil(categories.size() / 2.0);
    }
}
