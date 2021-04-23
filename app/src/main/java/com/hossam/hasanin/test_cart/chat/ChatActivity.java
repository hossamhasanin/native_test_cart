package com.hossam.hasanin.test_cart.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.hossam.hasanin.test_cart.cart.CartViewModel;
import com.hossam.hasanin.test_cart.chat.datasource.ChatDataSourceImpl;
import com.hossam.hasanin.test_cart.models.UserChat;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private ChatViewModel viewModel;
    private TextView infoMessage;
    private RecyclerView messagesRec;
    private EditText etMessage;
    private Button btnSend;
    private FloatingActionButton fbGoDown;
    private ProgressBar barLoading;
    private UserChat otherUser;
    private ChatAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        infoMessage = findViewById(R.id.tv_mess);
        messagesRec = findViewById(R.id.rec_messages);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
        fbGoDown = findViewById(R.id.fbtn_go_down);
        barLoading = findViewById(R.id.bar_loading);

        otherUser =  new UserChat(2 , "name");

        adapter = new ChatAdapter(getBaseContext() , new ArrayList<>(), otherUser);
        LinearLayoutManager manager = new LinearLayoutManager(getBaseContext());
        messagesRec.setLayoutManager(manager);
        messagesRec.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        MainApplication application = (MainApplication) getApplication();

        viewModel.setDataSource(new ChatDataSourceImpl(1));

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
                viewModel.send(etMessage.getText().toString(), otherUser.getId());
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

    }
}