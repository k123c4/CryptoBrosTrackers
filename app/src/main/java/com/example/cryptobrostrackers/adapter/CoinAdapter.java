package com.example.cryptobrostrackers.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cryptobrostrackers.R;
import com.example.cryptobrostrackers.model.CoinMarket;
import com.example.cryptobrostrackers.ui.dashboard.Dashboard;

import java.util.ArrayList;
import java.util.List;

public class CoinAdapter extends RecyclerView.Adapter<CoinAdapter.CoinViewHolder> {

    private Context context;
    private List<CoinMarket> coins = new ArrayList<>();

    public CoinAdapter(Context context, List<CoinMarket> coins) {
        this.context = context;
        if(coins != null) {
            this.coins.addAll(coins);
        }
    }

    public void updateData(List<CoinMarket> newCoins) {
        coins.clear();
        if(newCoins != null) {
            coins.addAll(newCoins);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CoinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.coin_item, parent, false);
        return new CoinViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoinViewHolder holder, int position) {
        CoinMarket coin = coins.get(position);

        holder.txtName.setText(coin.getName());
        holder.txtSymbol.setText(coin.getSymbol().toUpperCase());
        holder.txtPrice.setText("$" + coin.getCurrentPrice());
        holder.txtChange.setText(String.format("%.2f%%", coin.getPriceChangePct24h()));

        // Color for change
        int color = (coin.getPriceChangePct24h() >= 0)
                ? ContextCompat.getColor(context, android.R.color.holo_green_light)
                : ContextCompat.getColor(context, android.R.color.holo_red_light);

        holder.txtChange.setTextColor(color);

        // Load image with Glide
        Glide.with(context)
                .load(coin.getImageUrl())
                .into(holder.imgCoin);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Dashboard.class);
            intent.putExtra("coin_id", coin.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return coins.size();
    }

    static class CoinViewHolder extends RecyclerView.ViewHolder {

        ImageView imgCoin;
        TextView txtName;
        TextView txtSymbol;
        TextView txtPrice;
        TextView txtChange;

        public CoinViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCoin = itemView.findViewById(R.id.imgCoin);
            txtName = itemView.findViewById(R.id.txtName);
            txtSymbol = itemView.findViewById(R.id.txtSymbol);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtChange = itemView.findViewById(R.id.txtChange);
        }
    }
}
