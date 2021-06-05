package space.fedorenko.dbsmartphones;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import space.fedorenko.dbsmartphones.smartphone.ContactAdapter;
import space.fedorenko.dbsmartphones.smartphone.ContactModel;

public class ContactActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    Button findBtn;
    ArrayList<ContactModel> arrayList = new ArrayList<ContactModel>();
    ContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        setTitle("Contacts");

        recyclerView = findViewById(R.id.recycler_view);
        findBtn = findViewById(R.id.btnFind);
        checkPermission();

        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterBySize();
            }
        });
    }

    private void filterBySize(){
        EditText editText = findViewById(R.id.lastNameSize);
        String size = editText.getText().toString();

        if(size.length() != 0){
            getContacts(Integer.parseInt(size));
        } else {
            getContacts(0);
        }
    }

    private void checkPermission() {
        if(ContextCompat.checkSelfPermission(ContactActivity.this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ContactActivity.this,
                    new String[]{Manifest.permission.READ_CONTACTS}, 100);
        } else {
            getContacts(0);
        }
    }

    private void getContacts(int size){
        arrayList.clear();

        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
        Cursor phone_cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null, sort
        );

        if(phone_cursor.getCount() > 0) {
            while (phone_cursor.moveToNext()) {
                try {
                    int id = Integer.parseInt(phone_cursor.getString(
                            phone_cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                    ));

                    Cursor name_cursor = getContentResolver().query(
                            ContactsContract.Data.CONTENT_URI, null,
                            ContactsContract.Data.CONTACT_ID + " = " + id,
                            null, null
                    );

                    String name = phone_cursor.getString(phone_cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                    ));

                    String first_name = "";
                    String last_name = "";

                    while (name_cursor.moveToNext()) {
                        if (name_cursor.getString(name_cursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME)) != null) {
                            first_name = name_cursor.getString(name_cursor.getColumnIndex(
                                    ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME
                            ));
                            last_name = name_cursor.getString(name_cursor.getColumnIndex(
                                    ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME
                            ));
                        }
                    }
                    name_cursor.close();

                    String phoneNumber = phone_cursor.getString(phone_cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    ));

                    ContactModel model = new ContactModel();
                    model.setName(name);
                    model.setPhone(phoneNumber);

                    if (last_name.length() >= size)
                        arrayList.add(model);

                } catch (Exception e) {
                    Toast.makeText(ContactActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            phone_cursor.close();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContactAdapter(this, arrayList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getContacts(0);
        } else {
            Toast.makeText(ContactActivity.this, "Permissions Denied", Toast.LENGTH_SHORT).show();
            checkPermission();
        }
    }
}