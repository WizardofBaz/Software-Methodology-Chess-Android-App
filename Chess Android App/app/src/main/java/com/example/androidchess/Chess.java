package com.example.androidchess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidchess.pieces.*;
import com.example.androidchess.Draw;
import com.example.androidchess.Recorded_Games;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static android.provider.Telephony.Mms.Part.FILENAME;
import static com.example.androidchess.Draw.drawPress;
import static com.example.androidchess.Recorded_Games.Games;


public class Chess extends AppCompatActivity implements View.OnClickListener {

    private static final String FILE_NAME = "save.txt";

    public static HashMap<String, String> whitePieces = new HashMap<String, String>();
    public static HashMap<String, String> blackPieces = new HashMap<String, String>();
    Piece[][] board = new Piece[8][8];

    public static Piece lastPieceMoved;
    public static Piece capturedPiece;
    static boolean gameOver = false;
    static boolean whiteTurn = true;
    static boolean check = false;
    boolean r = false;
    Button sButton;
    static Button undoButton;


    static String bkPosition = "e8";
    static String wkPosition = "e1";


    Button[][] buttonBoard = new Button[8][8];

    String name;
    EditText gameName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Load();
        whitePieces = Board.setWhitePieces(board);
        blackPieces = Board.setBlackPieces(board);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting up images on buttonArray
        updateBoard(board);

        TextView checkText = findViewById(R.id.checkText);

        checkText.setVisibility(View.INVISIBLE);
        undoButton = findViewById(R.id.undoButton);
        undoButton.setEnabled(false);

        gameName = (EditText) findViewById(R.id.gameName);


    }


    Boolean onePressed = false;

    ArrayList<String> Moves = new ArrayList<String>();

    String from;
    String to;

    @Override
    public void onClick(View v) {

        TextView checkText = findViewById(R.id.checkText);


        TextView tT = findViewById(R.id.turnText);


        if (!onePressed) {

            if (!((Button) v).getText().toString().equals("")) {
                onePressed = true;
                from = v.getResources().getResourceEntryName(v.getId()).substring(10);
//                    System.out.println("from: "+ oldPos);

            }
            return;
        } else {


            onePressed = false;

            to = v.getResources().getResourceEntryName(v.getId()).substring(10);
//                System.out.println("to: "+ newPos);


            if (performMove(from, to)) {
                undoButton.setEnabled(true);
                updateBoard(board);
                Piece.updateHashmaps(board, whitePieces, blackPieces);


                Moves.add(from);
                Moves.add(to);


//                    System.out.println(Moves + "MOVES");


                if (whiteTurn) {
                    tT.setText("White Turn");
                } else {
                    tT.setText("Black Turn");
                }

                if (check) {
                    checkText.setVisibility(View.VISIBLE);
                } else {
                    checkText.setVisibility(View.INVISIBLE);

                }

            } else {
                System.out.println("CANT DO");
                if (whiteTurn) {
                    tT.setText("White Turn : Illegal Move");
                } else {
                    tT.setText("Black Turn : Illegal Move");
                }
            }
            ;


        }


    }

    public void recreate(View v){
        this.recreate();
        whiteTurn = true;
        gameOver = false;
        gameName.getText().clear();
    }

    public void updateBoard(Piece[][] board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

                String buttonID = "button_" + i + j + "_" + (char) (97 + j) + (8 - i);
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttonBoard[i][j] = findViewById(resID);
                buttonBoard[i][j].setOnClickListener(this);
                buttonBoard[i][j].setTextSize(30);

                buttonBoard[i][j].setText("");
                if (board[i][j] != null) {
                    buttonBoard[i][j].setText(board[i][j].toImage());
                }
            }
        }


    }

    public void resign(View view) {
        TextView tT = findViewById(R.id.turnText);

        if (whiteTurn) {
            tT.setText("Black Wins!");
            gameOver = true;
            r = true;
            for (int i = 0; i < buttonBoard.length; i++) {
                for (int j = 0; j < buttonBoard.length; j++) {
                    buttonBoard[i][j].setEnabled(false);
                }
            }

        } else {
            tT.setText("White Wins!");
            gameOver = true;
            r = true;
            for (int i = 0; i < buttonBoard.length; i++) {
                for (int j = 0; j < buttonBoard.length; j++) {
                    buttonBoard[i][j].setEnabled(false);
                }
            }

        }
    }

    public void recordedGamesButton(View view) {
        Intent intent = new Intent(this, Recorded_Games.class);
        startActivity(intent);
        getApplicationContext();
    }


    public void aiMove(View v) {
        System.out.println(wkPosition+" "+bkPosition);
        TextView checkText = findViewById(R.id.checkText);
        TextView tT = findViewById(R.id.turnText);



        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece possiblePiece = board[r][c];
                if (possiblePiece != null) {

                    String piecePosition = possiblePiece.currentPosition;

                    if ((whiteTurn && possiblePiece.getColor().equals("white")) ||
                            (!whiteTurn && possiblePiece.getColor().equals("black"))) {
                        for (char a = 'a'; a < 'i'; a++) {
                            for (int i = 1; i <= 8; i++) {
                                String possibleMove = "" + a + i;

                                if (performMove(piecePosition, possibleMove)) {
                                    undoButton.setEnabled(true);
                                    updateBoard(board);
                                    Piece.updateHashmaps(board, whitePieces, blackPieces);


                                    Moves.add(piecePosition);
                                    Moves.add(possibleMove);


                                    if (whiteTurn) {
                                        tT.setText("White Turn");
                                    } else {
                                        tT.setText("Black Turn");
                                    }

                                    if (check) {
                                        checkText.setVisibility(View.VISIBLE);
                                    } else {
                                        checkText.setVisibility(View.INVISIBLE);

                                    }

                                    return;

                                }


                            }
                        }


                    }


                }
            }

        }


        return;
    }

    public void undo(View v) {
        TextView tT = findViewById(R.id.turnText);
        undoButton.setEnabled(false);


        String newPosition = Moves.get(Moves.size() - 1);
        String oldPosition = Moves.get(Moves.size() - 2);


        lastPieceMoved.count--;
        board[Piece.getRow(newPosition)][Piece.getCol(newPosition)] = capturedPiece;
        board[Piece.getRow(oldPosition)][Piece.getCol(oldPosition)] = lastPieceMoved;
        whiteTurn = !whiteTurn;
        updateBoard(board);
        Piece.updateHashmaps(board, whitePieces, blackPieces);

        if (whiteTurn) {
            tT.setText("White Turn");
        } else {
            tT.setText("Black Turn");
        }


    }

    public void openPop() {

        if(gameOver == true) {
            PopUp exampleDialog = new PopUp();
            exampleDialog.show(getSupportFragmentManager(), "Saved");
        }
    }


    public void saveGame(View view) {
        TextView tT = findViewById(R.id.turnText);

        LocalDate date = LocalDate.now();

        if(TextUtils.isEmpty(gameName.getText())){
            Toast.makeText(getApplicationContext(), "To save you need a game name + game must be over",Toast.LENGTH_SHORT).show();
        }
if(gameOver == true) {
    tT.setText("Game Saved!");
    openPop();

    name = gameName.getText().toString();

    Games.put(name + " : " + date, Moves);


    FileOutputStream fos = null;
    try {
        fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
        fos.write(name.getBytes());
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        if (fos != null) {
            System.out.println("Saved!!!!!!");
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
    }

//    public void Load(){
//        FileInputStream fis = null;
//        try {
//            fis = openFileInput(FILE_NAME);
//            InputStreamReader isr = new InputStreamReader(fis);
//            BufferedReader br = new BufferedReader(isr);
//            StringBuilder sb = new StringBuilder();
//            String Text;
//
//            while((Text = br.readLine())!= null){
//                sb.append(Text).append("\n");
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        finally {
//            if(fis != null){
//                try {
//                    fis.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    public void savePop() {
        Save exampleDialog = new Save();
        exampleDialog.show(getSupportFragmentManager(), "Do you want to save?");
    }

    public void draw(View view) {
        TextView tT = findViewById(R.id.turnText);
        //tT.setText("Draw");
        openDraw();
        if (drawPress == true) {
            tT.setText("DRAW");
            for (int i = 0; i < buttonBoard.length; i++) {
                for (int j = 0; j < buttonBoard.length; j++) {
                    buttonBoard[i][j].setEnabled(false);
                }
            }

        }

    }

    public void openDraw() {
        if (drawPress == false) {
            Draw draw = new Draw();
            draw.show(getSupportFragmentManager(), "Draw?");
        }
    }

    public boolean performMove(String oldPos, String newPos) {
        TextView checkText = findViewById(R.id.checkText);
        TextView tT = findViewById(R.id.turnText);


        int oldR = Piece.getRow(oldPos);
        int oldC = Piece.getCol(oldPos);

        Piece currentPiece = board[oldR][oldC];

        boolean moveSuccessful = false;


        if (currentPiece != null) {

            // Make sure that player is moving their own piece
            if (currentPiece.movingOwnPiece(whiteTurn)) {

                // Make sure that player is not attacking their own piece
                if (!Piece.attackingOwnPiece(board, oldPos, newPos)) {

                    // Make sure that piece is moving properly
                    if (currentPiece.moveValid(board, oldPos, newPos, whiteTurn)) {

                        // -----Create a copy of the board-----------
                        Piece[][] boardCopy = Arrays.stream(board).map(Piece[]::clone).toArray(Piece[][]::new);

                        // perform move on copied board
                        Piece.move(boardCopy, oldPos, newPos);

                        // change position of king trackers
                        if (currentPiece.toString().charAt(1) == 'K') {
                            if (whiteTurn) {
                                wkPosition = newPos;
                            } else {
                                bkPosition = newPos;
                            }
                        }

                        // on copied board -test if players move resulted in check of own king
                        Piece.updateHashmaps(boardCopy, whitePieces, blackPieces);

                        if (whiteTurn) {
                            if (!Piece.kingChecked(boardCopy, wkPosition, blackPieces, whiteTurn)) {
                                moveSuccessful = true;
                                check = false;
                            } else {
                            }
                            ;

                        } else {
                            if (!Piece.kingChecked(boardCopy, bkPosition, whitePieces, whiteTurn)) {
                                moveSuccessful = true;
                                check = false;
                            }
                        }

                        // revert changes to king trackers and currentPiece position
                        currentPiece.currentPosition = oldPos;
                        currentPiece.count--;
                        if (currentPiece.toString().charAt(1) == 'K') {
                            if (whiteTurn) {
                                wkPosition = oldPos;
                            } else {
                                bkPosition = oldPos;
                            }
                        }
                        // ------DONE WITH COPIED BOARD---------


                        //if move was successful in copied board then finalize changes on board
                        if (moveSuccessful) {

                            int newR = Piece.getRow(newPos);
                            int newC = Piece.getCol(newPos);

                            capturedPiece = board[newR][newC];
                            Piece.move(board, oldPos, newPos);

                            // Checking if pawn was moved
                            if (currentPiece.toString().charAt(1) == 'p') {

                                if (whiteTurn) {
                                    if (currentPiece.currentPosition.charAt(1) == '8') {

                                        Piece.pawnPromotion(board, currentPiece, oldPos + " " + newPos, whiteTurn);

                                    }

                                } else {

                                    if (currentPiece.currentPosition.charAt(1) == '1') {
                                        Piece.pawnPromotion(board, currentPiece, oldPos + " " + newPos, whiteTurn);

                                    }

                                }

                            }

                            if (lastPieceMoved != null) {
                                lastPieceMoved.epAvail = false;
                            }
                            lastPieceMoved = currentPiece;

                            Piece.updateHashmaps(board, whitePieces, blackPieces);


                            if (currentPiece.toString().charAt(1) == 'K') {
                                if (whiteTurn) {
                                    wkPosition = newPos;
                                } else {
                                    bkPosition = newPos;
                                }
                            }

                            /**
                             * This section of the code makes sure that
                             * the king is not in check. This will check
                             * to see this after every move. Inside this section
                             * there is also the check to make sure that one
                             * of the players is not in checkmate which would
                             * result in the other player being defeated.
                             */

                            if (whiteTurn) {
                                // Testing if black King is in check
                                if (Piece.kingChecked(boardCopy, bkPosition, whitePieces, whiteTurn)) {
                                    check = true;

                                    // Check if opponent can make a move to get out of check

                                    Set<String> remainingBlackPieces;
                                    remainingBlackPieces = blackPieces.keySet();

                                    // Iterate through each of the opponents pieces
                                    int numberOfSavingMoves = 0;
                                    for (String piecePosition : remainingBlackPieces) {

                                        // For each piece check try moving it on a copied board
                                        numberOfSavingMoves += Piece.blackCheckMate(board, piecePosition,
                                                wkPosition, bkPosition, whitePieces, whiteTurn);

                                    }
                                    if (numberOfSavingMoves == 0) {
                                        checkText.setText("Checkmate Black Wins");
                                        checkText.setVisibility(View.VISIBLE);
                                        tT.setVisibility(View.INVISIBLE);
                                        gameOver = true;

                                        // Games.put(Moves,"Game1");

                                        //System.out.println(Games + "GAME");
                                        // savePop();
                                    }

                                }
                            } else {
                                // Testing if White King is in check
                                if (Piece.kingChecked(boardCopy, wkPosition, blackPieces, whiteTurn)) {
                                    check = true;

                                    // Check if opponent can make a move to get out of check

                                    Set<String> remainingWhitePieces;
                                    remainingWhitePieces = whitePieces.keySet();

                                    // Iterate through each of the opponents pieces
                                    int numberOfSavingMoves = 0;
                                    for (String piecePosition : remainingWhitePieces) {

                                        // For each piece check try moving it on a copied board
                                        numberOfSavingMoves += Piece.whiteCheckMate(board, piecePosition,
                                                wkPosition, bkPosition, blackPieces, whiteTurn);

                                    }

                                    if (numberOfSavingMoves == 0) {

                                        checkText.setText("Checkmate Black Wins");
                                        checkText.setVisibility(View.VISIBLE);
                                        tT.setVisibility(View.INVISIBLE);
                                        gameOver = true;
                                        //savePop();
                                    }
                                }
                            }

                        }

                    }

                }

            }

        }


        if (moveSuccessful) {
            whiteTurn = !whiteTurn;
        }


        if (gameOver == true) {
            //disable
            for (int i = 0; i < buttonBoard.length; i++) {
                for (int j = 0; j < buttonBoard.length; j++) {
                    buttonBoard[i][j].setEnabled(false);
                }
                savePop();
            }


        }

        return moveSuccessful;


    }


}



