package com.example.woolky.domain;

import com.example.woolky.utils.Board;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.List;

public class TicTacToe extends Game{

    public enum Piece {Blank, X, O};

    @Exclude
    private Board<Piece> board;
    private List<Integer> lastPlayedPosition;
    @Exclude
    private Piece myPiece;
    private Piece currentPlayer;
    private Piece winner;

    public TicTacToe(int numParticipants, LatLng boardInitialPosition, Piece myPiece) {
        super(numParticipants);

        board = new Board<>(3, 30, boardInitialPosition);
        List<Piece> initialPositions = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            initialPositions.add(Piece.Blank);
        }
        board.setPositions(initialPositions);

        lastPlayedPosition = new ArrayList<>(2);
        lastPlayedPosition.add(-1);
        lastPlayedPosition.add(-1);
        this.myPiece = myPiece;
        this.currentPlayer = Piece.X;
        this.winner = null;
    }

    @Override
    @Exclude
    //-1 == Não terminou
    //0 == Empate
    //1 == Ganhou
    public int isFinished() {
        List<Piece> positions = board.getPositions();
        int boardDim = board.getDim();

        for (int i = 0; i < boardDim; i++) {
            //col
            if (positions.get(lastPlayedPosition.get(0) * boardDim + i) != myPiece)
                break;
            //row
            if (positions.get(i * boardDim + lastPlayedPosition.get(1)) != myPiece)
                break;
            //diag
            if (lastPlayedPosition.get(0).equals(lastPlayedPosition.get(1)) && positions.get(i * boardDim + i) != myPiece)
                break;
            //anti-diag
            if (lastPlayedPosition.get(0) + lastPlayedPosition.get(1) == boardDim - 1 &&
                    positions.get(i * boardDim + (boardDim - i - 1)) != myPiece)
                break;

            if (i == boardDim-1) {
                //Este player ganhou
                return 1;
            }
        }

        //checka o empate, se já não houver casas vazias e ele não ganhou quer dizer que é empate
        if (!positions.contains(Piece.Blank)) {
            return 0;
        }

        return -1;
    }

    public void finishGame(int finishState) {
        this.winner = finishState == 1 ? myPiece : Piece.Blank;
    }

    public void updatesLastPosition(List<Long> playedPosition, GoogleMap mMap) {
        List<Integer> playedPositionInt = new ArrayList<>(2);
        playedPositionInt.add(playedPosition.get(0).intValue());
        playedPositionInt.add(playedPosition.get(1).intValue());
        int position = playedPositionInt.get(0) * this.board.getDim() + playedPositionInt.get(1);
        if (myPiece == currentPlayer) {
            if (myPiece == Piece.X)
                this.board.playCircle(playedPositionInt, mMap);
            else
                this.board.playCross(playedPositionInt, mMap);

            this.board.getPositions().set(position, opponentPiece());
        } else {
            this.board.getPositions().set(position, myPiece);
        }
        this.lastPlayedPosition = playedPositionInt;

    }

    public Piece opponentPiece() {
        return myPiece == Piece.X ? Piece.O : Piece.X;
    }

    @Exclude
    public boolean isPlayValid(List<Integer> play) {
        if (play.get(0).equals(-1) || play.get(1).equals(-1)) {
            return false;
        }
        int position = play.get(0) * this.board.getDim() + play.get(1);
        if (this.board.getPositions().get(position) == Piece.Blank) {
            return true;
        }

        return false;
    }

    public void makePlay(List<Integer> playedPosition, GoogleMap mMap) {
        int position = playedPosition.get(0) * this.board.getDim() + playedPosition.get(1);
        this.board.getPositions().set(position, myPiece);
        this.lastPlayedPosition = playedPosition;
        if (myPiece == Piece.X)
            this.board.playCross(playedPosition, mMap);
        else
            this.board.playCircle(playedPosition, mMap);
        currentPlayer = opponentPiece();
    }

    @Exclude
    public boolean isMyTurn() {
        return currentPlayer == myPiece;
    }

    @Exclude
    public Board<Piece> getBoard() {
        return board;
    }

    public void setBoard(Board<Piece> board) {
        this.board = board;
    }

    public List<Integer> getLastPlayedPosition() {
        return lastPlayedPosition;
    }

    public void setLastPlayedPosition(List<Integer> lastPlayedPosition) {
        this.lastPlayedPosition = lastPlayedPosition;
    }

    @Exclude
    public Piece getMyPiece() {
        return myPiece;
    }

    public void setMyPiece(Piece myPiece) {
        this.myPiece = myPiece;
    }

    public Piece getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Piece currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Piece getWinner() {
        return winner;
    }

    public void setWinner(Piece winner) {
        this.winner = winner;
    }
}
