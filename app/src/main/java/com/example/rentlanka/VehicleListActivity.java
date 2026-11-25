package com.example.rentlanka;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class VehicleListActivity extends AppCompatActivity {

    private RecyclerView rvVehicles;
    private TextView tvEmpty;
    private EditText etSearch;
    private Spinner spFilterType;
    private CheckBox cbFilterDriver;

    // Firestore එකෙන් ආපු හැම වාහනයක්ම
    private final List<Vehicle> allVehicles = new ArrayList<>();
    // Filter කරපු පස්සේ screen එකේ පෙන්නන වාහන
    private final List<Vehicle> vehicleList = new ArrayList<>();
    private VehicleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_list);

        rvVehicles = findViewById(R.id.rvVehicles);
        tvEmpty = findViewById(R.id.tvEmpty);
        etSearch = findViewById(R.id.etSearch);
        spFilterType = findViewById(R.id.spFilterType);
        cbFilterDriver = findViewById(R.id.cbFilterDriver);

        adapter = new VehicleAdapter(vehicleList);
        rvVehicles.setLayoutManager(new LinearLayoutManager(this));
        rvVehicles.setAdapter(adapter);

        String[] types = {"All types", "Car", "Van", "Tuk-tuk", "Motorbike", "Bicycle"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, types);
        spFilterType.setAdapter(typeAdapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        spFilterType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        cbFilterDriver.setOnCheckedChangeListener((buttonView, isChecked) -> applyFilters());

        loadVehicles();
    }

    private void loadVehicles() {
        FirebaseFirestore.getInstance().collection("vehicles")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    allVehicles.clear();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Vehicle v = doc.toObject(Vehicle.class);
                        if (v != null && v.available) {
                            v.id = doc.getId();
                            allVehicles.add(v);
                        }
                    }
                    applyFilters();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
    }

    private void applyFilters() {
        String query = etSearch.getText().toString().trim().toLowerCase();
        String selectedType = spFilterType.getSelectedItem().toString();
        boolean onlyWithDriver = cbFilterDriver.isChecked();

        vehicleList.clear();
        for (Vehicle v : allVehicles) {
            boolean matchesText = query.isEmpty()
                    || (v.name != null && v.name.toLowerCase().contains(query))
                    || (v.district != null && v.district.toLowerCase().contains(query));

            boolean matchesType = selectedType.equals("All types")
                    || selectedType.equals(v.type);

            boolean matchesDriver = !onlyWithDriver || v.withDriver;

            if (matchesText && matchesType && matchesDriver) {
                vehicleList.add(v);
            }
        }

        adapter.notifyDataSetChanged();
        tvEmpty.setVisibility(vehicleList.isEmpty() ? View.VISIBLE : View.GONE);
    }
}