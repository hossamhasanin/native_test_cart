package com.hossam.hasanin.test_cart.chat.messages;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hossam.hasanin.test_cart.MainApplication;
import com.hossam.hasanin.test_cart.R;
import com.hossam.hasanin.test_cart.chat.datasource.ChatDataSourceImpl;
import com.hossam.hasanin.test_cart.models.Message;
import com.hossam.hasanin.test_cart.models.UserChat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity implements DeleteMessageListener{

    private static final int PICK_IMAGE_MULTIPLE = 1;
    private ChatViewModel viewModel;
    private TextView infoMessage;
    private RecyclerView messagesRec;
    private EditText etMessage;
    private Button btnSend , btnUploadImage;
    private FloatingActionButton fbGoDown;
    private ProgressBar barLoading;
    private UserChat sendTo;
    private ChatAdapter adapter;
    private ArrayList<Uri> selectedImagesToSend;
    public UserChat sendFrom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        infoMessage = findViewById(R.id.tv_mess);
        messagesRec = findViewById(R.id.rec_messages);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
        btnUploadImage = findViewById(R.id.btn_upload_image);
        fbGoDown = findViewById(R.id.fbtn_go_down);
        barLoading = findViewById(R.id.bar_loading);

        sendTo =  new UserChat(2 , "user 2", "");
        sendFrom = new UserChat( 1 , "user 1", "");

        adapter = new ChatAdapter(getBaseContext() , new ArrayList<>(), sendTo, this);
        LinearLayoutManager manager = new LinearLayoutManager(getBaseContext());
        messagesRec.setLayoutManager(manager);
        messagesRec.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        MainApplication application = (MainApplication) getApplication();

        viewModel.setDataSource(new ChatDataSourceImpl(sendFrom));

        viewModel.findChatId(sendTo);

        viewModel.chatId.observe(this , chatId -> {
            Log.v("koko" , "activity chat id is "+ chatId);
            if (chatId != null) {
                if (!chatId.isEmpty()) {
                    viewModel.chatListener(sendTo.getId());
                }
            }
        });

        viewModel.viewstate.observe(this, viewState -> {
            if (viewState.getLoading()){
                barLoading.setVisibility(View.VISIBLE);
            } else {
                barLoading.setVisibility(View.GONE);
            }

            if (!viewState.getError().isEmpty()){
                infoMessage.setVisibility(View.VISIBLE);
                infoMessage.setText(viewState.getError());
            } else  {
                infoMessage.setVisibility(View.GONE);
            }

            if (!viewState.getMessages().isEmpty()){
                messagesRec.setVisibility(View.VISIBLE);

                int pos = ((LinearLayoutManager) messagesRec.getLayoutManager())
                        .findFirstCompletelyVisibleItemPosition();

                adapter.setMessages(viewState.getMessages());

                if (viewModel.savedScrollPos.getValue() != 0) {
                    manager.scrollToPositionWithOffset(viewModel.savedScrollPos.getValue(), 80);
                    Log.v("koko", "scroll pos " + pos);
                    Log.v("koko", "saved scroll pos " + viewModel.savedScrollPos.getValue());
                    viewModel.savedScrollPos.postValue(0);
                }
            } else {
                messagesRec.setVisibility(View.GONE);
            }

        });

        btnSend.setOnClickListener(view -> {
            if (application.isConnected.getValue()) {
                Message message = new Message("" , sendFrom.getId(), etMessage.getText().toString() , Message.TEXT_MESS , null);
                viewModel.send(message , sendTo);
                etMessage.setText("");
            } else {
                Toast.makeText(getBaseContext() , "You cannot send anything without internet" , Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.firstNewMessagePos.observe(this , pos -> {
            if (pos != 0){
                Toast.makeText(this , "new message "+ pos , Toast.LENGTH_LONG).show();
                fbGoDown.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this , "hide "+ pos , Toast.LENGTH_LONG).show();

                fbGoDown.setVisibility(View.GONE);
            }
        });


        messagesRec.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int pos = ((LinearLayoutManager) recyclerView.getLayoutManager())
                        .findFirstCompletelyVisibleItemPosition();

                int lastVisAblePos = ((LinearLayoutManager) recyclerView.getLayoutManager())
                        .findLastCompletelyVisibleItemPosition();

                int itemCount = recyclerView.getLayoutManager().getChildCount();
//                Log.v("koko" , "scroll pos "+ pos);
//                Log.v("koko" , "item count "+ itemCount);
                Log.v("koko" , "dy "+ dy);
                if (pos == 0 && pos > dy) {
                    viewModel.loadMore();
                    Log.v("koko" , "load more");
                }

                Log.v("koko" , "scroll lastVisAblePos "+ lastVisAblePos);
                if (pos > 0 &&
                    viewModel.firstNewMessagePos.getValue() <= lastVisAblePos &&
                    viewModel.firstNewMessagePos.getValue() != 0 && dy > 0){
                    Toast.makeText(getBaseContext() , "got to the new one" , Toast.LENGTH_LONG).show();
                    viewModel.firstNewMessagePos.postValue(0);
                }

            }
        });

        fbGoDown.setOnClickListener(view -> {
            messagesRec.smoothScrollToPosition(viewModel.firstNewMessagePos.getValue());
        });

        btnUploadImage.setOnClickListener(view -> {
            selectImages();
        });

    }

    private void selectImages(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_MULTIPLE);
    }

    private void showWantToRemoveDialog(Context context , String id , int pos){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                .setMessage("Are you sure you want to remove this item ?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    viewModel.deleteMessage(id , pos);
                    dialogInterface.dismiss();
                }).setNegativeButton("No" , (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                if(data.getData()!=null){

                    Uri mImageUri=data.getData();


                    byte[] image = compressImage(mImageUri);

                    viewModel.uploadImage(image , mImageUri , sendTo , sendFrom);

                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();

                            byte[] image = compressImage(uri);
                            viewModel.uploadImage(image , uri , sendTo , sendFrom);
                        }
                        Log.v("koko", "Selected Images" + mClipData.getItemCount());
                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    private byte[] compressImage(Uri uri) throws IOException {

        InputStream inputStream = getContentResolver().openInputStream(uri);
        Bitmap t = BitmapFactory.decodeStream(inputStream);

//        Bitmap compressedImage = new Compressor(this)
//                .setMaxWidth(200)
//                .setMaxHeight(200)
//                .setQuality(75).compressToBitmap(new File(uri.getPath()));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        t.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    @Override
    public void deleteMessage(String id , int pos) {
        showWantToRemoveDialog(this , id , pos);
    }
}