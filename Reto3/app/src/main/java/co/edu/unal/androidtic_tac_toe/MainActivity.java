package co.edu.unal.androidtic_tac_toe;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private TicTacToeGame mGame;
    // Buttons making up the board

    private boolean mGameOver, firstHuman;

    private Integer human_wins, android_wins, ties;






    private Button mBoardButtons[];



    // Various text displayed
    private TextView mInfoTextView, mInfoTextAndroidWins, mInfoTextHumanWins, mInfoTextTiesWins;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mGameOver = false;
        firstHuman = false;
        human_wins = 0;
        android_wins = 0;
        ties = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE];
        mBoardButtons[0] = (Button) findViewById(R.id.one);
        mBoardButtons[1] = (Button) findViewById(R.id.two);
        mBoardButtons[2] = (Button) findViewById(R.id.three);
        mBoardButtons[3] = (Button) findViewById(R.id.four);
        mBoardButtons[4] = (Button) findViewById(R.id.five);
        mBoardButtons[5] = (Button) findViewById(R.id.six);
        mBoardButtons[6] = (Button) findViewById(R.id.seven);
        mBoardButtons[7] = (Button) findViewById(R.id.eight);
        mBoardButtons[8] = (Button) findViewById(R.id.nine);

        mInfoTextView = (TextView) findViewById(R.id.information);
        mInfoTextAndroidWins = (TextView) findViewById(R.id.android_score);
        mInfoTextHumanWins = (TextView) findViewById(R.id.human_score);
        mInfoTextTiesWins = (TextView) findViewById(R.id.tie_score);
        mGame = new TicTacToeGame();
        startNewGame();
    }

    // Set up the game board.
    private void startNewGame() {
        mGameOver = false;
        firstHuman = !firstHuman;
        mGame.clearBoard();
        // Reset all buttons



        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
        }
        // Human goes first
        if (firstHuman)
            mInfoTextView.setText(R.string.first_human);
        else {
            int move = mGame.getComputerMove();
            mInfoTextView.setText(R.string.second_human);
            mGame.setMove(mGame.COMPUTER_PLAYER, move);
            mBoardButtons[move].setEnabled(false);
            mBoardButtons[move].setText(String.valueOf(mGame.COMPUTER_PLAYER));
             mBoardButtons[move].setTextColor(Color.rgb(200, 0, 0));

        }
    }    // End of startNewGame



    // Handles clicks on the game board buttons
    private class ButtonClickListener implements View.OnClickListener {
        int location;

        public ButtonClickListener(int location) {
            this.location = location;
        }

        public void onClick(View view) {
            if (mGameOver) return;
            if (mBoardButtons[location].isEnabled()) {
                setMove(TicTacToeGame.HUMAN_PLAYER, location);

                // If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_computer);
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    winner = mGame.checkForWinner();
                }


                if (winner == 0)
                    mInfoTextView.setText(R.string.turn_human);
                else if (winner == 1) {
                    mInfoTextView.setText(R.string.result_tie);
                    ties++;
                }
                else if (winner == 2) {
                    mInfoTextView.setText(R.string.result_human_wins);
                    human_wins++;
                }
                else {
                    mInfoTextView.setText(R.string.result_computer_wins);
                    android_wins++;
                }
                if (winner != 0){
                    mGameOver = true;
                    mInfoTextAndroidWins.setText(android_wins.toString());
                    mInfoTextHumanWins.setText(human_wins.toString());
                    mInfoTextTiesWins.setText(ties.toString());

                }
            }
        }

        private void setMove(char player, int location) {

            mGame.setMove(player, location);
            mBoardButtons[location].setEnabled(false);
            mBoardButtons[location].setText(String.valueOf(player));
            if (player == TicTacToeGame.HUMAN_PLAYER)
                mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0));
            else
                mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0));
        }




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add("New Game");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startNewGame();
        return true;
    }

}