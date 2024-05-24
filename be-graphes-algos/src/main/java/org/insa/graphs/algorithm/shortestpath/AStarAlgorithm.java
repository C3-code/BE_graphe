package org.insa.graphs.algorithm.shortestpath;

import java.util.ArrayList;
import java.util.Collections;

import org.insa.graphs.algorithm.AbstractSolution.Status;
import org.insa.graphs.algorithm.utils.BinaryHeap;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;

public class AStarAlgorithm extends DijkstraAlgorithm {

    public AStarAlgorithm(ShortestPathData data) {
        super(data);
    }
    
    @Override
    protected ShortestPathSolution doRun() {
        final ShortestPathData data = getInputData();
        ShortestPathSolution solution = null;

        /*-----------------------Algo A*--------------------------------------------------
        plus court chemin prenant en compte le cout realise et l'estimation du cout jusqu'a la destination*/

        //recuperer le graphe
        Graph graph = data.getGraph();
        int tailleGraphe = graph.size();
        Node noeudDestination = data.getDestination();

        //Recuperer le label du premier noeud
        LabelStar debut = new LabelStar(data.getOrigin(), noeudDestination,data);
        debut.setCost(0);

        //Declarer un binary heap pour stocker les labels des noeuds du graphe (de type label)
        BinaryHeap<Label> tasLabelStar = new BinaryHeap<Label>();

        //Creer un tableau de labels Star
        LabelStar tabLabelStar[] = new LabelStar[tailleGraphe];
        
        //inserer l'origine dans le tas
        tasLabelStar.insert(debut); 
        tabLabelStar[debut.getSommetCourant().getId()] = debut;

        boolean fin = false;
        while ( (!fin) && (!tasLabelStar.isEmpty()) ) {//tant qu'on n'a pas atteint la dest et que le tas n'est pas vide
            //Creation du label star du noeud courant 
            Label labelCourant = tasLabelStar.deleteMin(); 
            labelCourant.setMarque();
            System.out.println(labelCourant.getCost());
            Node noeudCourant = labelCourant.getSommetCourant();

            // afficher sur l'ecran que le noeud a ete marque  
            notifyNodeMarked(noeudCourant); 
            if (noeudCourant == data.getDestination()) { // si il sagit de la destination, on sort de la boucle 
                fin = true;
            }
            
            //Traitement des sommets successeurs
            for(Arc arc : noeudCourant.getSuccessors()) { 
                if (data.isAllowed(arc)){ //filtre verifiant que le chemin est accessible par le mode transport choisi
                    Node noeudSuccesseur = arc.getDestination();
                    LabelStar labelSuccesseur;
    
                    if (tabLabelStar[arc.getDestination().getId()]==null){ // si le label successeur n'est pas dans la table, on le cree 
                        labelSuccesseur = new LabelStar(noeudSuccesseur, noeudDestination,data);
                        tabLabelStar[arc.getDestination().getId()] = labelSuccesseur;

                        notifyNodeReached(noeudSuccesseur);//afficher a l'ecran que ce noeud la a ete atteint
                    }
                    else {
                        labelSuccesseur = tabLabelStar[arc.getDestination().getId()];
                    }
    
                    //Calculer le nouveau cout potentiel pour aller jusqu au successeur
                    LabelStar successeurActualise = new LabelStar(noeudSuccesseur, noeudDestination,data);
                    double nouveauCout = labelCourant.getCost() + data.getCost(arc);
                    successeurActualise.setCost(nouveauCout); //mise a jour du cout 

                    
                    //(ancienCout==Double.POSITIVE_INFINITY || nouveauCout < ancienCout)
                    if (!labelSuccesseur.getMarque() && (labelSuccesseur.compareTo(successeurActualise) == 1)) { //si le noeud n'est pas marque ou nouveau cout inferieur
                        //Si le noeud a deja ete traite 
                        if (labelSuccesseur.getCost() != Double.POSITIVE_INFINITY) {
                            tasLabelStar.remove(labelSuccesseur);
                        }
                    
                        //mettre a jour le cout 
                        labelSuccesseur.setCost(nouveauCout);
                        labelSuccesseur.setPere(arc);

                        //Inserer le label actualise dans le tas
                        tasLabelStar.insert(labelSuccesseur);
                    }
                }
            }
        }

                // Destination has no predecessor, the solution is infeasible...
        if (tabLabelStar[data.getDestination().getId()] == null) {
            solution = new ShortestPathSolution(data, Status.INFEASIBLE);
        }
        else {

            // The destination has been found, notify the observers.
            notifyDestinationReached(data.getDestination());

            // Create the path from the array of predecessors...
            ArrayList<Arc> arcs = new ArrayList<>();
            Arc arc = tabLabelStar[data.getDestination().getId()].getPere();
            while (arc != null) {
                arcs.add(arc);
                arc = tabLabelStar[arc.getOrigin().getId()].getPere();
            }

            // Reverse the path...
            Collections.reverse(arcs);

            // Create the final solution.
            solution = new ShortestPathSolution(data, Status.OPTIMAL, new Path(graph, arcs));
        }
        return solution;
    }

}
