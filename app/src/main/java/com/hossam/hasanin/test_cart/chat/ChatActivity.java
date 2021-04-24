package com.hossam.hasanin.test_cart.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.hossam.hasanin.test_cart.cart.CartViewModel;
import com.hossam.hasanin.test_cart.chat.datasource.ChatDataSourceImpl;
import com.hossam.hasanin.test_cart.models.Message;
import com.hossam.hasanin.test_cart.models.UserChat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import id.zelory.compressor.Compressor;

public class ChatActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_MULTIPLE = 1;
    private ChatViewModel viewModel;
    private TextView infoMessage;
    private RecyclerView messagesRec;
    private EditText etMessage;
    private Button btnSend , btnUploadImage;
    private FloatingActionButton fbGoDown;
    private ProgressBar barLoading;
    private UserChat otherUser;
    private ChatAdapter adapter;
    private ArrayList<Uri> selectedImagesToSend;
    public int userId;


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

        otherUser =  new UserChat(2 , "name");
        userId = 1;

        adapter = new ChatAdapter(getBaseContext() , new ArrayList<>(), otherUser);
        LinearLayoutManager manager = new LinearLayoutManager(getBaseContext());
        messagesRec.setLayoutManager(manager);
        messagesRec.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        MainApplication application = (MainApplication) getApplication();

        viewModel.setDataSource(new ChatDataSourceImpl(userId));

        viewModel.chatListener(otherUser.getId());

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
                Message message = new Message(userId , etMessage.getText().toString() , Message.TEXT_MESS , null);
                viewModel.send(message, otherUser.getId());
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
                    viewModel.loadMore(otherUser.getId());
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

                    viewModel.uploadImage(image , mImageUri , otherUser.getId() , userId);

                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();

                            byte[] image = compressImage(uri);
                            viewModel.uploadImage(image , uri , otherUser.getId() , userId);
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
}