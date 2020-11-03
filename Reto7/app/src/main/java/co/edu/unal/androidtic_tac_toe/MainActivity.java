package co.edu.unal.androidtic_tac_toe;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;


import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TicTacToeGame mGame;
    // Buttons making up the board
    private static final String TAG = "MyActivity";
    private boolean computerIsMoving;
    private boolean mGameOver, firstHuman;
    private Long server_state, p_turn;
    private Integer human_wins, android_wins, ties, player;


    final FirebaseFirestore db = FirebaseFirestore.getInstance();





    private BoardView mBoardView;

    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;

    // Listen for touches on the board



    boolean mSoundOn;
    SharedPreferences mPrefs;



    private Button mBoardButtons[];


    Handler handler = new Handler();
    // Various text displayed
    private TextView mInfoTextView, mInfoTextAndroidWins, mInfoTextHumanWins, mInfoTextTiesWins;

    final DocumentReference docRef = db.collection("GLOBAL").document("board");
    public void CheckForWinner() {

        int winner = mGame.checkForWinner();
        mGameOver = true;

        System.out.println("CHECKING WINNER" + winner);
        if (winner == 0)
            mGameOver = false;
        if (winner == 1) {
            mInfoTextView.setText(R.string.result_tie);
            ties ++;
        } else if (winner == 2) {
            mInfoTextView.setText("Player 1 win");
            human_wins++;
        } else  if (winner != 0){
            mInfoTextView.setText("Player 2 win");
            android_wins++;
        }


        if (winner != 0) {
            mGameOver = true;
            mInfoTextAndroidWins.setText(android_wins.toString());
            mInfoTextHumanWins.setText(human_wins.toString());
            mInfoTextTiesWins.setText(ties.toString());

        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
       docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    String fb_board = (String) snapshot.get("val");

                    for (int i=0; i<9; i++){
                        mGame.mBoard[i] = (fb_board.charAt(i) == '_' ? ' ': fb_board.charAt(i));
                    }
                    mBoardView.invalidate();
                    Log.d(TAG, "Current data: " + snapshot.getData());

                    CheckForWinner();

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });




/*

        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("first", "Ada");
        user.put("last", "Lovelace");
        user.put("born", 1815);
        user.put("ID", "TISI");

// Add a new document with a generated ID
        db.collection("users")
                .document("TISI").set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
*/



        // Restore the scores from the persistent preference data source
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSoundOn = mPrefs.getBoolean("sound", true);



        mGameOver = false;
        firstHuman = false;
        human_wins = 0;
        computerIsMoving = false;
        android_wins = 0;
        ties = 0;
        server_state = new Long(0);
        player = -1;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGame = new TicTacToeGame();
        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);
        // Listen for touches on the board
        mBoardView.setOnTouchListener(mTouchListener);

        String difficultyLevel = mPrefs.getString("difficulty_level",
                getResources().getString(R.string.difficulty_harder));
        if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
        else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
        else
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);

        mInfoTextView = (TextView) findViewById(R.id.information);
        mInfoTextAndroidWins = (TextView) findViewById(R.id.android_score);
        mInfoTextHumanWins = (TextView) findViewById(R.id.human_score);
        mInfoTextTiesWins = (TextView) findViewById(R.id.tie_score);

        startNewGame();






//        mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE];
//        mBoardButtons[0] = (Button) findViewById(R.id.one);
//        mBoardButtons[1] = (Button) findViewById(R.id.two);
//        mBoardButtons[2] = (Button) findViewById(R.id.three);
//        mBoardButtons[3] = (Button) findViewById(R.id.four);
//        mBoardButtons[4] = (Button) findViewById(R.id.five);
//        mBoardButtons[5] = (Button) findViewById(R.id.six);
//        mBoardButtons[6] = (Button) findViewById(R.id.seven);
//        mBoardButtons[7] = (Button) findViewById(R.id.eight);
//        mBoardButtons[8] = (Button) findViewById(R.id.nine);


    }


    private void resetServer() {



        Map<String, Object> state_db_obj = new HashMap<>();

        state_db_obj.put("val", new Long(0));

        db.collection("GLOBAL")
                .document("server_state").set(state_db_obj)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

        state_db_obj.put("val", new Long(1));

        db.collection("GLOBAL")
                .document("turn").set(state_db_obj)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });




        Map<String, Object> board_db_obj = new HashMap<>();

        board_db_obj.put("val", "_________");
        db.collection("GLOBAL")
                .document("board").set(board_db_obj)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

        mGame.clearBoard();
        mBoardView.invalidate();
        server_state = new Long(0);
        mInfoTextView.setText("Start new game");
        player = -1;

        firstHuman = true;

        android_wins = 0;
        ties = 0;
        human_wins = 0;
        mInfoTextAndroidWins.setText(android_wins.toString());
        mInfoTextHumanWins.setText(human_wins.toString());
        mInfoTextTiesWins.setText(ties.toString());



    }
    boolean gotten = false;
    // Set up the game board.
    private void startNewGame() {
        Source source = Source.SERVER;
        db.collection("GLOBAL")
                .document("test")
                .get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                           if (task.isSuccessful()) {
                                                               // Document found in the offline cache
                                                               DocumentSnapshot document = task.getResult();
                                                               System.out.println("TEST VARIABLE" + server_state);
                                                               Log.d(TAG, " document data: " + document.getData());
                                                           } else {
                                                               Log.d(TAG, " get failed: ", task.getException());
                                                           }
                                                       }          });

        if (player == -1 ) {



            gotten = false;
            db.collection("GLOBAL")
                    .document("server_state")
                    .get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        // Document found in the offline cache
                        DocumentSnapshot document = task.getResult();
                        server_state = new Long((Long) document.get("val"));
                        System.out.println("Server state" + server_state);
                        Log.d(TAG, "get document data: " + document.getData());
                        gotten = true;

                        Map<String, Object> state_db_obj = new HashMap<>();


                        System.out.println(" ------------  " + server_state);
                        if (server_state == 0) {
                            mGameOver = false;
                            player = 1;
                            state_db_obj.put("val", new Long(1));
                            mInfoTextView.setText("Connected as player 1");
                        } else if (server_state == 1) {
                            mGameOver = false;
                            player = 2;
                            state_db_obj.put("val", new Long(2));
                            mInfoTextView.setText("Connected as player 2");
                        } else {
                            System.out.println(" else ------------  " + server_state);
                            mGameOver = true;
                            state_db_obj.put("val", new Long(2));
                            mInfoTextView.setText("Server full");
                        }

                        db.collection("GLOBAL")
                                .document("server_state").set(state_db_obj)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });


                    } else {
                        Log.d(TAG, " get failed: ", task.getException());
                    }
                }
            });



        } else {
            firstHuman = !firstHuman;
            Map<String, Object> state_db_obj = new HashMap<>();

            System.out.println(firstHuman);
            if (firstHuman)
                state_db_obj.put("val", new Long(1));
            else
                state_db_obj.put("val", new Long(2));

            db.collection("GLOBAL")
                    .document("turn").set(state_db_obj)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                        }
                    });

            mGameOver = false;
            mInfoTextView.setText("Connected as player " + player);
        }


        Map<String, Object> board_db_obj = new HashMap<>();

        board_db_obj.put("val", "_________");
        db.collection("GLOBAL")
                .document("board").set(board_db_obj)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

        mGame.clearBoard();
        mBoardView.invalidate(); // Redraw the board
        // Reset all buttons



        // Human goes first

    }    // End of startNewGame



    // Handles clicks on the game board buttons



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }



    static final int DIALOG_QUIT_ID = 1;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                startNewGame();
                return true;
            case R.id.settings:
                startActivityForResult(new Intent(this, Settings.class), 0);
                return true;

            case R.id.quit:
                showDialog(DIALOG_QUIT_ID);
                return true;
            case R.id.restart:
                resetServer();
                return true;
        }
        return false;
    }



        // Listen for touches on the board
        // Listen for touches on the board
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        private boolean setMove(char player, int location) {
            if (mGame.setMove(player, location)) {
                mBoardView.invalidate();
                // Redraw the board
                return true;
            }
            return false;

        }










            public boolean onTouch(View v, MotionEvent event) {

                // Determine which cell was touched
                int col = (int) event.getX() / mBoardView.getBoardCellWidth();
                int row = (int) event.getY() / mBoardView.getBoardCellHeight();
                final int pos = row * 3 + col;


                db.collection("GLOBAL")
                        .document("turn")
                        .get(Source.SERVER).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // Document found in the offline cache
                            DocumentSnapshot document = task.getResult();
                            System.out.println("TEST VARIABLE" + server_state);
                            Long turn = new Long((Long)document.get("val"));

                            char c = (player == 1  ? TicTacToeGame.HUMAN_PLAYER : TicTacToeGame.COMPUTER_PLAYER);

                            if (turn.longValue() == player.longValue() && !mGameOver && setMove(c, pos)) {
                                if(mSoundOn)
                                    mHumanMediaPlayer.start();
                                Map<String, Object> board_db_obj = new HashMap<>();
                                String board_st = "";

                                for (int i=0; i<9; i++) {
                                    board_st +=  ( mGame.mBoard[i] == ' ' ? '_' :  mGame.mBoard[i]);
                                }
                                board_db_obj.put("val", board_st);
                                db.collection("GLOBAL")
                                        .document("board").set(board_db_obj)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error writing document", e);
                                            }
                                        });



                                Map<String, Object> new_turn = new HashMap<>();

                                new_turn.put("val", new Long((player == 2 ? 1 : 2)));

                                db.collection("GLOBAL")
                                        .document("turn").set(new_turn)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error writing document", e);
                                            }
                                        });
                            }
                            Log.d(TAG, " document data: " + document.getData());
                        } else {
                            Log.d(TAG, " get failed: ", task.getException());
                        }
                    }          });


// So we aren't notified of continued events when finger is moved
                    return false;
                }

    };





        @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch(id) {
            case DIALOG_QUIT_ID:
                // Create the quit confirmation dialog

                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MainActivity.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();

                break;



        }

        return dialog;
    }


    @Override
    protected void onResume() {
        super.onResume();

        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.human);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.computer);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == RESULT_CANCELED) {
            // Apply potentially new settings

            mSoundOn = mPrefs.getBoolean("sound", true);

            String difficultyLevel = mPrefs.getString("difficulty_level",
                    getResources().getString(R.string.difficulty_easy));

            if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
            else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
            else
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);

            mBoardView.setColor(mPrefs.getInt("board_color", 0xFFCCCCCC));
        }
    }
}

