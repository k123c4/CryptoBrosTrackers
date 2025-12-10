package com.example.cryptobrostrackers.ui.Watchlist;

import android.content.Intent;
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
import com.example.cryptobrostrackers.database.Coin;
import com.example.cryptobrostrackers.ui.dashboard.Dashboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.ViewHolder> {

    public interface OnDeleteClickListener {
        void onDeleteClick(Coin coin);
    }

    private final List<Coin> items = new ArrayList<>();
    private final OnDeleteClickListener deleteClickListener;

    public WatchlistAdapter(OnDeleteClickListener deleteClickListener) {
        this.deleteClickListener = deleteClickListener;
    }

    public void submitList(List<Coin> newItems) {
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
        Coin coin = items.get(position);

        String symbol = coin.symbol != null
                ? coin.symbol.toUpperCase(Locale.ENGLISH)
                : "";

        holder.txtName.setText(coin.name);
        holder.txtSymbol.setText(symbol);
        holder.txtPrice.setText(String.format(Locale.ENGLISH, "$%.2f", coin.price));
        holder.txtChange.setText(String.format(Locale.ENGLISH, "%.2f%%", coin.change24h));

        int color = (coin.change24h >= 0)
                ? ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_light)
                : ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_red_light);
        holder.txtChange.setTextColor(color);

        // Load image if we have one
        if (coin.imageUrl != null && !coin.imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(coin.imageUrl)
                    .into(holder.imgCoin);
        } else {
            holder.imgCoin.setImageResource(R.drawable.ic_launcher_foreground); // or any placeholder
        }

        // Delete button
        holder.deleteBt.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(coin);
            }
        });

        // Open Dashboard on row click (using snapshot values)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Dashboard.class);
            intent.putExtra("coin_id", coin.symbol);  // ID isn't super critical for chart; adjust if needed
            intent.putExtra("coin_name", coin.name);
            intent.putExtra("coin_symbol", coin.symbol);
            intent.putExtra("coin_price", coin.price);
            intent.putExtra("coin_change", coin.change24h);
            intent.putExtra("coin_cap", coin.marketCap);
            intent.putExtra("coin_image", coin.imageUrl);
            v.getContext().startActivity(intent);
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
