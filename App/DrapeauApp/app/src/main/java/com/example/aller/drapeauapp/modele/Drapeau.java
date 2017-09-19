package com.example.aller.drapeauapp.modele;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by aller on 19/09/2017.
 */
@DatabaseTable(tableName = "drapeau")
public class Drapeau {

    @DatabaseField(id = true)
    private String pays;

    @DatabaseField
    private String image;


    //CONSTRUCTEUR
    public Drapeau() {
    }

    public Drapeau(String pays, String image) {
        this.pays = pays;
        this.image = image;
    }

    //GET
    public String getPays() {
        return pays;
    }

    public String getImage() {
        return image;
    }


    //SET
    public void setPays(String pays) {
        this.pays = pays;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
