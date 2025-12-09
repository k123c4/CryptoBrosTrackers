package com.example.cryptobrostrackers.ui.Watchlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptobrostrackers.R;
import com.example.cryptobrostrackers.database.Coin;

import java.util.ArrayList;
import java.util.List;

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

        // Right now we only store the symbol in Room
        holder.txtSymbol.setText(coin.symbol.toUpperCase());


        holder.deleteBt.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(coin);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtSymbol, txtPrice, txtChange;
        ImageButton deleteBt;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtSymbol = itemView.findViewById(R.id.txtSymbol);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtChange = itemView.findViewById(R.id.txtChange);
            deleteBt = itemView.findViewById(R.id.deleteBt);
        }
    }
}
