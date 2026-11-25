package com.example.rentlanka;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

    private final List<Vehicle> vehicleList;

    public VehicleAdapter(List<Vehicle> vehicleList) {
        this.vehicleList = vehicleList;
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vehicle, parent, false);
        return new VehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        Vehicle v = vehicleList.get(position);

        holder.tvName.setText(v.name);
        holder.tvInfo.setText(v.type + " • " + v.district);
        holder.tvPrice.setText("Rs. " + v.pricePerDay + " / day");
        holder.tvDriver.setText(v.withDriver ? "With driver available" : "Self drive");
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    static class VehicleViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvInfo, tvPrice, tvDriver;

        VehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvVehicleName);
            tvInfo = itemView.findViewById(R.id.tvVehicleInfo);
            tvPrice = itemView.findViewById(R.id.tvVehiclePrice);
            tvDriver = itemView.findViewById(R.id.tvVehicleDriver);
        }
    }
}