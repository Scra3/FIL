/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gramvf;

/**
 *
 * @author scra
 */
public class Pair {
   
    private String modele;
    private int compteur;
    
    public Pair(String modele, int compteur) {
        this.modele = modele;
        this.compteur = compteur;
    }


    public String getModele() {
        return modele;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public int getCompteur() {
        return compteur;
    }

    public void setCompteur(int compteur) {
        this.compteur = compteur;
    }
    
    
    
}
