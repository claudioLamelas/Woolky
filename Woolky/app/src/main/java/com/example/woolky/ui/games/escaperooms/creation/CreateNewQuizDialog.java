package com.example.woolky.ui.games.escaperooms.creation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.woolky.R;
import com.example.woolky.domain.games.escaperooms.Quiz;

import java.util.ArrayList;
import java.util.List;

public class CreateNewQuizDialog extends DialogFragment {

    private final int chosenIndex;
    private CreateNewQuizListener listener;
    private Quiz chosen;

    public interface CreateNewQuizListener {
        void onDialogPositiveClick(DialogFragment dialog, Quiz quiz, boolean isEdit, int chosenIndex);
    }

    public CreateNewQuizDialog(Quiz chosen, int chosenIndex) {
        this.chosen = chosen;
        this.chosenIndex = chosenIndex;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_new_quiz_dialog, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_create_new_quiz_dialog, null);

        if (chosen != null) {
            fillFields(v);
        }

        String buttonText = chosen != null ? "Edit" : "Create";
        boolean isEdit = chosen != null;

        builder.setView(v)
                .setPositiveButton(buttonText, (dialog, id) -> {
                    Quiz q = createQuiz(v);
                    listener.onDialogPositiveClick(this, q, isEdit, chosenIndex);
                })
                .setNegativeButton("Close", (dialog, id) -> dialog.dismiss());
        return builder.create();
    }

    private void fillFields(View v) {
        ((TextView) v.findViewById(R.id.dialogTitle)).setText("Edit Quiz");

        EditText question = v.findViewById(R.id.question);
        EditText answer1 = v.findViewById(R.id.answer1);
        EditText answer2 = v.findViewById(R.id.answer2);
        EditText answer3 = v.findViewById(R.id.answer3);
        EditText answer4 = v.findViewById(R.id.answer4);
        RadioGroup rGroup = v.findViewById(R.id.correctAnswerGroup);

        question.setText(chosen.getQuestion());
        answer1.setText(chosen.getAnswers().get(0));
        answer2.setText(chosen.getAnswers().get(1));
        answer3.setText(chosen.getAnswers().get(2));
        answer4.setText(chosen.getAnswers().get(3));
        View radio = rGroup.getChildAt(chosen.getIndexOfCorrectAnswer());
        rGroup.check(radio.getId());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (CreateNewQuizListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement CreateNewQuizListener");
        }
    }

    private Quiz createQuiz(View v) {
        EditText question = v.findViewById(R.id.question);
        EditText answer1 = v.findViewById(R.id.answer1);
        EditText answer2 = v.findViewById(R.id.answer2);
        EditText answer3 = v.findViewById(R.id.answer3);
        EditText answer4 = v.findViewById(R.id.answer4);
        RadioGroup rGroup = v.findViewById(R.id.correctAnswerGroup);

        List<String> answers = new ArrayList<>();
        answers.add(answer1.getText().toString());
        answers.add(answer2.getText().toString());
        answers.add(answer3.getText().toString());
        answers.add(answer4.getText().toString());

        int optionId = rGroup.getCheckedRadioButtonId();
        View radioChoice = v.findViewById(optionId);
        int index = rGroup.indexOfChild(radioChoice);

        return new Quiz(question.getText().toString(), answers, index);
    }
}