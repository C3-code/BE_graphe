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
    /*Capsule temporelle: il faut implémenter les tests avec junits avec @before class et @Test (s'inspirer des tests
    realises dans la classe PathTEST) en 
    1) decrivant l'environnement => creation de graphes et de maps que  l'on veut tester 
        pour cela, reprendre le fonctionnement utilise dans le fichier Launch.java (tout sauf la dernière ligne
        qui permet de faire l'affichage. Car nous n'avons pas besoin de l'affichage)
    2) faire les test avec la fonction assert() issue de la bib junit */

    private static Graph graphShort;
    private static Graph graphLarge;
    private static Random random =  new Random();
    private boolean debut =  true;
    private int compteurIteration =  0;
    //private Path pathDijkstraR;
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


    private void testScenarioA(Graph graph, Node origin, Node destination) {

        ShortestPathData data = new ShortestPathData(graph, origin, destination, ArcInspectorFactory.getAllFilters().get(0)); //mode a changer pour essayer Fastest (get(2))
        
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

    private void testScenarioB(Graph graph, Node origin, Node destination) {

        if (debut) { //le 
            ShortestPathData data = new ShortestPathData(graph, origin, destination, ArcInspectorFactory.getAllFilters().get(0));
            //On lance l'algorithme de Dijkstra sur une grande carte
            DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(data);
            ShortestPathSolution solutionDijkstra = dijkstra.run();
            Path pathDijkstra = solutionDijkstra.getPath();
            arcs = pathDijkstra.getArcs();
    
            //On verifie qu'il existe un plus court chemin solution
            assertTrue(solutionDijkstra.isFeasible());
            debut =false;
        }
        //On parcourt le plus court chemin trouve point par point 
        //Pour chaque portion de solution, on relance un algorithme de dijkstra pour verifier qu'on trouve le meme PCC
        //on utilise la reccursivite
        if (compteurIteration < arcs.size()) {
                ShortestPathData dataBis = new ShortestPathData(graph, arcs.get(compteurIteration).getOrigin(), arcs.get(compteurIteration).getDestination(), ArcInspectorFactory.getAllFilters().get(0));
                DijkstraAlgorithm dijkstraBis = new DijkstraAlgorithm(dataBis);
                ShortestPathSolution sousChemin = dijkstraBis.run();
                Path sousCheminPath = sousChemin.getPath();

                assertEquals(arcs.get(compteurIteration).getLength(), sousCheminPath.getLength(), 1e-6);
                assertEquals(arcs.get(compteurIteration).getMinimumTravelTime(), sousCheminPath.getMinimumTravelTime(), 1e-6);
                compteurIteration++;
                testScenarioB(graph, origin,destination);
        }
        
            /* version iterative
            boolean correct = true;
            boolean fin = false;
            while (correct && !fin) {
            for (int i =0; i< pathDijkstra.size()-1; i++) {
                ShortestPathData dataBis = new ShortestPathData(graph, graphShort.getNodes().get(i), graphShort.getNodes().get(i+1), ArcInspectorFactory.getAllFilters().get(0));
                DijkstraAlgorithm dijkstraBis = new DijkstraAlgorithm(dataBis);
                ShortestPathSolution sousChemin = dijkstraBis.run();
                if () {

                }
            }
            fin = true; */
        
        /* 
        Path pathBellmanFord = solutionBellmanFord.getPath();
        Path pathDijkstra = solutionDijkstra.getPath();
        assertNotNull(pathDijkstra);
        assertNotNull(pathBellmanFord);
        assertEquals(pathDijkstra.getLength(), pathBellmanFord.getLength(), 1e-6);
        assertEquals(pathDijkstra.getMinimumTravelTime(), pathBellmanFord.getMinimumTravelTime(), 1e-6); */ 
    }
   

    /*--------SCENARIO C ----------------
    Comparer les performances entre A* et Dijkstra 
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

    
    /*--------SCENARIO A ----------------
    Tester la validite de Dijsktra et A* en comparaison avec l'algorithme de Bellman-Ford
    Prend en compte la non-validité d'un chemin (s'il n'existe pas de chemin entre l'origine et la destination, on vérifie bien que A* et Dijkstra sont !isFeasible()).
    Fonctionne si l'on change le filtre des arcs (toutes les routes, tests en temps ou en distance)
     */ /* 
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
    } */

    //il faudra verifier que l'algorithme s'arrete si il n'existe pas de chemin 
    /*--------SCENARIO B ----------------solutionAStar.getSolvingTime().getNano()
     Tests prenant en compte la propriete
     "Les sous-chemins des plus courts chemins sont des plus court chemin"
     */ /* 
    @Test
    public void testEmptyPathRoad() { 
       testScenarioB(graphShort, graphShort.getNodes().get(0), graphShort.getNodes().get(0));
    } 

    @Test
    public void testClassicLargePath() {
        for (int i=0; i < 2; i++) {
            testScenarioB(graphLarge, graphLarge.getNodes().get(random.nextInt(graphLarge.getNodes().size())), graphLarge.getNodes().get(random.nextInt(graphLarge.getNodes().size()))); //trouver les coordoonnees
        }  
    }   

    
    */

     
    

    //comparaison en temps d'execution             testScenarioC(graphLarge, graphLarge.getNodes().get(random.nextInt(graphLarge.getNodes().size())), graphLarge.getNodes().get(random.nextInt(graphLarge.getNodes().size()))); 
    /* 
    @Test
    public void testPerformance() { //on ne peut comparer la performance entre les algorithmes que si la distance entre deux points est suffisament elevee
        for (int i=0; i<50; i++) {
            testScenarioC(graphShort, graphShort.getNodes().get(1), graphShort.getNodes().get(1000));
            testScenarioC(graphShort, graphShort.getNodes().get(607), graphShort.getNodes().get(167));
            testScenarioC(graphShort, graphShort.getNodes().get(461), graphShort.getNodes().get(1026));
        }
            //testScenarioC(graphLarge, graphLarge.getNodes().get(random.nextInt(graphLarge.getNodes().size())), graphLarge.getNodes().get(random.nextInt(graphLarge.getNodes().size()))); 
            //testScenarioC(graphShort, graphShort.getNodes().get(random.nextInt(graphShort.getNodes().size())), graphShort.getNodes().get(random.nextInt(graphShort.getNodes().size())));
            //testScenarioC(graphShort, graphShort.getNodes().get(1000), graphShort.getNodes().get(1100));
          
    }    */

    @Test
    public void testB() {
        testScenarioB(graphShort, graphShort.getNodes().get(1), graphShort.getNodes().get(1000));
    }
    
    /*
    else {
            //assertEquals(1,0);
            System.out.println("fffffff");
        }
    @Test
    public void testShortPathRoad() {
        Node[] nodes = graphShort.getNodes().toArray(new Node[0]);
        Arc a2b = nodes[0].getSuccessors().get(0);
        Arc b2c = nodes[1].getSuccessors().get(0);
        Arc c2d_1 = nodes[2].getSuccessors().get(0);
        testScenario(graphShort, new Path(graphShort, Arrays.asList(a2b, b2c, c2d_1)), nodes[0], nodes[3], true);
    }



    */
}
