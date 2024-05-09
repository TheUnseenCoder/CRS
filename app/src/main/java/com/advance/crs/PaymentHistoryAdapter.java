package com.advance.crs;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PaymentHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private Context context;
    private List<PaymentHistoryItem> itemList;

    public PaymentHistoryAdapter(Context context, List<PaymentHistoryItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_HEADER) {
            view = LayoutInflater.from(context).inflate(R.layout.payment_header_row_layout, parent, false);
            return new HeaderViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.payment_item_layout, parent, false);
            return new CustomViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            CustomViewHolder viewHolder = (CustomViewHolder) holder;
            PaymentHistoryItem item = itemList.get(position - 1); // Subtract 1 to adjust for header
            viewHolder.bind(item);
        }
    }

    @Override
    public int getItemCount() {
        // Add 1 to include the header row
        return itemList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    private static class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView transactionIdTextView;
        private TextView payerIdTextView;
        private TextView payerFullnameTextView;
        private TextView orderIdTextView;
        private TextView paymentStatusTextView;
        private TextView dateTimeTextView;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            transactionIdTextView = itemView.findViewById(R.id.transaction_id_text_view);
            payerIdTextView = itemView.findViewById(R.id.payer_id_text_view);
            payerFullnameTextView = itemView.findViewById(R.id.payer_fullname_text_view);
            orderIdTextView = itemView.findViewById(R.id.order_id_text_view);
            paymentStatusTextView = itemView.findViewById(R.id.payment_status_text_view);
            dateTimeTextView = itemView.findViewById(R.id.date_time_text_view);
        }

        public void bind(PaymentHistoryItem item) {
            if(item.getPaymentStatus().equals("partially paid")){
                paymentStatusTextView.setText("Partially Paid");
                paymentStatusTextView.setTextColor(Color.parseColor("#FE9705"));
            }else{
                paymentStatusTextView.setText("Fully Paid");
                paymentStatusTextView.setTextColor(Color.parseColor("#3AC430"));
            }
            payerIdTextView.setText(item.getPayerId());
            payerFullnameTextView.setText(item.getPayerFullname());
            orderIdTextView.setText(String.valueOf(item.getOrderId()));
            transactionIdTextView.setText(item.getTransactionId());

            String dateTimeStr = item.getTimeStamp();
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM d, yyyy  hh:mm a", Locale.getDefault());

            try {
                Date dateTime = inputFormat.parse(dateTimeStr);
                String formattedDateTime = outputFormat.format(dateTime);
                dateTimeTextView.setText(formattedDateTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
