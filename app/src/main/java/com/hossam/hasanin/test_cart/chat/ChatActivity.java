package com.hossam.hasanin.test_cart.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hossam.hasanin.test_cart.MainApplication;
import com.hossam.hasanin.test_cart.R;
import com.hossam.hasanin.test_cart.cart.CartViewModel;
import com.hossam.hasanin.test_cart.chat.datasource.ChatDataSourceImpl;
import com.hossam.hasanin.test_cart.models.UserChat;

public class ChatActivity extends AppCompatActivity {

    private ChatViewModel viewModel;
    private TextView infoMessage;
    private RecyclerView messagesRec;
    private EditText etMessage;
    private Button btnSend;
    private ProgressBar barLoading;
    private UserChat otherUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        infoMessage = findViewById(R.id.tv_mess);
        messagesRec = findViewById(R.id.rec_messages);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
        barLoading = findViewById(R.id.bar_loading);

        otherUser =  new UserChat(2 , "name");

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
                ChatAdapter adapter = new ChatAdapter(getBaseContext() , viewState.getMessages() , otherUser);
                LinearLayoutManager manager = new LinearLayoutManager(getBaseContext());
//                manager.setStackFromEnd(true);
//                manager.setReverseLayout(true);
                messagesRec.setLayoutManager(manager);
                messagesRec.setAdapter(adapter);
                messagesRec.scrollToPosition(viewState.getMessages().size() - 1);
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

    }
}