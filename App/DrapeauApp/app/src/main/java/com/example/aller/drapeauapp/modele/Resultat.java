package com.example.aller.drapeauapp.modele;

/**
 * Created by user on 25/09/2017.
 */

public class Resultat {

    private String correctAnswer;

    private String userAnswer;

/*
*###################################################################################################
####################################################################################################
--------------------------------------------CONSTRUCTEUR------------------------------------------------
####################################################################################################
####################################################################################################
*/

    public Resultat() {
    }

    public Resultat(String correctAnswer, String userAnswer) {
        this.correctAnswer = correctAnswer;
        this.userAnswer = userAnswer;
    }

    /*
*###################################################################################################
####################################################################################################
--------------------------------------------GETTERS------------------------------------------------
####################################################################################################
####################################################################################################
*/

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

/*
*###################################################################################################
####################################################################################################
--------------------------------------------SETTERS------------------------------------------------
####################################################################################################
####################################################################################################
*/
    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }
}
