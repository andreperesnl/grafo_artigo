package grafoshexed;

import com.igormaznitsa.jhexed.engine.*;
import com.igormaznitsa.jhexed.engine.misc.HexPosition;
import com.igormaznitsa.jhexed.engine.misc.HexRect2D;
import com.igormaznitsa.jhexed.renders.swing.ColorHexRender;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import javax.swing.*;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

public class Test {

    public static final int NUM_COLS = 30;
    public static final int NUM_ROWS = 30;
    public static final Double RATE_Y = 0.577d;

    public static void main(String... args) {
        final JFrame frame = new JFrame("JHexed");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        final HexEngine<Graphics2D> engine = new HexEngine<Graphics2D>(20, 20, HexEngine.ORIENTATION_VERTICAL);
        engine.setModel(new DefaultIntegerHexModel(NUM_COLS, NUM_ROWS, -1));

        final Color[] ALLOWEDCOLORS = new Color[]{Color.white, Color.orange, Color.blue, Color.red, Color.green, Color.magenta, Color.yellow, Color.GRAY};

        engine.setRenderer(new ColorHexRender() {

            @Override
            public Color getFillColor(HexEngineModel<?> model, int col, int row) {
                final DefaultIntegerHexModel intmodel = (DefaultIntegerHexModel) model;
                return ALLOWEDCOLORS[intmodel.getValueAt(col, row) % ALLOWEDCOLORS.length];
            }

        });

        final JComponent content = new JComponent() {

            @Override
            protected void paintComponent(Graphics g) {
                engine.draw((Graphics2D) g);
            }

            @Override
            public Dimension getPreferredSize() {
                final HexRect2D rect = engine.getVisibleSize();
                return new Dimension(rect.getWidthAsInt(), rect.getHeightAsInt());
            }
        };

        content.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(e.getX());
                final HexPosition position = engine.pointToHex(e.getX(), e.getY());
                System.out.println(position);
                if (engine.getModel().isPositionValid(position)) {
                    final DefaultIntegerHexModel model = (DefaultIntegerHexModel) engine.getModel();
                    Integer value = model.getValueAt(position);
                    if (value > 7) {
                        value = 0;
                    } else {
                        value++;
                    }
                    model.setValueAt(position, value);
                }
                content.repaint();
            }

        });

        Graph g = construirGrafo(engine);

//        g.addAttribute("ui.stylesheet", "url('file:///fig.png')");
        Viewer view = g.display(false);
//        view.getDefaultView().setBackground(Color.red);

        System.out.println("OK");

        frame.add(content, BorderLayout.CENTER);
        frame.pack();
        JButton bt = new JButton("Dijkstra");
        frame.add(bt);
        FlowLayout fl = new FlowLayout(1);
        frame.setLayout(fl);

        frame.setVisible(true);

        bt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Dijkstra d = new Dijkstra(Dijkstra.Element.NODE, null, "weight");

                d.init(g);
//                System.out.println(getNodeNameByCoord(0, 5));
                d.setSource(g.getNode(getNodeNameByCoord(5, 15)));
//                System.out.println(getNodeByCoord(g, 3, 5).getAttribute("weight").toString());
                d.compute();

//                Iterator<Edge> it = d.getPath(g.getNode("V6")).getEdgeIterator();
                System.out.println(g.getNode(getNodeNameByCoord(9, 5)).toString());
//                d.getPath(g.getNode(getNodeNameByCoord(5, 5))).
                java.util.List<Node> l = d.getPath(g.getNode(getNodeNameByCoord(21, 21))).getNodePath();
                Iterator<Node> it = d.getPath(g.getNode(getNodeNameByCoord(21, 21))).getNodeIterator();
                Iterator<Edge> et = d.getPath(g.getNode(getNodeNameByCoord(21, 21))).getEdgeIterator();

                while (et.hasNext()) {
                    Edge ed = et.next();

                    ed.addAttribute("ui.style", "arrow-shape: arrow; fill-color: red; stroke-width: 20; size: 3px; arrow-shape: arrow;");
//                    ed.addAttribute("ui.style", "fill-color: red;");

                }

                System.out.println("l" + l);

                System.out.println("tamanho caminho " + it.hasNext());
               
                while (it.hasNext()) {
                    Node ed = it.next();

                    int xx = ed.getAttribute("cx");
                    int yy = ed.getAttribute("cy");
                    System.out.println("Trocando valor xy" + xx + "-" + yy);
                    ((DefaultIntegerHexModel) engine.getModel()).setValueAt(xx, yy, 2);

                    ed.addAttribute("ui.style", "fill-color: red; size:10px; ");
                }

                content.repaint();

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

    public static Graph construirGrafo(HexEngine<Graphics2D> engine) {
        Integer numColunas = engine.getModel().getColumnNumber();
        Integer numLinhas = engine.getModel().getRowNumber();

        System.out.println("coddlunas:" + numColunas);
        System.out.println("Lindddhas:" + numLinhas);
        Graph g = new SingleGraph("Teste");
       System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
       
       
       
       g.addAttribute("ui.stylesheet", "node { fill-color:blue;} ");
       
       
       g.addAttribute("ui.stylesheet", "graph { fill-mode:  image-scaled-ratio-max; "
                + "fill-image: url('img/fig.png'); "
                +"} ");
       
//g.addAttribute("ui.stylesheet", "node { fill-image: url('fig.png');  fill-mode: image-scaled-ratio-max;  } ");
//        g.addAttribute("ui.stylesheet", "graph {  fill-color: blue; }");
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

//                if (i < numLinhas - 1 && j < numColunas - 1) {
//                    g.addEdge("E" + i + "." + j + "-" + (i + 1) + "." + j,
//                            "V" + i + "." + j,
//                            "V" + (i + 1) + "." + j,true);
//
//                    g.addEdge("E" + i + "." + j + "-" + (i) + "." + (j + 1),
//                            "V" + i + "." + j,
//                            "V" + i + "." + (j + 1),true);
//                }
//
//                if (i == numLinhas - 1 && j < numColunas - 1) {
//                    g.addEdge("E" + i + "." + j + "-" + (i) + "." + (j + 1),
//                            "V" + i + "." + j,
//                            "V" + i + "." + (j + 1),true);
//                }
//
//                if (j == (numColunas - 1) && i < numLinhas - 1) {
//                    g.addEdge("E" + i + "." + j + "-" + (i + 1) + "." + j,
//                            "V" + i + "." + j,
//                            "V" + (i + 1) + "." + j,true);
//
//                }
//            }
//                HexPosition[] positions = engine.getNeighbourPositions(i, j, 1);
////            HexPosition atual = engine.getNeighbourPositions(i, j, 1);
//                ArrayList<HexPosition> lista = new ArrayList<HexPosition>(Arrays.asList(positions));
//                for (HexPosition p : lista) {
//                    System.out.println("(" + i + "," + j + ")P:" + p.toString());
//                    if (p.getColumn() < NUM_COLS && p.getRow() < NUM_ROWS) {
//                        if (p.getColumn() > i) {
//                            try {
//                                g.addEdge("E" + i + "." + j + "-" + p.getColumn() + "." + p.getRow(),
//                                        "V" + i + "." + j, "V" + p.getColumn() + "." + p.getRow(), true);
//                            } catch (Exception e) {
//
//                            }
//                        }
//                    }
//                }
            }
        }
        for (Edge e : g.getEdgeSet()) {
//            e.addAttribute("ui.style", "stroke-width: 1px;");
//            e.addAttribute("ui.style", "arrow-size: 1px, 1px;");
//            e.addAttribute("ui.style", "size: 1px; ");

//            e.addAttribute("weig/ht", 1);
//            e.addAttribute("label", e.toString());
//            e.setAttribute("directed", true);
        }
//        getNodeByCoord(g, 3, 5).setAttribute("weight", 9000);
//        getNodeByCoord(g, 5, 3).setAttribute("weight", 9000);
        getNodeByCoord(g, 5, 3).setAttribute("weight", Double.POSITIVE_INFINITY);
        getNodeByCoord(g, 5, 6).setAttribute("weight", Double.POSITIVE_INFINITY);
        getNodeByCoord(g, 3, 5).setAttribute("weight", Double.POSITIVE_INFINITY);
        getNodeByCoord(g, 6, 4).setAttribute("weight", Double.POSITIVE_INFINITY);
//        System.out.println(getNodeByCoord(g, 3, 5).getAttribute("weight").toString());
//        getEdgeNameByCoord(0, 0, 0, 0)

//        g.display();
        g.addAttribute("ui.quality");
        g.addAttribute("ui.antialias");
        return g;
    }
}
