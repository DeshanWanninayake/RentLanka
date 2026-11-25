package com.example.rentlanka;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddVehicleActivity extends AppCompatActivity {

    private EditText etVehicleName, etDistrict, etPrice, etDescription;
    private Spinner spVehicleType;
    private CheckBox cbWithDriver;
    private Button btnSaveVehicle;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        db = FirebaseFirestore.getInstance();

        etVehicleName = findViewById(R.id.etVehicleName);
        etDistrict = findViewById(R.id.etDistrict);
        etPrice = findViewById(R.id.etPrice);
        etDescription = findViewById(R.id.etDescription);
        spVehicleType = findViewById(R.id.spVehicleType);
        cbWithDriver = findViewById(R.id.cbWithDriver);
        btnSaveVehicle = findViewById(R.id.btnSaveVehicle);

        String[] types = {"Car", "Van", "Tuk-tuk", "Motorbike", "Bicycle"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, types);
        spVehicleType.setAdapter(adapter);

        btnSaveVehicle.setOnClickListener(v -> saveVehicle());
    }

    private void saveVehicle() {
        String name = etVehicleName.getText().toString().trim();
        String district = etDistrict.getText().toString().trim();
        String priceText = etPrice.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String type = spVehicleType.getSelectedItem().toString();
        boolean withDriver = cbWithDriver.isChecked();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(district)
                || TextUtils.isEmpty(priceText)) {
            Toast.makeText(this, "Please fill name, district and price", Toast.LENGTH_SHORT).show();
            return;
        }

        long price;
        try {
            price = Long.parseLong(priceText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter a valid price", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSaveVehicle.setEnabled(false);

        Map<String, Object> vehicle = new HashMap<>();
        vehicle.put("name", name);
        vehicle.put("type", type);
        vehicle.put("district", district);
        vehicle.put("pricePerDay", price);
        vehicle.put("withDriver", withDriver);
        vehicle.put("description", description);
        vehicle.put("ownerId", user.getUid());
        vehicle.put("available", true);
        vehicle.put("createdAt", System.currentTimeMillis());

        db.collection("vehicles")
                .add(vehicle)
                .addOnSuccessListener(ref -> {
                    Toast.makeText(this, "Vehicle saved!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnSaveVehicle.setEnabled(true);
                    Toast.makeText(this, "Failed to save: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}