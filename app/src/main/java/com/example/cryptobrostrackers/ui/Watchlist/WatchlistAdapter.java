package com.example.cryptobrostrackers.ui.Watchlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cryptobrostrackers.R;
import com.example.cryptobrostrackers.model.CoinMarket;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.ViewHolder> {

    public interface OnDeleteClickListener {
        void onDeleteClick(String symbol);
    }

    private final List<CoinMarket> items = new ArrayList<>();
    private final OnDeleteClickListener deleteClickListener;

    public WatchlistAdapter(OnDeleteClickListener deleteClickListener) {
        this.deleteClickListener = deleteClickListener;
    }

    public void submitList(List<CoinMarket> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.watchlist_coin_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CoinMarket coin = items.get(position);

        String symbol = coin.getSymbol() != null
                ? coin.getSymbol().toUpperCase(Locale.ENGLISH)
                : "";

        holder.txtName.setText(coin.getName());
        holder.txtSymbol.setText(symbol);
        holder.txtPrice.setText(String.format(Locale.ENGLISH, "$%.2f", coin.getCurrentPrice()));
        holder.txtChange.setText(String.format(Locale.ENGLISH, "%.2f%%", coin.getPriceChangePct24h()));

        int color = (coin.getPriceChangePct24h() >= 0)
                ? ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_light)
                : ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_red_light);
        holder.txtChange.setTextColor(color);

        // Glide image
        Glide.with(holder.itemView.getContext())
                .load(coin.getImageUrl())
                .into(holder.imgCoin);

        holder.deleteBt.setOnClickListener(v -> {
            if (deleteClickListener != null && coin.getSymbol() != null) {
                deleteClickListener.onDeleteClick(coin.getSymbol());
            }
        });
        holder.itemView.setOnClickListener(v -> {
            android.content.Context context = v.getContext();
            android.content.Intent intent =
                    new android.content.Intent(context, com.example.cryptobrostrackers.ui.dashboard.Dashboard.class);

            intent.putExtra("coin_id", coin.getId());
            intent.putExtra("coin_name", coin.getName());
            intent.putExtra("coin_symbol", coin.getSymbol());
            intent.putExtra("coin_price", coin.getCurrentPrice());
            intent.putExtra("coin_change", coin.getPriceChangePct24h());
            intent.putExtra("coin_cap", coin.getMarketCap());
            intent.putExtra("coin_image", coin.getImageUrl());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgCoin;
        TextView txtName;
        TextView txtSymbol;
        TextView txtPrice;
        TextView txtChange;
        ImageButton deleteBt;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCoin = itemView.findViewById(R.id.imgCoin);
            txtName = itemView.findViewById(R.id.txtName);
            txtSymbol = itemView.findViewById(R.id.txtSymbol);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtChange = itemView.findViewById(R.id.txtChange);
            deleteBt = itemView.findViewById(R.id.deleteBt);
        }
    }
}
