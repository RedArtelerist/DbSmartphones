package space.fedorenko.dbsmartphones;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
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

public class MainActivity extends AppCompatActivity {
    private Button matrixBtn, smartphoneBtn, aboutBtn, helperBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Main menu");

        matrixBtn = findViewById(R.id.matrixActivityBtn);
        smartphoneBtn = findViewById(R.id.smartphoneActivityBtn);
        aboutBtn = findViewById(R.id.aboutActivityBtn);
        helperBtn = findViewById(R.id.helperActivityBtn);

        matrixBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMatrixCalculator();
            }
        });
        smartphoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSmartphoneDb();
            }
        });
        aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAboutActivity();
            }
        });
        helperBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHelperActivity();
            }
        });
    }

    private void openSmartphoneDb() {
        Intent intent = new Intent(this, CreateSmartphoneActivity.class);
        startActivity(intent);
    }

    private void openMatrixCalculator() {
        Intent intent = new Intent(this, MatrixActivity.class);
        startActivity(intent);
    }

    private void openAboutActivity() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    private void openHelperActivity() {
        Intent intent = new Intent(this, HelperActivity.class);
        startActivity(intent);
    }

}