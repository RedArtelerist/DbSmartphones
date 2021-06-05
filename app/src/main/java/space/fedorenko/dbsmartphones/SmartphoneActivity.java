package space.fedorenko.dbsmartphones;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import space.fedorenko.dbsmartphones.smartphone.AutoCompleteCompanyAdapter;
import space.fedorenko.dbsmartphones.smartphone.CompanyItem;
import space.fedorenko.dbsmartphones.smartphone.Smartphone;
import space.fedorenko.dbsmartphones.smartphone.SmartphoneAdapter;

public class SmartphoneActivity extends AppCompatActivity implements SmartphoneAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private Button btnFilter;

    private List<CompanyItem> companyList;

    private SmartphoneAdapter adapter;
    private ProgressBar progressCircle;
    private FirebaseStorage storage;
    private DatabaseReference databaseRef;
    private ValueEventListener dbListener;
    private final List<String> companies = new ArrayList<>();

    private List<Smartphone> smartphones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smartphone);
        setTitle("Smartphones");

        fillCompanyList();
        AutoCompleteTextView textCompany = findViewById(R.id.edtCompany);
        AutoCompleteCompanyAdapter companyAdapter = new AutoCompleteCompanyAdapter(this, companyList);
        textCompany.setAdapter(companyAdapter);

        btnFilter = findViewById(R.id.btnFilter);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressCircle = findViewById(R.id.progressCircle);

        smartphones = new ArrayList<>();

        adapter = new SmartphoneAdapter(SmartphoneActivity.this, smartphones);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(SmartphoneActivity.this);

        storage = FirebaseStorage.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("smartphones");

        Query query = databaseRef.orderByChild("model");

        dbListener = query.addValueEventListener(valueEventListener);

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterQuery();
            }
        });
    }

    private void fillCompanyList() {
        companyList = new ArrayList<>();
        companyList.add(new CompanyItem("Apple", R.drawable.apple_logo));
        companyList.add(new CompanyItem("Samsung", R.drawable.samsung_logo));
        companyList.add(new CompanyItem("Xiaomi", R.drawable.xiaomi_logo));
        companyList.add(new CompanyItem("OnePlus", R.drawable.oneplus_logo));
        companyList.add(new CompanyItem("Oppo", R.drawable.opppo_logo));
        companyList.add(new CompanyItem("Huawei", R.drawable.huawei_logo));
        companyList.add(new CompanyItem("Honor", R.drawable.honor_logo));
        companyList.add(new CompanyItem("Realme", R.drawable.realme_logo));
        companyList.add(new CompanyItem("Vivo", R.drawable.vivo_logo));
        companyList.add(new CompanyItem("Google", R.drawable.google_logo));
        companyList.add(new CompanyItem("Motorola", R.drawable.motorola_logo));
        companyList.add(new CompanyItem("HTC", R.drawable.htc_logo));
        companyList.add(new CompanyItem("Nokia", R.drawable.nokia_logo));
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            smartphones.clear();
            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                Smartphone smartphone = postSnapshot.getValue(Smartphone.class);
                smartphone.setKey(postSnapshot.getKey());
                smartphones.add(smartphone);
            }
            getAverageScreen();
            adapter.notifyDataSetChanged();
            progressCircle.setVisibility(View.INVISIBLE);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Toast.makeText(SmartphoneActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            progressCircle.setVisibility(View.INVISIBLE);
        }
    };

    @Override
    public void onItemClick(int position) {
        //Toast.makeText(this, "Normal click at position: " + position, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onEditClick(int position) {
        Smartphone selectedItem = smartphones.get(position);
        Intent intent = new Intent(this, UpdateSmartphoneActivity.class);

        intent.putExtra("key", selectedItem.getKey());
        intent.putExtra("image", selectedItem.getImageUrl());
        intent.putExtra("company", selectedItem.getCompany());
        intent.putExtra("model", selectedItem.getModel());
        intent.putExtra("screen", String.valueOf(selectedItem.getScreen()));
        intent.putExtra("price", String.valueOf(selectedItem.getPrice()));
        intent.putExtra("address", selectedItem.getAddress());

        this.startActivity(intent);
    }

    @Override
    public void onRouteClick(int position) {
        Smartphone selectedItem = smartphones.get(position);
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("address", selectedItem.getAddress());
        this.startActivity(intent);
    }

    @Override
    public void onDeleteClick(int position) {
        Smartphone selectedItem = smartphones.get(position);
        final String selectedKey = selectedItem.getKey();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Delete " + selectedItem.getModel());
        builder.setMessage("Do you really want to delete this smartphone from database?");
        builder.setPositiveButton("Delete",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StorageReference imageRef = storage.getReferenceFromUrl(selectedItem.getImageUrl());
                        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                databaseRef.child(selectedKey).removeValue();
                                Toast.makeText(SmartphoneActivity.this, "Smartphone deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseRef.removeEventListener(dbListener);
    }

    private void filterQuery(){
        EditText edtCompany = findViewById(R.id.edtCompany);
        EditText edtScreen = findViewById(R.id.edtScreen);

        String company = edtCompany.getText().toString();
        String strScreen = edtScreen.getText().toString();

        Query query = databaseRef.orderByChild("model");
        if(company.length() != 0)
            query = databaseRef.orderByChild("company").equalTo(company);

        if(strScreen.length() == 0)
            dbListener = query.addValueEventListener(valueEventListener);
        else {
            double screen = Double.parseDouble(strScreen);
            Query finalQuery = query;
            dbListener = query.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    smartphones.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Smartphone smartphone = postSnapshot.getValue(Smartphone.class);
                        smartphone.setKey(postSnapshot.getKey());

                        try {
                            if (smartphone.getScreen() >= screen)
                                smartphones.add(smartphone);
                        } catch (Exception ex) {
                            Toast.makeText(SmartphoneActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                            finalQuery.addListenerForSingleValueEvent(valueEventListener);
                        }
                    }

                    getAverageScreen();

                    adapter.notifyDataSetChanged();
                    progressCircle.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(SmartphoneActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    progressCircle.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getAverageScreen(){
        TextView avgScreenTxt = findViewById(R.id.avgScreen);

        List<Double> sizes = new ArrayList<>();
        for(Smartphone smartphone : smartphones)
            sizes.add(smartphone.getScreen());

        double average = sizes.stream().mapToDouble(val -> val).average().orElse(0.0);
        double scale = Math.pow(10, 2);
        String strAvg =  Double.toString(Math.round(average * scale) / scale);
        avgScreenTxt.setText(strAvg);
    }
}