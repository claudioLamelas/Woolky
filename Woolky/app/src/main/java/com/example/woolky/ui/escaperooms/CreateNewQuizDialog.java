package com.example.woolky.ui.escaperooms;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.woolky.R;
import com.example.woolky.domain.escaperooms.Quiz;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateNewQuizDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateNewQuizDialog extends DialogFragment {

    private CreateNewQuizListener listener;

    public interface CreateNewQuizListener {
        void onDialogPositiveClick(DialogFragment dialog, Quiz quiz);
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreateNewQuizDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateNewQuizDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateNewQuizDialog newInstance(String param1, String param2) {
        CreateNewQuizDialog fragment = new CreateNewQuizDialog();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_new_quiz_dialog, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_create_new_quiz_dialog, null);

        builder.setView(v)
                .setPositiveButton("Create", (dialog, id) -> {
                    Quiz q = createQuiz(v);
                    listener.onDialogPositiveClick(this, q);
                })
                .setNegativeButton("Close", (dialog, id) -> dialog.dismiss());
        return builder.create();
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