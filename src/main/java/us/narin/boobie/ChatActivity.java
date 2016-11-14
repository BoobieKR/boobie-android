package us.narin.boobie;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
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
    private EditText chatEditText;

    private ProgressDialog mProgressDialog;


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

        rv = (RecyclerView) findViewById(R.id.chat_list);
        chatEditText = (EditText) findViewById(R.id.chat_edittext);
        Button chatSubmitBtn = (Button) findViewById(R.id.chat_submit_btn);
        mProgressDialog = new ProgressDialog(ChatActivity.this);

        chatSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject obj = new JSONObject();
                String chatContent = chatEditText.getText().toString();

                try {
                    obj.put("message", chatContent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mSocket.emit("sendMessage", obj);
            }
        });

        mAdapter = new ChatAdapter(messages);

        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv.setAdapter(mAdapter);

        mSocket.on("joinWithStranger", joinWithStranger);
        mSocket.on("receiveMessage", receiveMessage);
        mSocket.on("disconnect", onDisconnect);

    }

    private Emitter.Listener joinWithStranger = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
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
                        mProgressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "낯선 상대와 매칭되었습니다.", Toast.LENGTH_LONG).show();
                        Log.d("joinWithStranger", "CHAT_ACTIVITY-" + "매칭되었습니다.");
                    } else {
//                매칭되지않음.
//                방을 만들고 기다림.
                        mProgressDialog.setMessage("일단 매칭되지 않았으니 방을 만들고 기다리는 중입니다.");
                        mProgressDialog.show();
                        Log.d("joinWithStranger", "CHAT_ACTIVITY-" + "일단 매칭되지 않았으니 방을 만들고 기다립니다.");
                    }
                }
            });

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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("onDisconnect", "CHAT_ACTIVITY-연결이 끊어졌습니다.");
                    Toast.makeText(getApplicationContext(), "낯선 상대와 연결이 끊어졌습니다. 다시 기다립니다.", Toast.LENGTH_LONG).show();
                    mSocket.emit("connection");
                    mSocket.emit("watingForStranger");
                }
            });
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
