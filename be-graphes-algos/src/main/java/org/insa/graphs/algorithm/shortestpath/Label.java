package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.Node;

public class Label implements Comparable<Label>{ //les elements donnes doivent etre comparables 
    private Node sommetCourant;
    private boolean marque;
    private float coutRealise;
    private Node pere;

    public Label(Node noeud)  {
        this.sommetCourant = noeud;
        this.marque = false;
        this.coutRealise = Float.POSITIVE_INFINITY;
        this.pere = null;
    }

    /** Getters des elements */
    public Node getSommetCourant() {
        return sommetCourant;
    }

    //retourner le cout du label 
    public float getCost() {
        return coutRealise;
    }
    
    public boolean getMarque() {
        return marque;
    }

    public Node getPere() {
        return pere;
    }

    public int compareTo(Label autre) {
        // this.coutRealise et variable.coutrealise  
        int resultat;
        if (this.getCost()<autre.getCost()) {
            resultat = -1;
        }
        else if (this.getCost()==autre.getCost()){
            resultat = 0;
        }
        else {
            resultat = 1;
        }
        return resultat;
    }



    /** Setters des elements */
    //modifier le sommet courant
    public void setSommetCourant(Node sommet) {
        this.sommetCourant = sommet;
    }

    //modifier le cout du label
    public void setCost(float cout) {
        this.coutRealise = cout;
    }
    
    public void setMarque() {
        this.marque = true;
    }

    public void setPere(Node father) {
        this.pere=father;
    }
}

