package us.narin.boobie;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private Socket mSocket;

    private List<Chat> messages = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    private RecyclerView rv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();
        mSocket.connect();

//        매칭 요청
        mSocket.emit("watingForStranger");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        rv = (RecyclerView) findViewById(R.id.chat_list);

        mAdapter = new ChatAdapter(messages);

        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv.setAdapter(mAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mSocket.on("joinWithStranger", joinWithStranger);
        mSocket.on("receiveMessage", receiveMessage);
        mSocket.on("disconnect", onDisconnect);
    }

    private Emitter.Listener joinWithStranger = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            JSONObject responseData = (JSONObject) args[0];
            Integer joinType = 0;

            Log.d("joinWithStranger", "CHAT_ACTIVITY-" + responseData.toString());

            try {
                joinType = responseData.getInt("type");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (joinType == 1) {
//                매칭되었음.
                Log.d("joinWithStranger", "CHAT_ACTIVITY-" + "매칭되었습니다.");
            } else {
//                매칭되지않음.
//                방을 만들고 기다림.
                Log.d("joinWithStranger", "CHAT_ACTIVITY-" + "일단 매칭되지 않았으니 방을 만들고 기다립니다.");
            }

        }
    };

    private Emitter.Listener receiveMessage = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject responseData = (JSONObject) args[0];
                    String message = "";
                    try {
                        message = responseData.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.d("receiveMessage", "CHAT_ACTIVITY-" + responseData.toString());
                    addMessage(message);
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            Log.d("onDisconnect", "CHAT_ACTIVITY-연결이 끊어졌습니다.");
        }
    };

    private void addMessage(String message) {

        Chat chat = new Chat();
        chat.setMessageContent(message);

        messages.add(chat);
        mAdapter.notifyDataSetChanged();
        scrollToBottom();
    }

    private void scrollToBottom() {
        rv.scrollToPosition(mAdapter.getItemCount() - 1);
    }

}
