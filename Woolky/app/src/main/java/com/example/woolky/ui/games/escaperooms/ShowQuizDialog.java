package com.example.woolky.ui.games.escaperooms;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
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
import android.widget.Toast;

import com.example.woolky.R;
import com.example.woolky.domain.games.escaperooms.EscapeRoom;
import com.example.woolky.domain.games.escaperooms.Quiz;
import com.example.woolky.utils.Triple;
import com.google.android.gms.maps.model.Polyline;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShowQuizDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowQuizDialog extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Quiz quiz;
    private Polyline polyline;
    private EscapeRoom escapeRoom;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ShowQuizDialog() {
        // Required empty public constructor
    }

    public ShowQuizDialog(Quiz quiz, Polyline polyline, EscapeRoom escapeRoom) {
        this.quiz = quiz;
        this.polyline = polyline;
        this.escapeRoom = escapeRoom;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShowQuizDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static ShowQuizDialog newInstance(String param1, String param2) {
        ShowQuizDialog fragment = new ShowQuizDialog();
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
                    //TODO: Criar um listener para que seja o PlayEscapeRoom a fazer esta lógica
                    //TODO: Quando o user acerta mostrar um digito do código final
                    RadioGroup rGroup = v.findViewById(R.id.quizAnswers);
                    int optionId = rGroup.getCheckedRadioButtonId();
                    View radioChoice = v.findViewById(optionId);
                    int index = rGroup.indexOfChild(radioChoice);

                    if (index == quiz.getIndexOfCorrectAnswer()) {
                        polyline.setColor(Color.GREEN);

                        int lineIndex = escapeRoom.getPolylines().indexOf(polyline);
                        Triple<Integer, Integer, Integer> triple = escapeRoom.getLinesCircles().get(lineIndex);
                        triple.setThird(Color.GREEN);

                        Toast.makeText(getActivity(), "Correct Answer", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getActivity(), "Wrong Answer", Toast.LENGTH_SHORT).show();


                    dialog.dismiss();
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
}