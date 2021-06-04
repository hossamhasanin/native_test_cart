package com.hossam.hasanin.test_cart.sizes;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.hossam.hasanin.test_cart.R;

import java.util.List;

public class SizesAdapter extends RecyclerView.Adapter<SizesAdapter.MainCategoryViewHolder> {

    private final List<SizesModel> list;
    private SizesListener listener;
//    private SizesModel model;
    private int index = -1;
    private final Context mCtx;

    public SizesAdapter(List<SizesModel> list, Context mCtx) {
        this.list = list;
        this.mCtx = mCtx;
    }

    @NonNull
    @Override
    public MainCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_sizes, parent, false);
        return new MainCategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MainCategoryViewHolder holder, int position) {
        SizesModel model = list.get(position);
        if (model != null) {
            holder.tvSize.setText(model.getSize());
        }
        //holder.etSizeQTY.setText(model.getQuantity());

        long delay = 500; // 1 seconds after user stops typing
        final long[] last_text_edit = {0};
        Handler handler = new Handler();

        Runnable input_finish_checker = new Runnable() {
            public void run() {
                if (System.currentTimeMillis() > (last_text_edit[0] + delay - 500)) {
                    // TODO: do what you need here
                    // ............
                    // ............
                    //DoStuff();
                    //model.setSize_id(list.get(position).getSize_id());
                    Log.v("koko" , "text "+ holder.etSizeQTY.getText().toString());
                    String txt = holder.etSizeQTY.getText().toString();

                    if (txt.length() == 0 || txt.equals("0")){
                        model.setQuantity(0);
                        holder.tvSize.setChecked(false);
                    } else {
                        model.setQuantity(Integer.parseInt(holder.etSizeQTY.getText().toString()));
                        holder.tvSize.setChecked(true);
                    }

                    if (listener != null) {
                        listener.afterSizeQTYTextChanged(model.getSize_id(), model.getQuantity());
                    }
                }
            }
        };

        holder.tvSize.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean value) {
                if (!value && model.getQuantity() != 0){
                    holder.etSizeQTY.setText("");
                    //Toast.makeText(compoundButton.getContext() , "check " , Toast.LENGTH_LONG).show();
                } else if (!value){
                    listener.afterSizeQTYTextChanged(model.getSize_id(), 0);
                }
            }
        });


        holder.etSizeQTY.addTextChangedListener(new TextWatcher() {
                                                    @Override
                                                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                                                  int after) {
                                                    }

                                                    @Override
                                                    public void onTextChanged(final CharSequence s, int start, int before,
                                                                              int count) {
                                                        //You need to remove this to run only once
                                                        handler.removeCallbacks(input_finish_checker);

                                                    }

                                                    @Override
                                                    public void afterTextChanged(final Editable s) {
                                                        //avoid triggering event when text is empty
                                                        last_text_edit[0] = System.currentTimeMillis();
                                                        handler.postDelayed(input_finish_checker, delay);
                                                    }
                                                }

        );
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        } else {
            return list.size();
        }
    }


    class MainCategoryViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox tvSize;
        private int position;
        private final EditText etSizeQTY;
        private final ConstraintLayout constraintLayout;

        private MainCategoryViewHolder(final View parent) {
            super(parent);
            this.constraintLayout = parent.findViewById(R.id.ConstraintLayoutSizes);
            this.etSizeQTY = parent.findViewById(R.id.etSizeQTY);
            this.tvSize = parent.findViewById(R.id.tvSize);
            /*tvSize.setOnClickListener( view -> {
                position = getAdapterPosition();
                index = position;
                notifyDataSetChanged();
                if (listener != null) {
                    model = list.get( position );
                    listener.onClickCardViewItem( position, model );
                }
            } );*/
        }

    }

    public void setOnItemClickListener(SizesListener listener) {
        this.listener = listener;
    }
}
