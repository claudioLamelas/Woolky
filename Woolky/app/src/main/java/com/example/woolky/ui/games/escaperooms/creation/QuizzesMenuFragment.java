package com.example.woolky.ui.games.escaperooms.creation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.woolky.R;
import com.example.woolky.domain.games.escaperooms.EscapeRoom;
import com.example.woolky.domain.games.escaperooms.Quiz;

import java.util.ArrayList;
import java.util.List;

public class QuizzesMenuFragment extends Fragment implements CreateNewQuizDialog.CreateNewQuizListener {

    private EscapeRoom escapeRoom;
    public List<String> quizQuestions;
    private ArrayAdapter<String> arrayAdapter;

    public QuizzesMenuFragment(EscapeRoom escapeRoom) {
        this.escapeRoom = escapeRoom;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quizzes_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.quizzesBackButton).setOnClickListener(v -> getActivity().onBackPressed());

        view.findViewById(R.id.createNewQuizButton).setOnClickListener(v -> {
            CreateNewQuizDialog dialog = new CreateNewQuizDialog(null, -1);
            dialog.show(getChildFragmentManager(), "quiz");
        });

        ListView lv = view.findViewById(R.id.escapeRoomsList);

        quizQuestions = new ArrayList<>();
        for (Quiz q : escapeRoom.getQuizzes()) {
            quizQuestions.add(q.getQuestion());
        }

        arrayAdapter = new ArrayAdapter<>
                (getActivity(), android.R.layout.simple_list_item_1, quizQuestions);
        lv.setAdapter(arrayAdapter);

        lv.setOnItemClickListener((parent, view1, position, id) -> {
            Quiz chosen = escapeRoom.getQuizzes().get(position);
            CreateNewQuizDialog dialog = new CreateNewQuizDialog(chosen, position);
            dialog.show(getChildFragmentManager(), "quiz");
        });
        
        lv.setOnItemLongClickListener((parent, view1, position, id) -> {
            quizQuestions.remove(position);
            escapeRoom.getQuizzes().remove(position);
            arrayAdapter.notifyDataSetChanged();
            Toast.makeText(getActivity(), "Quiz deleted", Toast.LENGTH_SHORT).show();
            return false;
        });
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, Quiz quiz, boolean isEdit, int chosenIndex) {
        if (isEdit) {
            quizQuestions.set(chosenIndex, quiz.getQuestion());
            escapeRoom.getQuizzes().set(chosenIndex, quiz);
            Toast.makeText(getActivity(), "Quiz edited", Toast.LENGTH_SHORT).show();
        } else {
            quizQuestions.add(quiz.getQuestion());
            escapeRoom.getQuizzes().add(quiz);
            Toast.makeText(getActivity(), "Quiz added", Toast.LENGTH_SHORT).show();
        }
        arrayAdapter.notifyDataSetChanged();
        dialog.dismiss();
    }
}