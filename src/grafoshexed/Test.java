package grafoshexed;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSinkDGS;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.swingViewer.LayerRenderer;
import org.graphstream.ui.swingViewer.util.DefaultCamera;
import org.graphstream.ui.view.Viewer;

public class Test {

    public static final int NUM_COLS = 133;
    public static final int NUM_ROWS = 90;

    public static final int SOURCE_X = 8;
    public static final int SOURCE_Y = 53;

    public static final int SINK_X = 123;
    public static final int SINK_Y = 53;

    public static final Double RATE_Y = 0.577d;
    public static final Double VEGETACAO = 5d;
    
    
    public static final Double DISTANCIA = 400d;
    public static final Double RAZAO_H = 1.15d;
    

    public static Viewer v;

    public static void main(String... args) {
        final JFrame frame = new JFrame("JHexed");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        Graph g = construirGrafo();

//        g.addAttribute("ui.stylesheet", "url('file:///fig.png')");
        Viewer view = g.display(true);
        v = view;
//        view.getDefaultView().setBackground(Color.red);

        System.out.println("OK");

        // frame.add(content, BorderLayout.CENTER);
        frame.pack();

        JButton bt = new JButton("Dijkstra");
        frame.add(bt);
        FlowLayout fl = new FlowLayout(1);
        frame.setLayout(fl);

        JButton bt2 = new JButton("zoom");
        frame.add(bt2);

        bt2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                v.getDefaultView().getCamera().setViewPercent(0.8d);

            }
        });

        frame.pack();
        frame.setVisible(true);

        bt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Dijkstra d = new Dijkstra(Dijkstra.Element.NODE, null, "weight");

                d.init(g);
                d.setSource(g.getNode(getNodeNameByCoord(SOURCE_X, SOURCE_Y)));

                d.compute();
//          
                java.util.List<Node> l = d.getPath(g.getNode(getNodeNameByCoord(SINK_X, SINK_Y))).getNodePath();
                Iterator<Node> it = d.getPath(g.getNode(getNodeNameByCoord(SINK_X, SINK_Y))).getNodeIterator();
                Iterator<Edge> et = d.getPath(g.getNode(getNodeNameByCoord(SINK_X, SINK_Y))).getEdgeIterator();

                Double distancia = 0d;
                while (et.hasNext()) {
                    Edge ed = et.next();
                    ed.addAttribute("ui.style", " fill-color: red; stroke-width: 10; size: 2px; ");
                    Double y1 =  Double.valueOf(ed.getNode0().getAttribute("y").toString());
                    Double y2 =  Double.valueOf(ed.getNode1().getAttribute("y").toString());
                    
                    System.out.println("y1: "+ed.getNode0().getAttribute("y").toString()+" - "+ed.getNode1().getAttribute("y").toString());
                    if( Math.abs(y1-y2) < 0.01d){
                        distancia += DISTANCIA;
                        System.out.println("Mesmo eixo");
                    }else{
                        distancia += (DISTANCIA*RAZAO_H);
                        System.out.println("Diagonal");
                    }
                        
                }
                System.out.println("DISTANCIA TOTAL:"+distancia);

                int i = 0;
                while (it.hasNext()) {
                    i++;
                    Node ed = it.next();

                    int xx = ed.getAttribute("cx");
                    int yy = ed.getAttribute("cy");
//                    ((DefaultIntegerHexModel) engine.getModel()).setValueAt(xx, yy, 2);

                    ed.addAttribute("ui.style", "fill-color: red; size:6px; ");
                }
                System.out.println("TOTAL DE TORRES: " + i);

//                content.repaint();
            }
        });
    }

    private static String getNodeNameByCoord(int x, int y) {
        return "V" + x + "." + y;
    }

    private static Node getNodeByCoord(Graph g, int x, int y) {
        return g.getNode(getNodeNameByCoord(x, y));
    }

    private static String getEdgeNameByCoord(int vx, int vy, int wx, int wy) {
        return "E" + vx + "." + vy + "-" + wx + "." + wy;
    }

    public static Graph construirGrafo() {
        Integer numColunas = NUM_COLS;
        Integer numLinhas = NUM_ROWS;

        System.out.println("coddlunas:" + numColunas);
        System.out.println("Lindddhas:" + numLinhas);
        Graph g = new SingleGraph("Teste");
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        g.addAttribute("ui.stylesheet", "node { fill-color:blue;} ");

        g.addAttribute("ui.stylesheet", "graph { fill-mode:  image-scaled-ratio-max; "
                + "fill-image: url('img/fig.png'); "
                + "} ");

        //criar os nós 
        g.setAttribute("ui.stylesheet", "url(img/c.css)");
        for (int j = 0; j < numLinhas; j++) {
            for (int i = 0; i < numColunas; i++) {

                g.addNode("V" + i + "." + j);

                // g.getNode("V" + i + "." + j).addAttribute("ui.style", "size: 5px; ");
//                g.getNode("V" + i + "." + j).addAttribute("ui.style", " fill-mode: image-scaled; fill-image:  url('file:///fig.png');  ");
                g.getNode("V" + i + "." + j).setAttribute("layout.frozen");
                g.getNode("V" + i + "." + j).setAttribute("ui.frozen");

                // g.getNode("V" + i + "." + j).setAttribute("label", "V" + i + "." + j);
                if (j % 2 == 0) {
                    g.getNode("V" + i + "." + j).setAttribute("x", i - (NUM_COLS / 2));

//                    System.out.println("opss:"+g.getNode("V" + i + "." + j).getAttribute("x"));
                    g.getNode("V" + i + "." + j).setAttribute("y", (NUM_ROWS / 2 - j) * RATE_Y);
                } else {
                    g.getNode("V" + i + "." + j).setAttribute("x", ((i + 0.5) - NUM_COLS / 2));
                    g.getNode("V" + i + "." + j).setAttribute("y", (NUM_ROWS / 2 - j) * RATE_Y);
                }
                g.getNode("V" + i + "." + j).setAttribute("cx", i);
                g.getNode("V" + i + "." + j).setAttribute("cy", j);

                g.getNode("V" + i + "." + j).setAttribute("weight", 1);

            }
        }

        //criar as vizinhaças
        for (int j = 0; j < numLinhas; j++) {
            for (int i = 0; i < numColunas; i++) {
                if (j % 2 == 0) {

                    try {
                        g.addEdge("E" + i + "." + j + "-" + i + "." + (j - 1),
                                "V" + i + "." + j,
                                "V" + i + "." + (j - 1), true);
                    } catch (Exception e) {
                    }
                    try {
                        g.addEdge("E" + i + "." + j + "-" + (i + 1) + "." + j,
                                "V" + i + "." + j,
                                "V" + (i + 1) + "." + j, true);
                    } catch (Exception e) {
                    }
                    try {
                        g.addEdge("E" + i + "." + j + "-" + i + "." + (j + 1),
                                "V" + i + "." + j,
                                "V" + i + "." + (j + 1), true);
                    } catch (Exception e) {
                    }

                } else {
                    try {
                        g.addEdge("E" + i + "." + j + "-" + (i + 1) + "." + (j - 1),
                                "V" + i + "." + j,
                                "V" + (i + 1) + "." + (j - 1), true);
                    } catch (Exception e) {
                    }
                    try {
                        g.addEdge("E" + i + "." + j + "-" + (i + 1) + "." + j,
                                "V" + i + "." + j,
                                "V" + (i + 1) + "." + j, true);
                    } catch (Exception e) {
                    }
                    try {
                        g.addEdge("E" + i + "." + j + "-" + (i + 1) + "." + (j + 1),
                                "V" + i + "." + j,
                                "V" + (i + 1) + "." + (j + 1), true);
                    } catch (Exception e) {
                    }
                }

            }
        }

        inicarPonderacoes(g);

        for (Edge e : getNodeByCoord(g, 16, 24).getEdgeSet()) {
            e.setAttribute("ui.style", "fill-color:red;");
        }

        for (Node n : g.getNodeSet()) {
            if (Double.valueOf(n.getAttribute("weight").toString()) >= Double.POSITIVE_INFINITY) {
                n.setAttribute("ui.style", "fill-color: blue;");
                System.out.println("Setando classe->" + n.toString());
                n.setAttribute("ui.class", "infinity");
            } else {
                if (Double.valueOf(n.getAttribute("weight").toString()) == VEGETACAO) {
                    n.setAttribute("ui.class", "vegetacao");
                    n.setAttribute("ui.style", "fill-color: green;");
                }
            }
        }

        return g;
    }

    private static void inicarPonderacoes(Graph g) {
        /*
        Marialva SINK 53
         */

        ponderaLinha(g, 40, 50, 41, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 40, 50, 42, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 39, 50, 43, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 39, 50, 44, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 38, 49, 45, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 38, 49, 46, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 37, 49, 47, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 37, 49, 48, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 36, 49, 49, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 36, 49, 50, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 36, 49, 51, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 38, 49, 52, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 38, 48, 53, Double.POSITIVE_INFINITY,"infinity");

        ponderaLinha(g, 39, 48, 54, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 40, 48, 55, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 40, 48, 56, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 41, 48, 57, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 43, 45, 58, Double.POSITIVE_INFINITY,"infinity");

        /*
        Mandaguari
         */
        ponderaLinha(g, 77, 81, 41, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 70, 81, 42, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 67, 79, 43, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 67, 79, 44, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 67, 79, 45, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 67, 79, 46, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 67, 78, 47, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 67, 78, 48, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 67, 78, 48, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 67, 78, 49, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 67, 78, 50, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 67, 78, 51, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 67, 78, 52, Double.POSITIVE_INFINITY,"infinity");

        ponderaLinha(g, 67, 77, 53, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 67, 77, 54, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 67, 78, 55, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 68, 78, 56, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 68, 77, 57, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 68, 77, 58, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 68, 77, 59, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 68, 77, 60, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 68, 77, 61, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 68, 77, 62, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 68, 77, 63, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 68, 77, 64, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 68, 77, 65, Double.POSITIVE_INFINITY,"infinity");

        ponderaLinha(g, 70, 77, 66, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 71, 77, 67, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 74, 77, 68, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 75, 77, 69, Double.POSITIVE_INFINITY,"infinity");
        ponderaLinha(g, 77, 77, 70, Double.POSITIVE_INFINITY,"infinity");

        /*
        vegetacao apucarana
         */
        ponderaLinha(g, 108, 108, 39, VEGETACAO,"vegetacao");
        ponderaLinha(g, 108, 110, 40, VEGETACAO,"vegetacao");
        ponderaLinha(g, 106, 110, 41, VEGETACAO,"vegetacao");
        ponderaLinha(g, 105, 112, 42, VEGETACAO,"vegetacao");
        ponderaLinha(g, 105, 112, 43, VEGETACAO,"vegetacao");
        ponderaLinha(g, 104, 115, 44, VEGETACAO,"vegetacao");
        ponderaLinha(g, 104, 115, 45, VEGETACAO,"vegetacao");
        ponderaLinha(g, 104, 115, 46, VEGETACAO,"vegetacao");
        
        ponderaLinha(g, 104, 115, 46, VEGETACAO,"vegetacao");
        ponderaLinha(g, 104, 115, 47, VEGETACAO,"vegetacao");
        ponderaLinha(g, 104, 115, 48, VEGETACAO,"vegetacao");
        ponderaLinha(g, 104, 115, 49, VEGETACAO,"vegetacao");
        ponderaLinha(g, 104, 115, 50, VEGETACAO,"vegetacao");
        ponderaLinha(g, 104, 115, 51, VEGETACAO,"vegetacao");
        ponderaLinha(g, 106, 115, 52, VEGETACAO,"vegetacao");
        ponderaLinha(g, 108, 115, 53, VEGETACAO,"vegetacao");
        ponderaLinha(g, 108, 115, 54, VEGETACAO,"vegetacao");
        ponderaLinha(g, 108, 115, 55, VEGETACAO,"vegetacao");
        ponderaLinha(g, 108, 115, 56, VEGETACAO,"vegetacao");
        ponderaLinha(g, 108, 115, 57, VEGETACAO,"vegetacao");
        ponderaLinha(g, 108, 115, 58, VEGETACAO,"vegetacao");
        ponderaLinha(g, 108, 115, 59, VEGETACAO,"vegetacao");
        ponderaLinha(g, 108, 115, 60, VEGETACAO,"vegetacao");
        
        ponderaLinha(g, 108, 115, 61, VEGETACAO,"vegetacao");
        ponderaLinha(g, 108, 115, 62, VEGETACAO,"vegetacao");
        ponderaLinha(g, 108, 115, 63, VEGETACAO,"vegetacao");
        ponderaLinha(g, 108, 115, 64, VEGETACAO,"vegetacao");
        ponderaLinha(g, 110, 115, 65, VEGETACAO,"vegetacao");
        ponderaLinha(g, 110, 115, 66, VEGETACAO,"vegetacao");
        ponderaLinha(g, 110, 115, 67, VEGETACAO,"vegetacao");
        ponderaLinha(g, 110, 115, 68, VEGETACAO,"vegetacao");
        ponderaLinha(g, 108, 115, 69, VEGETACAO,"vegetacao");
        ponderaLinha(g, 108, 115, 70, VEGETACAO,"vegetacao");
        ponderaLinha(g, 108, 115, 71, VEGETACAO,"vegetacao");
        ponderaLinha(g, 108, 115, 72, VEGETACAO,"vegetacao");
        ponderaLinha(g, 108, 115, 73, VEGETACAO,"vegetacao");
        ponderaLinha(g, 109, 115, 74, VEGETACAO,"vegetacao");
        ponderaLinha(g, 109, 115, 75, VEGETACAO,"vegetacao");
        ponderaLinha(g, 110, 115, 76, VEGETACAO,"vegetacao");
        ponderaLinha(g, 110, 115, 77, VEGETACAO,"vegetacao");
        ponderaLinha(g, 111, 115, 78, VEGETACAO,"vegetacao");
        ponderaLinha(g, 111, 115, 79, VEGETACAO,"vegetacao");
    }

    public static void ponderaLinha(Graph g, int x1, int x2, int y, double fator, String classe) {
        for (int i = x1; i <= x2; i++) {
            Node n = getNodeByCoord(g, i, y);
            Double peso = Double.valueOf(n.getAttribute("weight").toString());
            n.setAttribute("weight", peso * fator);
            n.setAttribute("ui.class", classe);
        }
    }
}
