package org.insa.graphs.algorithm.shortestpath;
import org.insa.graphs.model.Node;
import org.insa.graphs.algorithm.AbstractInputData;

public class LabelStar extends Label{ //cette classe herite de la classe Label 
    private double coutDestination; //on rajoute une nouvelle info de coût estime a la destination
    

    public LabelStar(Node noeud, Node destination, ShortestPathData data)  {
        super(noeud);

        if (data.getMode() == AbstractInputData.Mode.LENGTH){ // si on s'interesse au cout en distance
            //Calcul de la distance à vol d'oiseau entre le noeud et la destination (methode calcul detaillee dans Point.java)
		    this.coutDestination = noeud.getPoint().distanceTo(destination.getPoint());
        }
        else { // si on s'interesse au cout en temps
            double vitesse = data.getGraph().getGraphInformation().getMaximumSpeed()*1000/3600;
            this.coutDestination = noeud.getPoint().distanceTo(destination.getPoint())/vitesse;
        }
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

