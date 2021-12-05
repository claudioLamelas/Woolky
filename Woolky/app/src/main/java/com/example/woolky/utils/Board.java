package com.example.woolky.utils;

import android.graphics.Color;

import com.example.woolky.utils.LocationCalculator;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class Board<T> {

    private int dim, sizeOfSquareSide;
    private LatLng initialPosition;
    private List<Polyline> boardLines;
    private List<T> positions;

    public Board(int dim, int sizeOfSquareSide, LatLng initialPosition) {
        this.dim = dim;
        this.sizeOfSquareSide = sizeOfSquareSide;
        //faz com que o utilizador comece no meio do tabuleiro
        this.initialPosition = getInitialPositionToTopLeft(initialPosition, sizeOfSquareSide);
        this.boardLines = new ArrayList<>();
    }

    private boolean inBoard(LatLng position) {
        boolean b1 = Double.compare(position.latitude, initialPosition.latitude) < 0;
        boolean b2 = Double.compare(position.latitude, LocationCalculator.getPositionXMetersBelow(initialPosition, sizeOfSquareSide, dim).latitude) > 0;
        boolean b3 = Double.compare(position.longitude, initialPosition.longitude) > 0;
        boolean b4 = Double.compare(position.longitude, LocationCalculator.getPositionXMetersRight(initialPosition, sizeOfSquareSide, dim).longitude) < 0;
        return b1 && b2 && b3 && b4;
    }

    public void drawBoard(GoogleMap mMap) {
        for (int i = 0; i <= dim; i++) {
            LatLng inicioLinhaVertical = LocationCalculator.getPositionXMetersRight(initialPosition, sizeOfSquareSide, i);
            LatLng fimLinhaVertical = LocationCalculator.getPositionXMetersBelow(initialPosition, sizeOfSquareSide, dim);
            fimLinhaVertical = LocationCalculator.getPositionXMetersRight(fimLinhaVertical, sizeOfSquareSide, i);
            drawLine(mMap, inicioLinhaVertical, fimLinhaVertical);

            LatLng inicioLinhaHorizontal = LocationCalculator.getPositionXMetersBelow(initialPosition, sizeOfSquareSide, i);
            LatLng fimLinhaHorizontal = LocationCalculator.getPositionXMetersRight(initialPosition, sizeOfSquareSide, dim);
            fimLinhaHorizontal = LocationCalculator.getPositionXMetersBelow(fimLinhaHorizontal, sizeOfSquareSide, i);
            drawLine(mMap, inicioLinhaHorizontal, fimLinhaHorizontal);
        }
    }

    private LatLng getInitialPositionToTopLeft(LatLng initialPosition, int sizeOfSquareSide) {
        initialPosition = LocationCalculator.getPositionXMetersRight(initialPosition, sizeOfSquareSide/2, -3);
        return LocationCalculator.getPositionXMetersBelow(initialPosition, sizeOfSquareSide/2, -3);
    }

    public List<Integer> getPositionInBoard(LatLng posicaoAtual) {
        List<Integer> linhaColuna = new ArrayList<>(2);
        linhaColuna.add(-1);
        linhaColuna.add(-1);

        if (inBoard(posicaoAtual)) {
            int linha = -1;
            int coluna = -1;
            for (int i = dim - 1; i >= 0; i--) {
                if (linha == -1 && Double.compare(posicaoAtual.latitude, LocationCalculator.getPositionXMetersBelow(initialPosition, sizeOfSquareSide, i).latitude) < 0) {
                    linha = i;
                    linhaColuna.remove(0);
                    linhaColuna.add(0, linha);
                }

                if (coluna == -1 && Double.compare(posicaoAtual.longitude, LocationCalculator.getPositionXMetersRight(initialPosition, sizeOfSquareSide, i).longitude) > 0) {
                    coluna = i;
                    linhaColuna.remove(1);
                    linhaColuna.add(1, coluna);
                }
            }
        }
        return linhaColuna;
    }

    private void drawLine(GoogleMap mMap, LatLng inicioLinha, LatLng fimLinha) {
        Polyline line = mMap.addPolyline((new PolylineOptions()).clickable(false).add(inicioLinha, fimLinha));
        boardLines.add(line);
    }

    public void playCircle(List<Integer> coordenadas, GoogleMap mMap) {
        if (coordenadas.get(0) > -1 && coordenadas.get(1) > -1) {
            LatLng centerOfCell = LocationCalculator.getPositionXMetersBelow(initialPosition, sizeOfSquareSide/2, coordenadas.get(0) * 2 + 1);
            centerOfCell = LocationCalculator.getPositionXMetersRight(centerOfCell, sizeOfSquareSide/2, coordenadas.get(1) * 2 + 1);
            mMap.addCircle(new CircleOptions().center(centerOfCell).radius(10.0).strokeColor(Color.RED));
        }
    }

    public void setInitialPosition(LatLng newInitialPosition) {
        this.initialPosition = getInitialPositionToTopLeft(newInitialPosition, this.sizeOfSquareSide);
    }

    public void remove() {
        for (Polyline l : boardLines) {
            l.remove();
        }
    }

    public List<T> getPositions() {
        return positions;
    }

    public int getDim() {
        return dim;
    }

    public void setPositions(List<T> initialPositions) {
        positions = initialPositions;
    }
}
