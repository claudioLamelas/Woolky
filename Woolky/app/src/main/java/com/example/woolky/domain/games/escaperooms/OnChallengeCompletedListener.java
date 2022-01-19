package com.example.woolky.domain.games.escaperooms;

import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.model.Polyline;

public interface OnChallengeCompletedListener {

    void challengeCompleted(DialogFragment dialog, Polyline polyline);

}
