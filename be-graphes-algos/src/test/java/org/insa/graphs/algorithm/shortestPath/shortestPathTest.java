package org.insa.graphs.algorithm.shortestPath;
import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Random.*;
import java.lang.Math;
import javax.xml.datatype.Duration;
import org.insa.graphs.algorithm.ArcInspectorFactory;
import org.insa.graphs.algorithm.shortestpath.AStarAlgorithm;
import org.insa.graphs.algorithm.shortestpath.BellmanFordAlgorithm;
import org.insa.graphs.algorithm.shortestpath.DijkstraAlgorithm;
import org.insa.graphs.algorithm.shortestpath.ShortestPathData;
import org.insa.graphs.algorithm.shortestpath.ShortestPathSolution;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;
import org.insa.graphs.model.io.BinaryGraphReader;
import org.insa.graphs.model.io.GraphReader;
import org.junit.BeforeClass;
import org.junit.Test;


public class shortestPathTest {

    private static Graph graphShort;
    private static Graph graphLarge;
    private static Random random =  new Random();

    //Variables globales utilisees pour le scenarioB
    private boolean debut =  true;
    private int compteurIteration =  0;
    private List<Arc> arcs;
    

    @BeforeClass 
    public static void initAll() throws Exception {
        // Changez le chemin du fichier de carte pour les tests
        final String mapInsa = "/mnt/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/insa.mapgr";
        final String mapFrance = "/mnt/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/france.mapgr";
        final String mapMidiPyrenees = "/mnt/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/midi-pyrenees.mapgr";

        // Créez un lecteur des graphes.
        GraphReader readerInsa = new BinaryGraphReader(new DataInputStream(new BufferedInputStream(new FileInputStream(mapInsa))));
        GraphReader readerFrance = new BinaryGraphReader(new DataInputStream(new BufferedInputStream(new FileInputStream(mapFrance))));
        GraphReader readerMidiPyrenees = new BinaryGraphReader(new DataInputStream(new BufferedInputStream(new FileInputStream(mapMidiPyrenees))));

        // Lisez le graphe.
        graphShort = readerInsa.read();
        graphLarge = readerMidiPyrenees.read();
    }

    
    /*-------------SCENARIO A ------------------
    Tester la validite de Dijsktra et A* en comparaison avec l'algorithme de Bellman-Ford
    Prend en compte la non-validité d'un chemin (s'il n'existe pas de chemin entre l'origine et 
    la destination, on vérifie bien que A* et Dijkstra sont !isFeasible()).
    Fonctionne si l'on change le filtre des arcs (toutes les routes, tests en temps ou en distance)
     */
    private void testScenarioA(Graph graph, Node origin, Node destination) {

        ShortestPathData data = new ShortestPathData(graph, origin, destination, ArcInspectorFactory.getAllFilters().get(0)); //pour essayer Fastest: (get(2))
        
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(data);
        ShortestPathSolution solutionDijkstra = dijkstra.run();

        BellmanFordAlgorithm bellmanFord = new BellmanFordAlgorithm(data);
        ShortestPathSolution solutionBellmanFord = bellmanFord.run();

        AStarAlgorithm aStar = new AStarAlgorithm(data);
        ShortestPathSolution solutionAStar = aStar.run();

        if (solutionBellmanFord.isFeasible()){
            assertTrue(solutionBellmanFord.isFeasible());
            assertTrue(solutionDijkstra.isFeasible());
            Path pathBellmanFord = solutionBellmanFord.getPath();
            Path pathDijkstra = solutionDijkstra.getPath();
            assertNotNull(pathDijkstra);
            assertNotNull(pathBellmanFord);
            assertEquals(pathDijkstra.getLength(), pathBellmanFord.getLength(), 1e-6);
            assertEquals(pathDijkstra.getMinimumTravelTime(), pathBellmanFord.getMinimumTravelTime(), 1e-6);

            assertTrue(solutionAStar.isFeasible());
            Path pathAStar = solutionAStar.getPath();
            assertNotNull(pathAStar);
            assertEquals(pathAStar.getLength(), pathBellmanFord.getLength(), 1e-6);
            assertEquals(pathAStar.getMinimumTravelTime(), pathBellmanFord.getMinimumTravelTime(), 1e-6);
        }
        else {
            assertTrue(!(solutionDijkstra.isFeasible()));
            assertTrue(!(solutionAStar.isFeasible()));
            System.out.println("Pas de chemin existant.");
        }
    }


     /*--------SCENARIO B ----------------
     Tests prenant en compte la propriete suivante
     "Les sous-chemins des plus courts chemins sont des plus court chemin"
     */
    private void testScenarioB(Graph graph, Node origin, Node destination) {

        //boucle realisee uniquement lors de la premiere iteration, permet de determiner le plus court chemin avec Dijkstra 
        if (debut) { 
            ShortestPathData data = new ShortestPathData(graph, origin, destination, ArcInspectorFactory.getAllFilters().get(0));
            //On lance l'algorithme de Dijkstra sur une grande carte
            DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(data);
            ShortestPathSolution solutionDijkstra = dijkstra.run();
            if (solutionDijkstra.isFeasible()) { //On verifie qu'il existe un plus court chemin solution
                Path pathDijkstra = solutionDijkstra.getPath();
                arcs = pathDijkstra.getArcs();
            }
            debut =false;            
        }

        //On parcourt le plus court chemin trouve precedemment point par point 
        //Pour chaque portion de solution, on relance un algorithme de dijkstra pour verifier qu'on trouve le meme PCC
        //on utilise la reccursivite
        if (compteurIteration < arcs.size()) {
            Arc arcCourant = arcs.get(compteurIteration);
            ShortestPathData dataBis = new ShortestPathData(graph, arcCourant.getOrigin(), arcCourant.getDestination(), ArcInspectorFactory.getAllFilters().get(0));
            DijkstraAlgorithm dijkstraBis = new DijkstraAlgorithm(dataBis);
            ShortestPathSolution sousChemin = dijkstraBis.run();

            if (sousChemin.isFeasible()) {
                Path sousCheminPath = sousChemin.getPath();
                assertEquals(arcCourant.getLength(), sousCheminPath.getLength(), 1e-3);
                assertEquals(arcCourant.getMinimumTravelTime(), sousCheminPath.getMinimumTravelTime(), 1e-3);
                System.out.println("Les tests ont marches");
            }
            compteurIteration++;
            testScenarioB(graph, origin,destination);
        }
        compteurIteration =0; //on remet le compteur a 0 pour de prochains tests
    }
   

    /*--------SCENARIO C ----------------
    Comparer les performances entre A* et Dijkstra,
    en termes de temps d'execution
    */ 
    private void testScenarioC(Graph graph, Node origin, Node destination) {
        ShortestPathData data = new ShortestPathData(graph, origin, destination, ArcInspectorFactory.getAllFilters().get(0));
        
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(data);
        ShortestPathSolution solutionDijkstra = dijkstra.run();
             
        AStarAlgorithm aStar = new AStarAlgorithm(data);
        ShortestPathSolution solutionAStar = aStar.run();

        if (solutionDijkstra.isFeasible() && solutionAStar.isFeasible()){ //si il existe une solution, on peut comparer les performances
            Path pathDijkstra = solutionDijkstra.getPath(); 
            Path pathAStar = solutionAStar.getPath(); 
            assertEquals(pathDijkstra.getLength(), pathAStar.getLength(), 1e-6);
            assertEquals(pathDijkstra.getMinimumTravelTime(), pathAStar.getMinimumTravelTime(), 1e-6);
            assertTrue((solutionAStar.getSolvingTime().compareTo(solutionDijkstra.getSolvingTime()) < 0)); //nous verifions que le temps d'execution de A* est plus faible que le Dijkstra
            System.out.println("Temps execution Dijkstra : " + solutionDijkstra.getSolvingTime().getNano());
            System.out.println("Temps execution A* : " + solutionAStar.getSolvingTime().getNano());
        }
        else {
            assertTrue(!(solutionDijkstra.isFeasible()));
            assertTrue(!(solutionAStar.isFeasible()));
            System.out.println("Pas de chemin existant.");
        }
    }

    //Tests ScenarioA - Validite avec Bellman
    @Test
    public void testClassicPathRoad() {
        for (int i=0; i<50; i++) {
            testScenarioA(graphShort, graphShort.getNodes().get(random.nextInt(graphShort.getNodes().size())), graphShort.getNodes().get(random.nextInt(graphShort.getNodes().size()))); //trouver les coordoonnees
        }    
    }     
    
    @Test
    public void testSinglePathRoad() {
        for (int i=0; i<10; i++) {
            testScenarioA(graphShort, graphShort.getNodes().get(0), graphShort.getNodes().get(1));
        }
    } 

    //Tests ScenarioB - Validite sans Bellman
    @Test
    public void testB() {
        testScenarioB(graphShort, graphShort.getNodes().get(607), graphShort.getNodes().get(167));
        testScenarioB(graphShort, graphShort.getNodes().get(461), graphShort.getNodes().get(1026));
    }
 
    //Tests ScenarioC - Performance
    @Test
    public void testPerformance() { //on ne peut comparer la performance entre les algorithmes que si la distance entre deux points est suffisament elevee
        for (int i=0; i<30; i++) {
            testScenarioC(graphShort, graphShort.getNodes().get(1), graphShort.getNodes().get(1000));
            //testScenarioC(graphShort, graphShort.getNodes().get(607), graphShort.getNodes().get(101));
            testScenarioC(graphShort, graphShort.getNodes().get(461), graphShort.getNodes().get(1026));
        }   
    }    
}
