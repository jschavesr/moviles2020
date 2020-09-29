package co.edu.unal.androidtic_tac_toe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private TicTacToeGame mGame;
    // Buttons making up the board

    private boolean computerIsMoving;
    private boolean mGameOver, firstHuman;

    private Integer human_wins, android_wins, ties;

    private BoardView mBoardView;

    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;

    // Listen for touches on the board







    private Button mBoardButtons[];


    Handler handler = new Handler();
    // Various text displayed
    private TextView mInfoTextView, mInfoTextAndroidWins, mInfoTextHumanWins, mInfoTextTiesWins;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mGameOver = false;
        firstHuman = false;
        human_wins = 0;
        computerIsMoving = false;
        android_wins = 0;
        ties = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGame = new TicTacToeGame();
        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);
        // Listen for touches on the board
        mBoardView.setOnTouchListener(mTouchListener);

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

    // Set up the game board.
    private void startNewGame() {
        mGameOver = false;
        firstHuman = !firstHuman;
        mGame.clearBoard();
        mBoardView.invalidate(); // Redraw the board
        // Reset all buttons

        computerIsMoving = false;


        // Human goes first
        if (firstHuman)
            mInfoTextView.setText(R.string.first_human);
        else {
            int move = mGame.getComputerMove();
            mInfoTextView.setText(R.string.second_human);

//            mGame.setMove(mGame.COMPUTER_PLAYER, move);

        }
    }    // End of startNewGame



    // Handles clicks on the game board buttons



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }


    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                startNewGame();
                return true;
            case R.id.ai_difficulty:
                showDialog(DIALOG_DIFFICULTY_ID);
                return true;
            case R.id.quit:
                showDialog(DIALOG_QUIT_ID);
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
                int pos = row * 3 + col;

                if (!computerIsMoving && !mGameOver && setMove(TicTacToeGame.HUMAN_PLAYER, pos)) {

                    mHumanMediaPlayer.start();    // Play the sound effect
                    computerIsMoving = true;



                    handler.postDelayed(new Runnable() {

                        public void run() {
                            int winner = mGame.checkForWinner();
                            if (winner == 0) {

                                mInfoTextView.setText(R.string.turn_computer);
                                mComputerMediaPlayer.start();
                                int move = mGame.getComputerMove();
                                setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                                winner = mGame.checkForWinner();

                            }


                            if (winner == 0)
                                mInfoTextView.setText(R.string.turn_human);
                            else if (winner == 1) {
                                mInfoTextView.setText(R.string.result_tie);
                                ties++;
                            } else if (winner == 2) {
                                mInfoTextView.setText(R.string.result_human_wins);
                                human_wins++;
                            } else {
                                mInfoTextView.setText(R.string.result_computer_wins);
                                android_wins++;
                            }
                            if (winner != 0) {
                                mGameOver = true;
                                mInfoTextAndroidWins.setText(android_wins.toString());
                                mInfoTextHumanWins.setText(human_wins.toString());
                                mInfoTextTiesWins.setText(ties.toString());

                            }
                            computerIsMoving = false;
                            mBoardView.invalidate();
                        }

                    }, 750);




                        }

// So we aren't notified of continued events when finger is moved
                    return false;
                }

    };





        @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch(id) {
            case DIALOG_DIFFICULTY_ID:

                builder.setTitle(R.string.difficulty_choose);

                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                        getResources().getString(R.string.difficulty_expert)};


                int selected = mGame.getDifficultyLevel().ordinal();
                // selected is the radio button that should be selected.

                builder.setSingleChoiceItems(levels, selected,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss();   // Close dialog

                                switch (item){
                                    case 0:
                                        mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
                                        break;
                                    case 1:
                                        mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
                                        break;
                                    case 2:
                                        mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
                                        break;
                                }
                                // Display the selected difficulty level
                                Toast.makeText(getApplicationContext(), levels[item],
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog = builder.create();

                break;

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


}