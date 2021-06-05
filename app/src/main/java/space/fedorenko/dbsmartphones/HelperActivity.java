package space.fedorenko.dbsmartphones;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class HelperActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper);

        Button one = findViewById(R.id.hlpMatrixBtn);
        one.setOnClickListener(this);
        Button two = findViewById(R.id.hlpSmartBtn);
        two.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        LinearLayout layout = null;
        switch (v.getId()) {
            case R.id.hlpMatrixBtn:
                layout = findViewById(R.id.helperMatrix);
                break;
            case R.id.hlpSmartBtn:
                layout = findViewById(R.id.helperSmartphone);
                break;
            default:
                break;
        }

        try {
            assert layout != null;
            if(layout.getVisibility() == View.GONE)
                layout.setVisibility(View.VISIBLE);
            else
                layout.setVisibility(View.GONE);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}