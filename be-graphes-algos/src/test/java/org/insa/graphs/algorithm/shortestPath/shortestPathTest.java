package org.insa.graphs.algorithm.shortestPath;
import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.insa.graphs.algorithm.ArcInspectorFactory;
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

    private static Graph graphRoad;
    private static Graph graphNonRoad;


    @BeforeClass
    public static void initAll() throws Exception {
        // Changez le chemin du fichier de carte pour les tests de carte routiere ou non
        final String mapRoadName = "/mnt/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/insa.mapgr";
        final String mapNonRoadName = "/mnt/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/carre.mapgr";
        // Créez un lecteur de graphe.
        GraphReader readerRoad = new BinaryGraphReader(new DataInputStream(new BufferedInputStream(new FileInputStream(mapRoadName))));
        GraphReader readerNonRoad = new BinaryGraphReader(new DataInputStream(new BufferedInputStream(new FileInputStream(mapNonRoadName))));
        // Lisez le graphe.
        graphRoad = readerRoad.read();
        graphNonRoad = readerNonRoad.read();

    }

    private void testScenario(Graph graph, Path expectedPath, Node origin, Node destination, boolean testBellmanFord) {
        ShortestPathData data = new ShortestPathData(graph, origin, destination, ArcInspectorFactory.getAllFilters().get(0));
        
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(data);
        ShortestPathSolution solutionDijkstra = dijkstra.run();
        
        if (expectedPath.isValid()) {
            assertTrue(solutionDijkstra.isFeasible());
            Path pathDijkstra = solutionDijkstra.getPath();
            assertNotNull(pathDijkstra);
            assertEquals(expectedPath.getLength(), pathDijkstra.getLength(), 1e-6);
            assertEquals(expectedPath.getMinimumTravelTime(), pathDijkstra.getMinimumTravelTime(), 1e-6);
            
            if (testBellmanFord) {
                BellmanFordAlgorithm bellmanFord = new BellmanFordAlgorithm(data);
                ShortestPathSolution solutionBellmanFord = bellmanFord.run();
                assertTrue(solutionBellmanFord.isFeasible());
                Path pathBellmanFord = solutionBellmanFord.getPath();
                assertNotNull(pathBellmanFord);
                assertEquals(pathDijkstra.getLength(), pathBellmanFord.getLength(), 1e-6);
                assertEquals(pathDijkstra.getMinimumTravelTime(), pathBellmanFord.getMinimumTravelTime(), 1e-6);
            }
        } else {
            assertFalse(solutionDijkstra.isFeasible());
        }
    }

    @Test
    public void testEmptyPathRoad() {
        testScenario(graphRoad, new Path(graphRoad, new ArrayList<Arc>()), graphRoad.getNodes().get(0), graphRoad.getNodes().get(0), true);
    }
    
    @Test
    public void testSingleNodePathRoad() {
        testScenario(graphRoad, new Path(graphRoad, graphRoad.getNodes().get(1)), graphRoad.getNodes().get(1), graphRoad.getNodes().get(1), true);
    }

    @Test
    public void testShortPathRoad() {
        Node[] nodes = graphRoad.getNodes().toArray(new Node[0]);
        Arc a2b = nodes[0].getSuccessors().get(0);
        Arc b2c = nodes[1].getSuccessors().get(0);
        Arc c2d_1 = nodes[2].getSuccessors().get(0);
        testScenario(graphRoad, new Path(graphRoad, Arrays.asList(a2b, b2c, c2d_1)), nodes[0], nodes[3], true);
    }

    @Test
    public void testLongPathRoad() {
        Node[] nodes = graphRoad.getNodes().toArray(new Node[0]);
        Arc a2b = nodes[0].getSuccessors().get(0);
        Arc b2c = nodes[1].getSuccessors().get(0);
        Arc c2d_1 = nodes[2].getSuccessors().get(0);
        Arc d2e = nodes[3].getSuccessors().get(0);
        testScenario(graphRoad, new Path(graphRoad, Arrays.asList(a2b, b2c, c2d_1, d2e)), nodes[0], nodes[4], false);
    }

    @Test
    public void testInvalidPathRoad() {
        Node[] nodes = graphRoad.getNodes().toArray(new Node[0]);
        Arc a2b = nodes[0].getSuccessors().get(0);
        Arc c2d_1 = nodes[2].getSuccessors().get(0);
        Arc d2e = nodes[3].getSuccessors().get(0);
        testScenario(graphRoad, new Path(graphRoad, Arrays.asList(a2b, c2d_1, d2e)), nodes[0], nodes[4], true);
    }

    @Test
    public void testEmptyPathNonRoad() {
        testScenario(graphNonRoad, new Path(graphNonRoad, new ArrayList<Arc>()), graphNonRoad.getNodes().get(0), graphNonRoad.getNodes().get(0), true);
    }

    @Test
    public void testSingleNodePathNonRoad() {
        testScenario(graphNonRoad, new Path(graphNonRoad, graphNonRoad.getNodes().get(1)), graphNonRoad.getNodes().get(1), graphNonRoad.getNodes().get(1), true);
    }

    @Test
    public void testShortPathNonRoad() {
        Node[] nodes = graphNonRoad.getNodes().toArray(new Node[0]);
        Arc a2b = nodes[0].getSuccessors().get(0);
        Arc b2c = nodes[1].getSuccessors().get(0);
        Arc c2d_1 = nodes[2].getSuccessors().get(0);
        testScenario(graphNonRoad, new Path(graphNonRoad, Arrays.asList(a2b, b2c, c2d_1)), nodes[0], nodes[3], true);
    }

    @Test
    public void testLongPathNonRoad() {
        Node[] nodes = graphNonRoad.getNodes().toArray(new Node[0]);
        Arc a2b = nodes[0].getSuccessors().get(0);
        Arc b2c = nodes[1].getSuccessors().get(0);
        Arc c2d_1 = nodes[2].getSuccessors().get(0);
        Arc d2e = nodes[3].getSuccessors().get(0);
        testScenario(graphNonRoad, new Path(graphNonRoad, Arrays.asList(a2b, b2c, c2d_1, d2e)), nodes[0],nodes[4],false);
    }

    @Test
    public void testInvalidPathNonRoad() {
        Node[] nodes = graphNonRoad.getNodes().toArray(new Node[0]);
        Arc a2b = nodes[0].getSuccessors().get(0);
        Arc c2d_1 = nodes[2].getSuccessors().get(0);
        Arc d2e = nodes[3].getSuccessors().get(0);
        testScenario(graphNonRoad, new Path(graphNonRoad, Arrays.asList(a2b, c2d_1, d2e)), nodes[0], nodes[4], true);
    }


}
