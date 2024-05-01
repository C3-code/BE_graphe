package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.Node;

public class Label implements Comparable<Label>{ //les elements donnes doivent etre comparables 
    private Node sommetCourant;
    private boolean marque;
    private int coutRealise;
    private Node pere;

    /** Getters des elements */
    public Node getSommetCourant(Node noeud) {
        return get
    }

    //retourner le cout du label 
    public int getCost(Label label) {
        return coutRealise;
    }

    public int compareTo(Label variable) {
        // this.coutRealise et variable.coutrealise  
        return 1;
    }
}

