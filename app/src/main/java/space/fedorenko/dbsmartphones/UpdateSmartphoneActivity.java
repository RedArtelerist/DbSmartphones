package space.fedorenko.dbsmartphones;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import space.fedorenko.dbsmartphones.smartphone.AutoCompleteCompanyAdapter;
import space.fedorenko.dbsmartphones.smartphone.CompanyItem;
import space.fedorenko.dbsmartphones.smartphone.Smartphone;

public class UpdateSmartphoneActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Button btnReplaceImage;
    private Button btnUpdate;
    private TextView back;

    private List<CompanyItem> companyList;

    private TextInputLayout inputCompany;
    private TextInputLayout inputModel;
    private TextInputLayout inputScreen;
    private TextInputLayout inputPrice;
    private TextInputLayout inputAddress;

    private ImageView imageView;
    private ProgressBar progressBar;
    private Uri imageUri;
    private String image;

    private StorageReference storageRef;
    private DatabaseReference databaseRef;
    private StorageTask updateTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_smartphone);
        setTitle("Update");

        btnReplaceImage = findViewById(R.id.btnReplaceImage);
        btnUpdate = findViewById(R.id.btnUpdate);
        back = findViewById(R.id.back);

        String key = getIntent().getStringExtra("key");
        image = getIntent().getStringExtra("image");
        String company = getIntent().getStringExtra("company");
        String model = getIntent().getStringExtra("model");
        String screen = getIntent().getStringExtra("screen");
        String price = getIntent().getStringExtra("price");
        String address = getIntent().getStringExtra("address");

        inputCompany = findViewById(R.id.inputCompany);
        inputCompany.getEditText().setText(company);

        inputModel = findViewById(R.id.inputModel);
        inputModel.getEditText().setText(model);

        inputScreen = findViewById(R.id.inputScreen);
        inputScreen.getEditText().setText(screen);

        inputPrice = findViewById(R.id.inputPrice);
        inputPrice.getEditText().setText(price);

        inputAddress = findViewById(R.id.inputAddress);
        inputAddress.getEditText().setText(address);

        imageView = findViewById(R.id.image);
        Picasso.get().load(image).into(imageView);
        progressBar = findViewById(R.id.progress_bar);

        storageRef = FirebaseStorage.getInstance().getReference("uploads");
        databaseRef = FirebaseDatabase.getInstance().getReference("smartphones");

        fillCompanyList();
        AutoCompleteTextView textCompany = findViewById(R.id.textCompany);
        AutoCompleteCompanyAdapter adapter = new AutoCompleteCompanyAdapter(this, companyList);
        textCompany.setAdapter(adapter);

        btnReplaceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateTask != null && updateTask.isInProgress()) {
                    Toast.makeText(UpdateSmartphoneActivity.this, "Update in progress", Toast.LENGTH_SHORT).show();
                } else {
                    if(checkFields())
                        updateSmartphone(key);
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); return;
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

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageView);
        }
    }

    private boolean validateCompany() {
        String company = inputCompany.getEditText().getText().toString().trim();
        Pattern p = Pattern.compile("^[A-Za-z\\s\\-]+$");
        if (company.isEmpty()) {
            inputCompany.setError("Field can't be empty");
            return false;
        }else if(company.length() > 15){
            inputCompany.setError("Company too long");
            return false;
        } else if(!p.matcher(company).matches()){
            inputCompany.setError("Company must contain only letters");
            return false;
        } else {
            inputCompany.setError(null);
            return true;
        }
    }

    private boolean validateModel() {
        String model = inputModel.getEditText().getText().toString().trim();
        if (model.isEmpty()) {
            inputModel.setError("Field can't be empty");
            return false;
        } else if (model.length() > 30) {
            inputModel.setError("Model too long");
            return false;
        } else {
            inputModel.setError(null);
            return true;
        }
    }

    private boolean validateScreen() {
        String screen = inputScreen.getEditText().getText().toString().trim();
        Pattern p = Pattern.compile("^[0-9](?:\\.[0-9]{1,2})?");
        if (screen.isEmpty()) {
            inputScreen.setError("Field can't be empty");
            return false;
        } else if (!p.matcher(screen).matches()) {
            inputScreen.setError("Invalid screen size format");
            return false;
        } else {
            inputScreen.setError(null);
            return true;
        }
    }

    private boolean validatePrice() {
        String price = inputPrice.getEditText().getText().toString();
        if (price.isEmpty()) {
            inputPrice.setError("Field can't be empty");
            return false;
        } else if (Integer.parseInt(price) < 1000 ||
                Integer.parseInt(price) > 1000000) {
            inputPrice.setError("Price must be from 1000 to 1000000");
            return false;
        } else {
            inputPrice.setError(null);
            return true;
        }
    }

    private boolean validateAddress() {
        String address = inputAddress.getEditText().getText().toString().trim();
        if (address.isEmpty()) {
            inputAddress.setError("Field can't be empty");
            return false;
        } else if (address.length() > 40) {
            inputAddress.setError("Address too long");
            return false;
        } else {
            inputAddress.setError(null);
            return true;
        }
    }

    private boolean checkFields() {
        return !(!validateCompany() | !validateModel() | !validateScreen() |
                !validatePrice() | !validateAddress());
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void updateSmartphone(String key) {
        if (imageUri != null) {
            StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            updateTask = fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setProgress(0);
                                        }
                                    }, 500);
                                    Toast.makeText(UpdateSmartphoneActivity.this, "Update successful", Toast.LENGTH_LONG).show();
                                    Smartphone smartphone = new Smartphone(
                                            inputCompany.getEditText().getText().toString(),
                                            inputModel.getEditText().getText().toString(),
                                            Double.parseDouble(inputScreen.getEditText().getText().toString()),
                                            Integer.parseInt(inputPrice.getEditText().getText().toString()),
                                            inputAddress.getEditText().getText().toString(),
                                            uri.toString()
                                    );

                                    databaseRef.child(key).setValue(smartphone);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UpdateSmartphoneActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.setProgress(0);
                }
            }, 500);
            Toast.makeText(UpdateSmartphoneActivity.this, "Update successful", Toast.LENGTH_LONG).show();
            Smartphone smartphone = new Smartphone(
                    inputCompany.getEditText().getText().toString(),
                    inputModel.getEditText().getText().toString(),
                    Double.parseDouble(inputScreen.getEditText().getText().toString()),
                    Integer.parseInt(inputPrice.getEditText().getText().toString()),
                    inputAddress.getEditText().getText().toString(),
                    image
            );

            databaseRef.child(key).setValue(smartphone);
        }
    }
}