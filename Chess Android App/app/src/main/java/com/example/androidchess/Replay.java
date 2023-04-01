package com.example.androidchess;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.androidchess.Recorded_Games;
import com.example.androidchess.Chess;
import com.example.androidchess.pieces.Board;
import com.example.androidchess.pieces.Piece;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;

import static com.example.androidchess.Recorded_Games.Games;
import static com.example.androidchess.Recorded_Games.selectedGameMoves;

public class Replay extends AppCompatActivity implements  View.OnClickListener{

    ArrayList<String> arrayList;
    int current;
    int length;
    Button nextTurn;
    public static HashMap<String, String> whitePieces = new HashMap<String, String>();
    public static HashMap<String, String> blackPieces = new HashMap<String, String>();
    Piece[][] board = new Piece[8][8];
    Button[][] buttonBoard = new Button[8][8];
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
    public static Stack myStack= new Stack <String>();



    @Override
    public void onClick(View view){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        whitePieces = Board.setWhitePieces(board);
        blackPieces = Board.setBlackPieces(board);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replay);
        TextView checkText = findViewById(R.id.checkText);

        checkText.setVisibility(View.INVISIBLE);

        updateBoard(board);

        System.out.println(Recorded_Games.selectedGame);
        System.out.println(Recorded_Games.selectedGameMoves);

        myStack.addAll(selectedGameMoves);
        Collections.reverse(myStack);


    }


    public void updateBoard(Piece[][] board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

                String buttonID = "button_" + i + j + "_" + (char) (97 + j) + (8 - i);
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttonBoard[i][j] = findViewById(resID);
                buttonBoard[i][j].setTextSize(30);

                buttonBoard[i][j].setText("");
                if (board[i][j] != null) {
                    buttonBoard[i][j].setText(board[i][j].toImage());
                }
            }
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
                // savePop();
            }


        }

        return moveSuccessful;
    }


//    Stack myStack= new Stack <String>();
//    myStack.addAll(selectedGameMoves);
//    Collections.reverse(myStack);

    public void nextButton(View view){

        if(myStack.isEmpty()){
            return;
        }
        String Oldbull = null;
        String Newbull = null;

        Oldbull = (String)myStack.pop();
        Newbull = (String)myStack.pop();

        System.out.println(Oldbull);
        System.out.println(Newbull);

        performMove(Oldbull,Newbull);
        updateBoard(board);

        System.out.println(Oldbull);
        System.out.println(Newbull);

    }

}
