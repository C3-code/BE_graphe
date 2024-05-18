package org.insa.graphs.algorithm.shortestpath;
import org.insa.graphs.algorithm.AbstractSolution.Status;
import org.insa.graphs.algorithm.utils.BinaryHeap;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Graph;
import java.util.ArrayList;
import java.util.Collections;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {

    public DijkstraAlgorithm(ShortestPathData data) {
        super(data);
    }

    @Override
    protected ShortestPathSolution doRun() {
        final ShortestPathData data = getInputData();
        ShortestPathSolution solution = null;
        
        //plus court chemin d'une orgine vers tous les autres sommets
        //algo dijkstra considère les noeuds au cout le plus faible parmi ceux qui n'ont pas été marqué

        Graph graph = data.getGraph(); //recuperer le graphe
        int tailleGraphe = graph.size();
        //Recuperer le label du premier noeud
        Label debut = new Label(data.getOrigin());
        debut.setCost(0);
        //Declarer un binary heap pour stocker les labels des noeuds du graphe 
        BinaryHeap<Label> tasLabel = new BinaryHeap<Label>();
        //Creer un tableau de labels
        Label tabLabels[] = new Label[tailleGraphe];
        //List<Arc> chemin = new ArrayList<Arc>();
        

        tasLabel.insert(debut); //inserer l'origine dans le tas
        tabLabels[debut.getSommetCourant().getId()] = debut;

        boolean fin = false;
        while ( (!fin) && (!tasLabel.isEmpty()) ) {//tant qu'on n'a pas atteint la dest et que le tas n'est pas vide
        //Creation du label du noeud courant 
            Label labelCourant = tasLabel.deleteMin(); 
            labelCourant.setMarque();
            System.out.println(labelCourant.getCost());

            Node noeudCourant = labelCourant.getSommetCourant();

           /*  if (labelCourant.getPere() != null) { //si ce n'est pas  le premier noeud
                chemin.insert(labelCourant.getPere().successors)
            }*/

            notifyNodeMarked(noeudCourant); // afficher sur l'ecran que le noeud a ete marque  
            if (noeudCourant == data.getDestination()) { // si il sagit de la destination, on sort de la boucle 
                fin = true;
            }
            
            //Traitement des sommets successeurs
            for(Arc arc : noeudCourant.getSuccessors()) { 
                if (data.isAllowed(arc)){ //filtre verifiant que le chemin est accessible par le mode choisi
                    Node noeudSuccesseur = arc.getDestination();
                    Label labelSuccesseur;
    
                    if (tabLabels[arc.getDestination().getId()]==null){
                        labelSuccesseur = new Label (noeudSuccesseur);
                        tabLabels[arc.getDestination().getId()] = labelSuccesseur;
                    }
                    else {
                        labelSuccesseur = tabLabels[arc.getDestination().getId()];
                    }
    
                    //Calculer le nouveau cout 
                    double ancienCout = labelSuccesseur.getCost();
                    double nouveauCout = labelCourant.getCost() + data.getCost(arc);
    
                    if (!labelSuccesseur.getMarque() && (ancienCout==Double.POSITIVE_INFINITY || nouveauCout < ancienCout)) { //si le noeud n'est pas marque ou nouveau cout inferieur
                        //mettre a jour le cout 
                        labelSuccesseur.setCost(nouveauCout);
                        labelSuccesseur.setPere(arc);
                        //Si le noeud n'a jamais ete traite 
                        if (ancienCout != Double.POSITIVE_INFINITY) {
                            tasLabel.remove(labelSuccesseur);
                            
                        }
                        tasLabel.insert(labelSuccesseur);
                    }
                }
            }
        }

                // Destination has no predecessor, the solution is infeasible...
        if (tabLabels[data.getDestination().getId()] == null) {
            solution = new ShortestPathSolution(data, Status.INFEASIBLE);
        }
        else {

            // The destination has been found, notify the observers.
            notifyDestinationReached(data.getDestination());

            // Create the path from the array of predecessors...
            ArrayList<Arc> arcs = new ArrayList<>();
            Arc arc = tabLabels[data.getDestination().getId()].getPere();
            while (arc != null) {
                arcs.add(arc);
                arc = tabLabels[arc.getOrigin().getId()].getPere();
            }

            // Reverse the path...
            Collections.reverse(arcs);

            // Create the final solution.
            solution = new ShortestPathSolution(data, Status.OPTIMAL, new Path(graph, arcs));
        }


    
        //pour recreeer le plus court chemin, parcourir le tableau de labels à l'envers en suivant les paramètres "pere" de chacun jusqu'à arriver au départ  
        //pour creeer un chemin il faut un graphe et une liste d'arc => creer une liste d'arc dès le debut 
        


        
        //Stocker les labels des noeuds 
        return solution;
    }

}
