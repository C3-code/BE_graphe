package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.algorithm.utils.BinaryHeap;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Graph;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {

    public DijkstraAlgorithm(ShortestPathData data) {
        super(data);
    }

    @Override
    protected ShortestPathSolution doRun() {
        final ShortestPathData data = getInputData();
        ShortestPathSolution solution = null;
        // TODO:
        //plus court chemin d'une orgine vers tous les autres sommets
        //algo dijkstra considère les noeuds au cout le plus faible parmi ceux qui n'ont pas été marqué

        Graph graph = data.getGraph(); //recuperer le graphe
        int tailleGraphe = graph.size();
        //Recuperer le label du premier noeud
        Label debut = new Label(data.getOrigin());
        //Declarer un binary heap pour stocker les labels des noeuds du graphe 
        BinaryHeap<Label> tasLabel = new BinaryHeap<Label>();
        //Creer un tableau de labels
        Label tabLabels[] = new Label[tailleGraphe];
        
        tasLabel.insert(debut); //inserer l'origine dans le tas
        tabLabels[debut.getSommetCourant().getId()] = debut;

        boolean fin = false;
        while ( (!fin) && (!tasLabel.isEmpty()) ) {//tant qu'on n'a pas atteint la dest et que le tas n'est pas vide
        //Creation du label du noeud courant 
            Label labelCourant = tasLabel.deleteMin(); 
            labelCourant.setMarque();

            Node noeudCourant = labelCourant.getSommetCourant();

            notifyNodeMarked(noeudCourant); // afficher sur l'ecran que le marquage du noeud 
            if (noeudCourant == data.getDestination()) { // si il sagit de la destination, on sort de la boucle 
                fin = true;
            }
            
            //Traitement des sommets successeurs
            for(Arc arc : noeudCourant.getSuccessors()) { 
                Node noeudSuccesseur = arc.getDestination();
                Label labelSuccesseur = new Label (noeudSuccesseur);

                //Calculer le nouveau cout 
                double ancienCout = labelSuccesseur.getCost();
                double nouveauCout = labelCourant.getCost() + data.getCost(arc);

                if (!labelSuccesseur.getMarque() && (ancienCout==Double.POSITIVE_INFINITY || nouveauCout < ancienCout)) {
                    //mettre a jour le cout 
                    labelSuccesseur.setCost(nouveauCout);
                    labelSuccesseur.setPere(noeudCourant);
                    tabLabels[labelSuccesseur.getSommetCourant().getId()] = labelSuccesseur;//insertion du label dans le tableau de labels

                    //Si le noeud n'a jamais ete traite 
                    if (ancienCout == Double.POSITIVE_INFINITY) {
                        tasLabel.insert(labelSuccesseur);
                    }
                    else {
                        tasLabel.remove(labelSuccesseur);
                        tasLabel.insert(labelSuccesseur);
                    }
                    
                }

            }
            

        }
        //solution = new ShortestPathSolution(data, Status.OPTIMAL, new Path(graph, arcs));
        //pour recreeer le plus court chemin, parcourir le tableau de labels à l'envers en suivant les paramètres "pere" de chacun jusqu'à arriver au départ  

        
        //Stocker les labels des noeuds 
        return solution;
    }

}
