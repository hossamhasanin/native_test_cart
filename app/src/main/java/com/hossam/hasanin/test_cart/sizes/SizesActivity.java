package com.hossam.hasanin.test_cart.sizes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.hossam.hasanin.test_cart.R;

import java.util.ArrayList;

public class SizesActivity extends AppCompatActivity implements SizesListener {

    ArrayList<SizesModel> models;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sizes);

        RecyclerView rec = findViewById(R.id.rec_sizes);

        models = new ArrayList<>();

        models.add(new SizesModel(1 , 1));
        models.add(new SizesModel(2 , 1));
        models.add(new SizesModel(3 , 1));
        models.add(new SizesModel(4 , 1));
        models.add(new SizesModel(5 , 1));

        SizesAdapter adapter = new SizesAdapter(models , this);
        adapter.setOnItemClickListener(this);

        rec.setLayoutManager(new LinearLayoutManager(this));
        rec.setAdapter(adapter);


        findViewById(R.id.btn_cancel).setOnClickListener(view -> {
            Log.v("koko" , "arr " + models.get(0).getQuantity());
        });

    }

    @Override
    public void afterSizeQTYTextChanged(Integer id, Integer qty) {
        Toast.makeText(this , "quantity is "+ qty , Toast.LENGTH_LONG).show();

        if (id != null){
            findItemChangeIt(models , id , qty);
        }


    }

    private void findItemChangeIt(ArrayList<SizesModel> qtyList , Integer id, Integer qty){
        for (SizesModel model : qtyList){
            if (model.getSize_id().equals(id)){
                model.setQuantity(qty);
                Log.v("koko" , "change "+id);
                break;
            }
        }
    }
}