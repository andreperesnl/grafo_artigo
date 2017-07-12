package grafoshexed;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.*;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.SingleGraph;
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
    public static final Double AGUA = 1d;

    public static final Double DISTANCIA = 400d;
    public static final Double RAZAO_H = 1.15d;
    public static Viewer v;

    public static JLabel lbKm;
    public static JLabel lbTorres;
    public static JLabel lbTorresVertices;
    public static ConversorGrafo conversor;

    public static void main(String... args) {
        final JFrame frame = new JFrame("JHexed");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        Graph g = construirGrafo();

//        g.addAttribute("ui.stylesheet", "url('file:///fig.png')");
        Viewer view = g.display(true);
        //  v = view;
//        view.getDefaultView().setBackground(Color.red);
        System.out.println("OK");

        // frame.add(content, BorderLayout.CENTER);
        frame.pack();

        JButton bt = new JButton("Dijkstra");
        frame.add(bt);
        GridLayout gl = new GridLayout(5, 1);
        FlowLayout fl = new FlowLayout(1);
        frame.setLayout(gl);

        lbKm = new JLabel("Km:");
        lbTorres = new JLabel("Torres Simples:");
        lbTorresVertices = new JLabel("Torres Vértices:");

        frame.add(lbKm);
        frame.add(lbTorres);
        frame.add(lbTorresVertices);
        JCheckBox ckMap = new JCheckBox("Mostrar malha.");
        frame.add(ckMap);

        ckMap.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    g.setAttribute("ui.stylesheet", "node { fill-color: rgba(200,200,200,150); }");
                    g.setAttribute("ui.stylesheet", "edge { fill-color: rgba(200,200,200,150); }");
                } else {
                    g.setAttribute("ui.stylesheet", "node { fill-color: rgba(200,200,200,0); }");
                    g.setAttribute("ui.stylesheet", "edge { fill-color: rgba(200,200,200,0); }");
                }
            }
        });
        conversor = new ConversorGrafo();
        conversor.converter(g);
//        new ConversorGrafo().converter(g);
        frame.pack();
        frame.setVisible(true);

        bt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Path p = conversor.caminhoMinimo(g, g.getNode(getNodeNameByCoord(SOURCE_X, SOURCE_Y)), g.getNode(getNodeNameByCoord(SINK_X, SINK_Y)));

                java.util.List<String> nosEscolhidos = new ArrayList<>();
                for (Node n : p.getNodeSet()) {
                    nosEscolhidos.add(n.getAttribute("noOriginal"));
                    g.getEdge(n.getAttribute("arestaOriginal")).setAttribute("ui.style", "fill-color: red;");

                }
              

//
//                Dijkstra d = new Dijkstra(Dijkstra.Element.NODE, null, "weight");
//
//                d.init(g);
//                d.setSource(g.getNode(getNodeNameByCoord(SOURCE_X, SOURCE_Y)));
//
//                d.compute();
//          
//                java.util.List<Node> l = d.getPath(g.getNode(getNodeNameByCoord(SINK_X, SINK_Y))).getNodePath();
//                Iterator<Node> it = d.getPath(g.getNode(getNodeNameByCoord(SINK_X, SINK_Y))).getNodeIterator();
//                Iterator<Edge> et = d.getPath(g.getNode(getNodeNameByCoord(SINK_X, SINK_Y))).getEdgeIterator();
//
//                Double distancia = 0d;
//                Integer torres = 0;
//                Integer torresV = 0;
//                boolean ultimoEixo = true;
//                while (et.hasNext()) {
//                    Edge ed = et.next();
//                    ed.addAttribute("ui.style", " fill-color: red; stroke-width: 10; size: 2px; ");
//                    Double y1 = Double.valueOf(ed.getNode0().getAttribute("y").toString());
//                    Double y2 = Double.valueOf(ed.getNode1().getAttribute("y").toString());
//
////                    System.out.println("y1: " + ed.getNode0().getAttribute("y").toString() + " - " + ed.getNode1().getAttribute("y").toString());
//                    if (Math.abs(y1 - y2) < 0.01d) {
//                        distancia += DISTANCIA;
//                        if (ultimoEixo == false) {
//                            torresV++;
//                        } else {
//                            torres++;
//                        }
////                        System.out.println("Mesmo eixo");
//                        ultimoEixo = true;
//                    } else {
//                        distancia += (DISTANCIA * RAZAO_H);
//
//                        if (ultimoEixo) {
//                            torresV++;
//                        } else {
//                            torres++;
//                        }
//
////                        System.out.println("Diagonal");
//                        ultimoEixo = false;
//                    }
//
//                }
//                System.out.println("DISTANCIA TOTAL:" + distancia);
//                lbKm.setText("Distância:" + distancia);
//                lbTorres.setText("Torres:" + torres);
//                lbTorresVertices.setText("Torres Vértice:" + torresV);
//
//                int i = 0;
//                while (it.hasNext()) {
//                    i++;
//                    Node ed = it.next();
//
//                    int xx = ed.getAttribute("cx");
//                    int yy = ed.getAttribute("cy");
////                    ((DefaultIntegerHexModel) engine.getModel()).setValueAt(xx, yy, 2);
//
//                    ed.addAttribute("ui.style", "fill-color: red; size:6px; ");
//                }
//                System.out.println("TOTAL DE TORRES: " + i);
//
////                content.repaint();
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

                        Edge ed = g.addEdge("E" + i + "." + j + "-" + i + "." + (j - 1),
                                "V" + i + "." + j,
                                "V" + i + "." + (j - 1), true);
                        ed.setAttribute("weight", RAZAO_H);
                        ed.setAttribute("diagonal", 1);
                    } catch (Exception e) {
                    }
                    try {
                        Edge ed = g.addEdge("E" + i + "." + j + "-" + (i + 1) + "." + j,
                                "V" + i + "." + j,
                                "V" + (i + 1) + "." + j, true);
                        ed.setAttribute("diagonal", 0);
                    } catch (Exception e) {
                    }
                    try {
                        Edge ed = g.addEdge("E" + i + "." + j + "-" + i + "." + (j + 1),
                                "V" + i + "." + j,
                                "V" + i + "." + (j + 1), true);
                        ed.setAttribute("weight", RAZAO_H);
                        ed.setAttribute("diagonal", 1);
                    } catch (Exception e) {
                    }

                } else {
                    try {
                        Edge ed = g.addEdge("E" + i + "." + j + "-" + (i + 1) + "." + (j - 1),
                                "V" + i + "." + j,
                                "V" + (i + 1) + "." + (j - 1), true);
                        ed.setAttribute("weight", RAZAO_H);
                        ed.setAttribute("diagonal", 1);
                    } catch (Exception e) {
                    }
                    try {
                        Edge ed = g.addEdge("E" + i + "." + j + "-" + (i + 1) + "." + j,
                                "V" + i + "." + j,
                                "V" + (i + 1) + "." + j, true);
                        ed.setAttribute("diagonal", 0);
                    } catch (Exception e) {
                    }
                    try {
                        Edge ed = g.addEdge("E" + i + "." + j + "-" + (i + 1) + "." + (j + 1),
                                "V" + i + "." + j,
                                "V" + (i + 1) + "." + (j + 1), true);
                        ed.setAttribute("weight", RAZAO_H);
                        ed.setAttribute("diagonal", 1);
                    } catch (Exception e) {
                    }
                }

            }
        }

        inicarPonderacoes(g);
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

        ponderaLinha(g, 39, 50, 41, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 40, 50, 42, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 39, 50, 43, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 39, 50, 44, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 38, 49, 45, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 38, 49, 46, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 37, 49, 47, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 37, 49, 48, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 36, 49, 49, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 36, 49, 50, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 36, 49, 51, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 38, 49, 52, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 38, 48, 53, Double.POSITIVE_INFINITY, "infinity");

        ponderaLinha(g, 39, 48, 54, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 40, 48, 55, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 40, 48, 56, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 41, 48, 57, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 43, 45, 58, Double.POSITIVE_INFINITY, "infinity");

        /*
        Mandaguari
         */
        ponderaLinha(g, 77, 81, 41, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 70, 81, 42, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 67, 79, 43, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 67, 79, 44, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 67, 79, 45, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 67, 79, 46, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 67, 78, 47, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 67, 78, 48, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 67, 78, 48, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 67, 78, 49, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 67, 78, 50, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 67, 78, 51, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 67, 78, 52, Double.POSITIVE_INFINITY, "infinity");

        ponderaLinha(g, 67, 77, 53, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 67, 77, 54, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 67, 78, 55, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 68, 78, 56, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 68, 77, 57, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 68, 77, 58, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 68, 77, 59, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 68, 77, 60, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 68, 77, 61, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 68, 77, 62, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 68, 77, 63, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 68, 77, 64, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 68, 77, 65, Double.POSITIVE_INFINITY, "infinity");

        ponderaLinha(g, 70, 77, 66, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 71, 77, 67, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 74, 77, 68, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 75, 77, 69, Double.POSITIVE_INFINITY, "infinity");
        ponderaLinha(g, 77, 77, 70, Double.POSITIVE_INFINITY, "infinity");

        /*
        vegetacao apucarana
         */
        ponderaLinha(g, 107, 108, 39, VEGETACAO, "vegetacao");
        ponderaLinha(g, 107, 111, 40, VEGETACAO, "vegetacao");
        ponderaLinha(g, 106, 110, 41, VEGETACAO, "vegetacao");
        ponderaLinha(g, 105, 112, 42, VEGETACAO, "vegetacao");
        ponderaLinha(g, 105, 112, 43, VEGETACAO, "vegetacao");
        ponderaLinha(g, 104, 115, 44, VEGETACAO, "vegetacao");
        ponderaLinha(g, 104, 115, 45, VEGETACAO, "vegetacao");
        ponderaLinha(g, 104, 115, 46, VEGETACAO, "vegetacao");

        ponderaLinha(g, 104, 115, 46, VEGETACAO, "vegetacao");
        ponderaLinha(g, 104, 115, 47, VEGETACAO, "vegetacao");
        ponderaLinha(g, 104, 115, 48, VEGETACAO, "vegetacao");
        ponderaLinha(g, 104, 115, 49, VEGETACAO, "vegetacao");
        ponderaLinha(g, 104, 115, 50, VEGETACAO, "vegetacao");
        ponderaLinha(g, 104, 115, 51, VEGETACAO, "vegetacao");
        ponderaLinha(g, 106, 115, 52, VEGETACAO, "vegetacao");
        ponderaLinha(g, 108, 115, 53, VEGETACAO, "vegetacao");
        ponderaLinha(g, 108, 115, 54, VEGETACAO, "vegetacao");
        ponderaLinha(g, 108, 115, 55, VEGETACAO, "vegetacao");
        ponderaLinha(g, 108, 115, 56, VEGETACAO, "vegetacao");
        ponderaLinha(g, 108, 115, 57, VEGETACAO, "vegetacao");
        ponderaLinha(g, 108, 115, 58, VEGETACAO, "vegetacao");
        ponderaLinha(g, 108, 115, 59, VEGETACAO, "vegetacao");
        ponderaLinha(g, 108, 115, 60, VEGETACAO, "vegetacao");

        ponderaLinha(g, 108, 115, 61, VEGETACAO, "vegetacao");
        ponderaLinha(g, 108, 115, 62, VEGETACAO, "vegetacao");
        ponderaLinha(g, 108, 115, 63, VEGETACAO, "vegetacao");
        ponderaLinha(g, 108, 115, 64, VEGETACAO, "vegetacao");
        ponderaLinha(g, 110, 115, 65, VEGETACAO, "vegetacao");
        ponderaLinha(g, 110, 115, 66, VEGETACAO, "vegetacao");
        ponderaLinha(g, 110, 115, 67, VEGETACAO, "vegetacao");
        ponderaLinha(g, 110, 115, 68, VEGETACAO, "vegetacao");
        ponderaLinha(g, 108, 115, 69, VEGETACAO, "vegetacao");
        ponderaLinha(g, 108, 115, 70, VEGETACAO, "vegetacao");
        ponderaLinha(g, 108, 115, 71, VEGETACAO, "vegetacao");
        ponderaLinha(g, 108, 115, 72, VEGETACAO, "vegetacao");
        ponderaLinha(g, 108, 115, 73, VEGETACAO, "vegetacao");
        ponderaLinha(g, 109, 115, 74, VEGETACAO, "vegetacao");
        ponderaLinha(g, 109, 115, 75, VEGETACAO, "vegetacao");
        ponderaLinha(g, 110, 115, 76, VEGETACAO, "vegetacao");
        ponderaLinha(g, 110, 114, 77, VEGETACAO, "vegetacao");
        ponderaLinha(g, 111, 114, 78, VEGETACAO, "vegetacao");
        ponderaLinha(g, 111, 114, 79, VEGETACAO, "vegetacao");

        ponderaLinha(g, 85, 85, 13, AGUA, "agua");
        ponderaLinha(g, 85, 85, 14, AGUA, "agua");
        ponderaLinha(g, 85, 86, 15, AGUA, "agua");
        ponderaLinha(g, 85, 86, 16, AGUA, "agua");
        ponderaLinha(g, 85, 86, 17, AGUA, "agua");
        ponderaLinha(g, 87, 88, 18, AGUA, "agua");
        ponderaLinha(g, 87, 88, 19, AGUA, "agua");

        ponderaLinha(g, 87, 88, 20, AGUA, "agua");
        ponderaLinha(g, 87, 88, 21, AGUA, "agua");
        ponderaLinha(g, 88, 89, 22, AGUA, "agua");
        ponderaLinha(g, 89, 90, 23, AGUA, "agua");
        ponderaLinha(g, 89, 90, 24, AGUA, "agua");
        ponderaLinha(g, 89, 89, 25, AGUA, "agua");
        ponderaLinha(g, 89, 89, 26, AGUA, "agua");
        ponderaLinha(g, 89, 89, 27, AGUA, "agua");
        ponderaLinha(g, 89, 89, 28, AGUA, "agua");
        ponderaLinha(g, 89, 89, 29, AGUA, "agua");
        ponderaLinha(g, 89, 89, 30, AGUA, "agua");

        ponderaLinha(g, 89, 89, 30, AGUA, "agua");
        ponderaLinha(g, 88, 89, 31, AGUA, "agua");
        ponderaLinha(g, 89, 90, 32, AGUA, "agua");
        ponderaLinha(g, 89, 90, 33, AGUA, "agua");
        ponderaLinha(g, 90, 91, 34, AGUA, "agua");
        ponderaLinha(g, 89, 91, 35, AGUA, "agua");
        ponderaLinha(g, 90, 91, 36, AGUA, "agua");
        ponderaLinha(g, 91, 92, 37, AGUA, "agua");
        ponderaLinha(g, 92, 93, 38, AGUA, "agua");

        ponderaLinha(g, 92, 93, 39, AGUA, "agua");
        ponderaLinha(g, 93, 94, 40, AGUA, "agua");
        ponderaLinha(g, 93, 94, 41, AGUA, "agua");
        ponderaLinha(g, 94, 95, 42, AGUA, "agua");
        ponderaLinha(g, 94, 95, 43, AGUA, "agua");
        ponderaLinha(g, 94, 95, 44, AGUA, "agua");
        ponderaLinha(g, 95, 95, 45, AGUA, "agua");
        ponderaLinha(g, 95, 95, 46, AGUA, "agua");
        ponderaLinha(g, 95, 95, 47, AGUA, "agua");
        ponderaLinha(g, 95, 95, 48, AGUA, "agua");
        ponderaLinha(g, 95, 95, 49, AGUA, "agua");
        ponderaLinha(g, 95, 95, 50, AGUA, "agua");
        ponderaLinha(g, 95, 95, 51, AGUA, "agua");
        ponderaLinha(g, 95, 95, 52, AGUA, "agua");
        ponderaLinha(g, 94, 95, 53, AGUA, "agua");

        ponderaLinha(g, 94, 95, 54, AGUA, "agua");
        ponderaLinha(g, 94, 95, 55, AGUA, "agua");
        ponderaLinha(g, 94, 95, 56, AGUA, "agua");
        ponderaLinha(g, 93, 95, 57, AGUA, "agua");
        ponderaLinha(g, 93, 95, 58, AGUA, "agua");
        ponderaLinha(g, 93, 95, 59, AGUA, "agua");

        ponderaLinha(g, 94, 95, 60, AGUA, "agua");
        ponderaLinha(g, 94, 95, 61, AGUA, "agua");
        ponderaLinha(g, 95, 95, 62, AGUA, "agua");
        ponderaLinha(g, 95, 95, 63, AGUA, "agua");
        ponderaLinha(g, 95, 96, 64, AGUA, "agua");
        ponderaLinha(g, 96, 96, 65, AGUA, "agua");
        ponderaLinha(g, 97, 97, 66, AGUA, "agua");
        ponderaLinha(g, 97, 97, 67, AGUA, "agua");
        ponderaLinha(g, 97, 98, 68, AGUA, "agua");
        ponderaLinha(g, 97, 98, 69, AGUA, "agua");

        ponderaLinha(g, 98, 103, 70, AGUA, "agua");
        ponderaLinha(g, 98, 103, 71, AGUA, "agua");
        ponderaLinha(g, 101, 103, 72, AGUA, "agua");
        ponderaLinha(g, 101, 103, 73, AGUA, "agua");
        ponderaLinha(g, 103, 103, 74, AGUA, "agua");
        ponderaLinha(g, 103, 103, 75, AGUA, "agua");
        ponderaLinha(g, 103, 103, 76, AGUA, "agua");
        ponderaLinha(g, 103, 103, 77, AGUA, "agua");
        ponderaLinha(g, 103, 103, 78, AGUA, "agua");
        ponderaLinha(g, 103, 103, 79, AGUA, "agua");
        ponderaLinha(g, 103, 103, 80, AGUA, "agua");
        ponderaLinha(g, 103, 103, 81, AGUA, "agua");
        ponderaLinha(g, 103, 103, 82, AGUA, "agua");

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
