package com.hossam.hasanin.test_cart.chat.chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hossam.hasanin.test_cart.R;
import com.hossam.hasanin.test_cart.chat.datasource.ChatDataSourceImpl;
import com.hossam.hasanin.test_cart.chat.messages.ChatAdapter;
import com.hossam.hasanin.test_cart.chat.messages.ChatViewModel;
import com.hossam.hasanin.test_cart.models.UserChat;

import java.util.ArrayList;

public class ChatUsersActivity extends AppCompatActivity {

    private ChatUsersViewModel viewModel;
    private UserChat currentUser;

    private ProgressBar barLoading;
    private TextView tvMessage;
    private RecyclerView recChats;
    private ChatUsersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_users);

        barLoading = findViewById(R.id.bar_loading);
        tvMessage = findViewById(R.id.tv_mess);
        recChats = findViewById(R.id.rec_chats);

        adapter = new ChatUsersAdapter(new ArrayList<>());
        LinearLayoutManager manager = new LinearLayoutManager(getBaseContext());
        recChats.setLayoutManager(manager);
        recChats.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ChatUsersViewModel.class);

        currentUser = new UserChat(2 , "name" , "");

        viewModel.setDataSource(new ChatDataSourceImpl(currentUser));

        viewModel.getChats("" , currentUser);

        viewModel.viewstate.observe(this , viewState -> {

            if (viewState.getLoading()){
                barLoading.setVisibility(View.VISIBLE);
            } else {
                barLoading.setVisibility(View.GONE);
            }

            if (!viewState.getError().isEmpty()){
                tvMessage.setVisibility(View.VISIBLE);
                tvMessage.setText(viewState.getError());
            } else  {
                tvMessage.setVisibility(View.GONE);
            }

            if (!viewState.getChats().isEmpty()){
                recChats.setVisibility(View.VISIBLE);

                int pos = ((LinearLayoutManager) recChats.getLayoutManager())
                        .findFirstCompletelyVisibleItemPosition();

                adapter.setUsers(viewState.getChats());

                if (viewModel.savedScrollPos.getValue() != 0) {
                    manager.scrollToPosition(viewModel.savedScrollPos.getValue());
                    Log.v("koko", "scroll pos " + pos);
                    Log.v("koko", "saved scroll pos " + viewModel.savedScrollPos.getValue());
                    viewModel.savedScrollPos.postValue(0);
                }
            } else {
                recChats.setVisibility(View.GONE);
            }

        });

        recChats.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    int visibleItems = recyclerView.getLayoutManager().getChildCount();
                    int totalCount = recyclerView.getLayoutManager().getItemCount();
                    int pastVisibleItems =
                            ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                    if (visibleItems + pastVisibleItems == totalCount ) {
                        viewModel.getChats(viewModel.viewstate.getValue().getChats().get(viewModel.viewstate.getValue().getChats().size() - 1).getId(), currentUser);
                    }
                }

            }
        });

    }
}