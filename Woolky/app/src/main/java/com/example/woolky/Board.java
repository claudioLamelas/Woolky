package com.example.woolky;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class Board {

    public int dim, sizeOfSquareSide;
    public LatLng initialPosition;

    public Board(int dim, int sizeOfSquareSide, LatLng initialPosition) {
        this.dim = dim;
        this.sizeOfSquareSide = sizeOfSquareSide;
        this.initialPosition = initialPosition;
    }

    public boolean inBoard(LatLng position) {
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

    public int[] getPositionInBoard(LatLng posicaoAtual) {
        int[] linhaColuna = {-1, -1};
        if (inBoard(posicaoAtual)) {
            int linha = -1;
            int coluna = -1;
            for (int i = dim - 1; i >= 0; i--) {
                if (linha == -1 && Double.compare(posicaoAtual.latitude, LocationCalculator.getPositionXMetersBelow(initialPosition, sizeOfSquareSide, i).latitude) < 0) {
                    linha = i;
                    linhaColuna[0] = linha;
                }

                if (coluna == -1 && Double.compare(posicaoAtual.longitude, LocationCalculator.getPositionXMetersRight(initialPosition, sizeOfSquareSide, i).longitude) > 0) {
                    coluna = i;
                    linhaColuna[1] = coluna;
                }
            }
        }
        return linhaColuna;
    }

    private void drawLine(GoogleMap mMap, LatLng inicioLinha, LatLng fimLinha) {
        mMap.addPolyline((new PolylineOptions()).clickable(false).add(inicioLinha, fimLinha));
    }
}
