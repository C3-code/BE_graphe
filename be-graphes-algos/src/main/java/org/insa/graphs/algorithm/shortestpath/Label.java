package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.Node;

public class Label implements Comparable<Label>{ //les elements donnes doivent etre comparables 
    private Node sommetCourant;
    private boolean marque;
    private double coutRealise;
    private Node pere;

    public Label(Node noeud)  {
        this.sommetCourant = noeud;
        this.marque = false;
        this.coutRealise = Double.POSITIVE_INFINITY; //type avec une majuscule => c'est un objet et pas un type 
        this.pere = null;
    }

    /** Getters des elements */
    public Node getSommetCourant() {
        return sommetCourant;
    }

    //retourner le cout du label 
    public double getCost() {
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
    public void setCost(double cout) {
        this.coutRealise = cout;
    }
    
    public void setMarque() {
        this.marque = true;
    }

    public void setPere(Node father) {
        this.pere=father;
    }
}

