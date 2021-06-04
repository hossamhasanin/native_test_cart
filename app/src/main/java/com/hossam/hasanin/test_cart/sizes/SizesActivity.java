package com.hossam.hasanin.test_cart.sizes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.hossam.hasanin.test_cart.R;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class SizesActivity extends AppCompatActivity implements SizesListener {

    ArrayList<SizesModel> models;

    List<SizesModel> qtyList = new ArrayList<>();


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
            Toast.makeText(this , "qtyList size "+qtyList.size() , Toast.LENGTH_LONG).show();
        });

    }

    @Override
    public void afterSizeQTYTextChanged(Integer id, Integer qty) {
        Toast.makeText(this , "quantity is "+ qty , Toast.LENGTH_LONG).show();

        if (!contains(qtyList, id, qty)) {
            if (qty != 0){
                SizesModel qtyModelClass = new SizesModel(id, qty);
                qtyList.add(qtyModelClass);
            }
        } else {
            findItemChangeIt(qtyList, id, qty);
        }

    }

    private boolean contains(List<SizesModel> list, int productId, Integer qty) {
        int i;
        for (i = 0; i < list.size(); i++) {
            if (list.get(i).getSize_id() == (productId) && !list.get(i).getQuantity().equals(qty)) {
                return true;
            }
        }
        return false;
    }

    private void findItemChangeIt(List<SizesModel> qtyList , Integer id, Integer qty){
//        for (SizesModel model : qtyList){
//            if (model.getSize_id().equals(id)){
//                model.setQuantity(qty);
//                Log.v("koko" , "change "+id);
//                break;
//            }
//        }
        ListIterator<SizesModel> iter = qtyList.listIterator();
        while(iter.hasNext()){
            SizesModel sizesModel = iter.next();
            if(sizesModel.getSize_id().equals(id)){
                if (qty == 0){
                    iter.remove();
                } else {
                    sizesModel.setQuantity(qty);
                    Log.v("koko" , "change "+id);
                }
                break;
            }
        }
    }
}