/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package binary.tree;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author scra
 */
public class Mot {

    private int token;
    private String mot;

    public Mot(String mot) {
        this.token = 0;
        this.mot = mot;
    }

    public Mot(String mot, int token) {
        this.mot = mot;
        this.token = token;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public String getMot() {
        return mot;
    }

    public void setMot(String mot) {
        this.mot = mot;
    }
}
