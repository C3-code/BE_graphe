package org.insa.graphs.algorithm.shortestpath;
import org.insa.graphs.model.Node;

public class LabelStar extends Label{ //cette classe herite de la classe Label 
    private double coutDestination; //on rajoute une nouvelle info de coût
    

    public LabelStar(Node noeud, Node destination)  {
        super(noeud);

        //Calcul de la distance à vol d'oiseau entre le noeud et la destination (methode calcul detaillee dans Point.java)
		this.coutDestination = noeud.getPoint().distanceTo(destination.getPoint());
    }

    /** Getters des elements */
    //specification pour le cout destination qui n'est pas dans la classe Label mère
    public double getCostDestination() {
        return coutDestination;
    }

    //Recuperer le cout total (ici cout depuis origine + cout estime destination)
    public double getTotalCost() {
        return this.getCost() + coutDestination;
    }

    /** Setters des elements */
    //modifier le sommet courant
    public void setCostDestination(double cout) {
        this.coutDestination = cout;
    }
}

