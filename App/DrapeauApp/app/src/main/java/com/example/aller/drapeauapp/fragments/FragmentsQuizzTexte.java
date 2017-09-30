package com.example.aller.drapeauapp.fragments;


import android.content.Context;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.aller.drapeauapp.FragmentsActivity;
import com.example.aller.drapeauapp.ReplaceFont;
import com.example.aller.drapeauapp.R;
import com.example.aller.drapeauapp.ResultatActivity;
import com.example.aller.drapeauapp.db.DBConnection;
import com.example.aller.drapeauapp.modele.Drapeau;
import com.example.aller.drapeauapp.modele.RandomFlag;
import com.example.aller.drapeauapp.modele.Resultat;
import com.squareup.picasso.Picasso;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by aller on 19/09/2017.
 */

public class
FragmentsQuizzTexte extends Fragment implements View.OnClickListener, ProgressBarProgression, generateQuizz {

    //FontFamily
    private ReplaceFont replaceFont;

	//FramgmentActivity
	private FragmentActivity fragmentActivity;

	//ImageView
	private ImageView imageViewQuizzTexte;

	//Button
	private Button buttonQuizzTexteUn;
	private Button buttonQuizzTexteDeux;
	private Button buttonQuizzTexteTrois;
	private Button buttonQuizzTexteQuatre;

	private TextView timer;
	private TextView question;
	private int timerValue;

	//ProgressBar
	private ProgressBar progressBarQuizzTexte;


	//Interface
	private FragmentChanger mFragmentChanger;

	// database
	private DBConnection dbConnection;

	// resultat
	private Resultat resultat;

	// drapeaux liste

	private List<Drapeau> drapeauList = null;

/*
*###################################################################################################
####################################################################################################
--------------------------------------------METHODES------------------------------------------------
####################################################################################################
####################################################################################################
*/


	//redefinition de la methode onCreate View
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.quizz_texte_fragment, container, false);


		////////////////////////////////////////////////////////////////////////////////////////
		//Button
		buttonQuizzTexteUn = view.findViewById(R.id.buttonQuizzTexteUn);
		buttonQuizzTexteUn.setOnClickListener(this);

		buttonQuizzTexteDeux = view.findViewById(R.id.buttonQuizzTexteDeux);
		buttonQuizzTexteDeux.setOnClickListener(this);

		buttonQuizzTexteTrois = view.findViewById(R.id.buttonQuizzTexteTrois);
		buttonQuizzTexteTrois.setOnClickListener(this);

		buttonQuizzTexteQuatre = view.findViewById(R.id.buttonQuizzTexteQuatre);
		buttonQuizzTexteQuatre.setOnClickListener(this);

		//imageView
		imageViewQuizzTexte = view.findViewById(R.id.imageViewQuizzTexteFragment);
		timer = view.findViewById(R.id.textView3);

		timerValue = 11;
		question = view.findViewById(R.id.textView);
		question.setText("Question " + FragmentsActivity.questionNumber);

		//ProgressBar
		progressBarQuizzTexte = view.findViewById(R.id.progressBarQuizzTexte);

		/////////////////////////////////////////////////////////////////////////////////////////

		//On retourne la vue
		return view;
	}

	/*
	Au démarage un quizz est tirer au hasard
	 */
	@Override
	public void onStart() {
		super.onStart();
		generateRandomQuizz();
	}


	/*
	Gestion de la ProgressBar
	 */
	@Override
	public void incrementProgressBar() {
		progressBarQuizzTexte.incrementProgressBy(1);
		timerValue--;
		timer.setText(String.valueOf(timerValue));
	}

	@Override
	public void resetProgressBar() {
		progressBarQuizzTexte.setProgress(0);
	}

	/*
	Gestion du quizz
	 */
	@Override
	public void generateRandomQuizz() {
		try {
			dbConnection = new DBConnection(getContext());
			RandomFlag randomFlag = new RandomFlag();
			List<Integer> integers = new ArrayList<>();
			int min = 1;
			int max = dbConnection.getDaoDrapeau().queryForAll().size();
			integers = randomFlag.generateRandomIntList(min, max, 4);
			drapeauList = new ArrayList<>();
			for (Integer i :
					integers) {
				drapeauList.add(dbConnection.getDaoDrapeau().queryForAll().get(i));
			}
			buttonQuizzTexteUn.setText(drapeauList.get(0).getPays());
			buttonQuizzTexteDeux.setText(drapeauList.get(1).getPays());
			buttonQuizzTexteTrois.setText(drapeauList.get(2).getPays());
			buttonQuizzTexteQuatre.setText(drapeauList.get(3).getPays());
			int randomInt = randomFlag.generateRandomInt(0, 3);
			Picasso.with(getContext()).load(drapeauList.get(randomInt)
					.getUrlImage())
					.into(imageViewQuizzTexte);
			resultat = new Resultat("", "");
			resultat.setCorrectAnswer(drapeauList.get(randomInt).getPays());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	/////////////////////////////////////////////////////////////////////////////////


	//Interface pour le changement de fragment
	public interface FragmentChanger {
		public void remplacementDunFragmentUneFoisLeQuizzLance();
	}


	//redefintion de la methode onAttach pour le changement de fragment
	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		try {
			mFragmentChanger = (FragmentChanger) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString() + " Erreur onAttach");
		}
	}


	//Redefintion de la methode onClick pour les actions des boutons
	@Override
	public void onClick(View view) {
		Log.i("info", "click sur un Button");
		if (view.getId() == buttonQuizzTexteUn.getId()) {
			resultat.setUserAnswer(drapeauList.get(0).getPays());
		} else if (view.getId() == buttonQuizzTexteDeux.getId()) {
			resultat.setUserAnswer(drapeauList.get(1).getPays());
		} else if (view.getId() == buttonQuizzTexteTrois.getId()) {
			resultat.setUserAnswer(drapeauList.get(2).getPays());
		} else if (view.getId() == buttonQuizzTexteQuatre.getId()) {
			resultat.setUserAnswer(drapeauList.get(3).getPays());
		}
		ResultatActivity.resultatList.add(resultat);
		mFragmentChanger.remplacementDunFragmentUneFoisLeQuizzLance();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (resultat.getUserAnswer().equals(""))
			ResultatActivity.resultatList.add(resultat);
	}

}//end.
