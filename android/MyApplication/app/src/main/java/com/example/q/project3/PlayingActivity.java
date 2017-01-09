package com.example.q.project3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by q on 2017-01-07.
 */

public class PlayingActivity extends Activity {
    /* Firebase setup */
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference().child("game");
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private StorageReference storageReference = firebaseStorage.getReferenceFromUrl("gs://sherlockhums-25f4c.appspot.com");
    int RECORDER = 1;
    int currentRecorder = 1;
    int currentRound = 1;
    JSONArray tempSongs;
    String currTitle;
    String currArtist;
    int round = 0;

    TextView player1_message;
    TextView player2_message;
    TextView player3_message;
    TextView player4_message;

    EditText answer_box;
    Button submit_btn;
    /* Temp variables */
    boolean is_recorder = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_playing);

        GameData gameData = new GameData();
        Map<String, Object> gameValues = gameData.toMap();
        final Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("curr_recorder", currentRecorder);
        childUpdates.put("curr_round", currentRound);
        childUpdates.put("is_recording", true);

        answer_box = (EditText) findViewById(R.id.answer_box);
        submit_btn = (Button) findViewById(R.id.submit_btn);
        answer_box.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    databaseReference.child("player1_message").setValue(answer_box.getText().toString());
                    answer_box.setText("");
                    return true;
                }
                return false;
            }
        });
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("player1_message").setValue(answer_box.getText().toString());
                answer_box.setText("");
            }
        });

        player1_message = (TextView) findViewById(R.id.player1_message);
        player2_message = (TextView) findViewById(R.id.player2_message);
        player3_message = (TextView) findViewById(R.id.player3_message);
        player4_message = (TextView) findViewById(R.id.player4_message);

        databaseReference.updateChildren(childUpdates);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // GameData gameData = dataSnapshot.getValue(GameData.class);
                String value = dataSnapshot.getValue(String.class);
                switch (dataSnapshot.getKey()) {
                    case "player1_message":
                        player1_message.setText(value);
                        break;
                    case "player2_message":
                        player2_message.setText(value);
                        break;
                    case "player3_message":
                        player3_message.setText(value);
                        break;
                    case "player4_message":
                        player4_message.setText(value);
                        break;
                    case "is_recording":
                        start_guessing();
                        break;
                    default:
                        break;
                }

                Log.d("GAME DATA", "VALUE IS: " + value);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.d("FAIL IN DB", "Failed to read value. ", databaseError.toException());
            }
        });


        try {
            tempSongs = new JSONArray(
                    "[ {\"title\": \"Single lady\", \"artist\": \"Beyonce\"}, "
                    + "{\"title\": \"Tell me\", \"artist\": \"원더걸스\"}, "
                    + "{\"title\": \"보고싶다\", \"artist\": \"김범수\"}, "
                    + "{\"title\": \"나 항상 그대를\", \"artist\": \"이선희\"}, "
                    + "{\"title\": \"Hug\", \"artist\": \"동방신기\"}, "
                    + "{\"title\": \"Gee\", \"artist\": \"소녀시대\"}, "
                    + "{\"title\": \"무조건\", \"artist\": \"박상철\"}, "
                    + "{\"title\": \"Isn't she lovely\", \"artist\": \"Stevie Wonder\"} ]"
            ) ;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        startRound();
    }

    private void startRound() {
        int id = getResources().getIdentifier("player" + currentRecorder, "id", getPackageName());
        LinearLayout recorder = (LinearLayout) findViewById(id);
        recorder.setBackgroundColor(getResources().getColor(R.color.colorRecorder));
        try {
            Log.d("JSON Obj 0", tempSongs.getJSONObject(round).toString());
            currTitle = tempSongs.getJSONObject(round).getString("title");
            currArtist = tempSongs.getJSONObject(round).getString("artist");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (is_recorder) {
            Intent i = new Intent(PlayingActivity.this, RecordingActivity.class);
            i.putExtra("title", currTitle);
            i.putExtra("artist", currArtist);
            startActivityForResult(i, RECORDER);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        databaseReference.child("is_recording").setValue(false);
//        databaseReference.child("mid");
    }

    void start_guessing() {

    }
}
