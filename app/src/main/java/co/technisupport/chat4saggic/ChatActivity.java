package co.technisupport.chat4saggic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.technisupport.chat4saggic.utils.Global;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView messagesRecycler;
    MessageAdapter messageAdapter;
    RelativeLayout rootView;
    ImageButton emojiButton;
    ImageButton closeSearchButon;
    ImageButton moreButton;
    ArrayList<MessageObject> messages;
    ArrayList<Integer> searchOccurences;
    FloatingActionButton sendButton;
    EmojiEditText editText;
    EditText searchEdit;
    Toolbar toolbar;
    LinearLayout searchBar;
    EmojiPopup emojiPopup;
    TextView occurencesText;
    String highlight="";
    int currentSearchIndex;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
    Uri photoUri;

    public String backgroundColorUser = "#D3EFFF";
    public String backgroundColorSender = "#F0F0F0";

    public String textColorUser = "#000000";
    public String textColorSender = "#000000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        cleanCache();
        messages = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            messages.add(new MessageObject(i%2,"Mensaje de prueba " + i, dateFormat.format(new Date())));
        }
        rootView = findViewById(R.id.root_view);
        messagesRecycler = findViewById(R.id.messages_recycler);
        sendButton = findViewById(R.id.send_button);
        toolbar = findViewById(R.id.toolbar);
        editText = findViewById(R.id.edit_text);
        emojiButton = findViewById(R.id.emoji_button);
        closeSearchButon = findViewById(R.id.close_search_button);
        moreButton = findViewById(R.id.more_button);
        searchBar = findViewById(R.id.search_bar);
        searchEdit = findViewById(R.id.search_edit);
        occurencesText = findViewById(R.id.occurences_text);

        searchOccurences = new ArrayList<>();
        messageAdapter = new MessageAdapter(this,messages);
        messagesRecycler.setLayoutManager(new LinearLayoutManager(this));
        messagesRecycler.setAdapter(messageAdapter);
        messagesRecycler.scrollToPosition(messages.size()-1);

        emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(editText);
        sendButton.setOnClickListener(this);
        emojiButton.setOnClickListener(this);
        moreButton.setOnClickListener(this);
        closeSearchButon.setOnClickListener(this);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchOccurences = new ArrayList<>();
                for (int i = 0; i < messages.size(); i++) {
                    if(messages.get(i).getText().toLowerCase().contains(s.toString().toLowerCase())){
                        searchOccurences.add(i);
                    }
                }
                if(searchOccurences.size()>0){
                    currentSearchIndex = 0;
                    String occurencesString = "1/"+searchOccurences.size();
                    occurencesText.setText(occurencesString);
                    highlight = s.toString().toLowerCase();
                    findViewById(R.id.next_search_button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(currentSearchIndex < searchOccurences.size()-1){
                                currentSearchIndex++;

                            }else{
                                currentSearchIndex=0;
                            }
                            String occurencesString = (currentSearchIndex+1)+"/"+searchOccurences.size();
                            occurencesText.setText(occurencesString);
                            messagesRecycler.scrollToPosition(searchOccurences.get(currentSearchIndex));
                        }
                    });
                    findViewById(R.id.previous_search_button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(currentSearchIndex  != 0){
                                currentSearchIndex--;

                            }else{
                                currentSearchIndex=searchOccurences.size()-1;
                            }
                            String occurencesString = (currentSearchIndex+1)+"/"+searchOccurences.size();
                            occurencesText.setText(occurencesString);
                            messagesRecycler.scrollToPosition(searchOccurences.get(currentSearchIndex));
                        }
                    });
                    messagesRecycler.scrollToPosition(searchOccurences.get(0));
                }else{
                    occurencesText.setText("");
                    highlight = "";
                }
                messageAdapter.notifyDataSetChanged();

            }
        });
        rootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(bottom>oldBottom){
                    emojiButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_emoticon));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_search:
                toggleSearchBar();
                break;
            case R.id.menu_end:
                messages.clear();
                messageAdapter.notifyDataSetChanged();
                break;
            case R.id.menu_disable_sound:
                Toast.makeText(this, "En desarrollo", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_send_transcript:
                Toast.makeText(this, "En desarrollo", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void toggleKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        }
    }

    public void toggleSearchBar(){
        if(searchBar.getVisibility()==View.VISIBLE){
            highlight="";
            messageAdapter.notifyDataSetChanged();
            Animation fadeOutAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
            toolbar.setVisibility(View.VISIBLE);
            searchBar.startAnimation(fadeOutAnimation);
            fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    searchBar.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }else{
            Animation fadeInAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            searchBar.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.GONE);
            searchBar.startAnimation(fadeInAnimation);
            searchEdit.requestFocus();
            toggleKeyboard();
            fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if(searchBar.getVisibility()==View.VISIBLE){
            toggleSearchBar();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send_button:
                if(!editText.getText().toString().isEmpty()){
                    messages.add(new MessageObject(0,editText.getText().toString(),dateFormat.format(new Date())));
                    messageAdapter.notifyDataSetChanged();
                    messagesRecycler.scrollToPosition(messages.size()-1);
                    editText.setText("");
                }
                break;
            case R.id.emoji_button:
                if(emojiPopup.isShowing()){
                    emojiButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_emoticon));
                    emojiPopup.dismiss();
                }else{
                    emojiButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard));
                    emojiPopup.toggle();
                }
                break;
            case R.id.close_search_button:
                onBackPressed();
                break;
            case R.id.more_button:
                selectImage();
                break;
        }
    }

    // ------------------ attachments ------------------

    public void selectImage() {
        final CharSequence[] items = new String[]{"Usar la camara", "Escoger de la galería", "Cancelar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Adjuntar una foto");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Usar la camara")) {
                    if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        ActivityCompat.requestPermissions(ChatActivity.this,new String[]{
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },10);
                        return;
                    }
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = Global.createImageFile(ChatActivity.this);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        if (photoFile != null) {
                            photoUri = FileProvider.getUriForFile(ChatActivity.this, "co.technisupport.chat4saggic.fileprovider", photoFile);
                            List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                            for (ResolveInfo resolveInfo : resInfoList) {
                                String packageName = resolveInfo.activityInfo.packageName;
                                grantUriPermission(packageName, photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            }
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                            startActivityForResult(takePictureIntent, 1);
                        }
                    }
                } else if (items[item].equals("Escoger de la galería")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            1);
                } else if (items[item].equals("Cancelar")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK){
            DraweeController controller;
            if(data!=null){
                if(data.getData()!=null){
                    photoUri = data.getData();
                }
            }
            switch (requestCode){

                case 1:
                    messages.add(new MessageObject(0, "", dateFormat.format(new Date()), photoUri));
                    messageAdapter.notifyDataSetChanged();
                    messagesRecycler.scrollToPosition(messages.size()-1);

                    break;
            }
        }
    }

    public void cleanCache(){
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String aChildren : children) {
                    new File(dir, aChildren).delete();
                }
            }
        }
    }
}


