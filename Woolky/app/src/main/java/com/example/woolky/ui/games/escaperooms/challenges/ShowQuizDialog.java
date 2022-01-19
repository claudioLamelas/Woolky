package com.example.woolky.ui.games.escaperooms.challenges;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.woolky.R;
import com.example.woolky.domain.games.escaperooms.Quiz;
import com.google.android.gms.maps.model.Polyline;

import java.util.List;

public class ShowQuizDialog extends DialogFragment {

    private AnswerQuizListener answerQuizListener;

    public interface AnswerQuizListener {
        void onDialogPositiveClick(DialogFragment dialog, int chosenAnswer, Quiz quiz, Polyline polyline);
    }

    private Quiz quiz;
    private Polyline polyline;

    public ShowQuizDialog() {}

    public ShowQuizDialog(Quiz quiz, Polyline polyline) {
        this.quiz = quiz;
        this.polyline = polyline;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_quiz_dialog, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_show_quiz_dialog, null);

        fillBlanksWithQuiz(v);

        builder.setView(v)
                .setPositiveButton("Answer", (dialog, id) -> {
                    RadioGroup rGroup = v.findViewById(R.id.quizAnswers);
                    int optionId = rGroup.getCheckedRadioButtonId();
                    View radioChoice = v.findViewById(optionId);
                    int index = rGroup.indexOfChild(radioChoice);
                    answerQuizListener.onDialogPositiveClick(this, index, quiz, polyline);
                });
        return builder.create();
    }

    private void fillBlanksWithQuiz(View v) {
        TextView question = v.findViewById(R.id.quizQuestion);
        RadioButton answer0 = v.findViewById(R.id.quizAnswer0);
        RadioButton answer1 = v.findViewById(R.id.quizAnswer1);
        RadioButton answer2 = v.findViewById(R.id.quizAnswer2);
        RadioButton answer3 = v.findViewById(R.id.quizAnswer3);

        question.setText(this.quiz.getQuestion());
        List<String> answersList = this.quiz.getAnswers();
        answer0.setText(answersList.get(0));
        answer1.setText(answersList.get(1));
        answer2.setText(answersList.get(2));
        answer3.setText(answersList.get(3));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            answerQuizListener = (AnswerQuizListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement AnswerQuizListener");
        }
    }
}