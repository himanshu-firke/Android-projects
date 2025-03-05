package com.example.practice;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends Activity {

    private EditText nameInput, numberInput;
    private ListView contactsList;
    private Button saveButton, callButton;
    private ArrayList<String> contacts;
    private ArrayAdapter<String> adapter;
    private String selectedContact = "";
    private SharedPreferences sharedPreferences;

    private static final int CALL_PERMISSION_REQUEST = 1;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        nameInput = findViewById(R.id.nameInput);
        numberInput = findViewById(R.id.numberInput);
        contactsList = findViewById(R.id.contactsList);
        saveButton = findViewById(R.id.saveButton);
        callButton = findViewById(R.id.callButton);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("ContactsData", MODE_PRIVATE);

        // Load saved contacts
        contacts = loadContacts();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, contacts);
        contactsList.setAdapter(adapter);
        contactsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // Save Button - Adds contact to the list
        saveButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String number = numberInput.getText().toString().trim();

            if (!name.isEmpty() && !number.isEmpty()) {
                String contact = name + " - " + number;
                contacts.add(contact);
                adapter.notifyDataSetChanged();
                saveContacts();
                nameInput.setText("");
                numberInput.setText("");
                Toast.makeText(MainActivity.this, "Contact saved!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Enter valid name and number", Toast.LENGTH_SHORT).show();
            }
        });

        // Selecting a contact from the list
        contactsList.setOnItemClickListener((parent, view, position, id) -> selectedContact = contacts.get(position));

        // Call Button - Handles fake calls
        callButton.setOnClickListener(v -> {
            if (!selectedContact.isEmpty()) {
                String[] contactParts = selectedContact.split(" - ");
                if (contactParts.length == 2) {
                    String phoneNumber = contactParts[1].trim();
                    initiateFakeCall(phoneNumber);
                }
            } else {
                Toast.makeText(MainActivity.this, "Select a contact first!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to trigger a fake call instead of a real call
    private void initiateFakeCall(String phoneNumber) {
        Toast.makeText(MainActivity.this, "Incoming fake call in 3 seconds...", Toast.LENGTH_SHORT).show();
        handler.postDelayed(() -> {
            Intent fakeCallIntent = new Intent(MainActivity.this, FakeCallActivity.class);
            fakeCallIntent.putExtra("caller_number", phoneNumber); // Pass fake number
            startActivity(fakeCallIntent);
        }, 3000); // Delay to simulate a real call coming in after 3 seconds
    }

    private void saveContacts() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(contacts);
        editor.putString("contacts_list", json);
        editor.apply();
    }

    private ArrayList<String> loadContacts() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("contacts_list", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return json != null ? gson.fromJson(json, type) : new ArrayList<>();
    }
}
