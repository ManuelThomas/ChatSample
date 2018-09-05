package co.technisupport.chat4saggic;

/**
 * Created by manuelthomas on 2/6/18.
 */

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;


public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ChatActivity context;
    private ArrayList<MessageObject> messages;

    public MessageAdapter(ChatActivity context, ArrayList<MessageObject> messages){
        this.context=context;
        this.messages=messages;
    }

    private class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        AppCompatTextView messageTextView;
        TextView dateTextView;
        LinearLayoutCompat bubbleContainer;
        LinearLayout messageContainer;
        SimpleDraweeView mediaView;

        private MessageViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.message_textview);
            dateTextView = itemView.findViewById(R.id.date_textview);
            messageContainer = itemView.findViewById(R.id.message_container);
            bubbleContainer = itemView.findViewById(R.id.bubble_container);
            mediaView = itemView.findViewById(R.id.media_view);

            RoundingParams roundingParams = RoundingParams.fromCornersRadius(context.getResources().getDimensionPixelSize(R.dimen.round_corner_media));
            mediaView.getHierarchy().setRoundingParams(roundingParams);
        }

        @Override
        public void onClick(View view) {

        }



    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_message, viewGroup, false);
        return new MessageViewHolder(v);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

        MessageObject message = messages.get(i);

        if(context.searchOccurences.contains(i)){
            Spannable spannableString = new SpannableString(message.getText());
            int startIndex = message.getText().toLowerCase().indexOf(context.highlight);
            int stopIndex = startIndex + context.highlight.length();
            spannableString.setSpan(new BackgroundColorSpan(Color.YELLOW), startIndex, stopIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ((MessageViewHolder)viewHolder).messageTextView.setText(spannableString);
        }else{
            ((MessageViewHolder)viewHolder).messageTextView.setText(message.getText());
        }

        ((MessageViewHolder) viewHolder).dateTextView.setText(message.getDateTime());
        ((MessageViewHolder) viewHolder).messageContainer.setGravity(message.getUserId()==0? Gravity.END:Gravity.START);
        ((MessageViewHolder) viewHolder).bubbleContainer.getBackground().setColorFilter(
                message.getUserId()==0?
                        Color.parseColor(context.backgroundColorUser):
                        Color.parseColor(context.backgroundColorSender),
                PorterDuff.Mode.MULTIPLY);

        if(message.getText().isEmpty()){
            ((MessageViewHolder) viewHolder).messageTextView.setVisibility(View.GONE);
        }else{
            ((MessageViewHolder) viewHolder).messageTextView.setVisibility(View.VISIBLE);
            ((MessageViewHolder) viewHolder).messageTextView.setTextColor(message.getUserId()==0?
                    Color.parseColor(context.textColorUser):
                    Color.parseColor(context.textColorSender));
        }

        if(message.getMediaUri()!=null){
            ((MessageViewHolder) viewHolder).mediaView.setImageURI(message.getMediaUri());
            ((MessageViewHolder) viewHolder).mediaView.getLayoutParams().height = (int) (Resources.getSystem().getDisplayMetrics().widthPixels*0.65);
            ((MessageViewHolder) viewHolder).mediaView.getLayoutParams().width = (int) (Resources.getSystem().getDisplayMetrics().widthPixels*0.65);


        }else{
            ((MessageViewHolder) viewHolder).mediaView.setImageURI((Uri) null);
            ((MessageViewHolder) viewHolder).mediaView.getLayoutParams().height = 0;
            ((MessageViewHolder) viewHolder).mediaView.getLayoutParams().width = 0;
        }


    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

}