package us.narin.boobie;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.w3c.dom.Text;

import java.util.List;

/**
 * @author jade
 * @date 12/11/2016
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<Chat> mMessages;

    ChatAdapter(List<Chat> mMessages){
        this.mMessages = mMessages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.chat_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Chat message = mMessages.get(position);
        viewHolder.setMessage(message.getMessageContent());
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView messageContent;

        public ViewHolder(View itemView) {
            super(itemView);
            messageContent = (TextView) itemView.findViewById(R.id.message_content);
        }

        public void setMessage(String message) {
            if (null == messageContent) return;
            messageContent.setText(message);
        }

    }
}
