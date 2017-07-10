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

    public static final int NUM_COLS = 133;
    public static final int NUM_ROWS = 90;
    
    public static final int SOURCE_X = 8;
    public static final int SOURCE_Y = 53;

    public static final int SINK_X = 123;
    public static final int SINK_Y = 53;

    public static final Double RATE_Y = 0.577d;
    
    
    public static Viewer v;
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
                v.getDefaultView().getCamera().setViewPercent(0.5d);
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

                while (et.hasNext()) {
                    Edge ed = et.next();
                    ed.addAttribute("ui.style", " fill-color: red; stroke-width: 10; size: 2px; ");
                }

                int i = 0;
                while (it.hasNext()) {
                    i++;
                    Node ed = it.next();

                    int xx = ed.getAttribute("cx");
                    int yy = ed.getAttribute("cy");
                    ((DefaultIntegerHexModel) engine.getModel()).setValueAt(xx, yy, 2);

                    ed.addAttribute("ui.style", "fill-color: red; size:6px; ");
                }
                System.out.println("TOTAL DE TORRES: "+i);

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

        getNodeByCoord(g, 5, 3).setAttribute("weight", Double.POSITIVE_INFINITY);
        getNodeByCoord(g, 5, 6).setAttribute("weight", Double.POSITIVE_INFINITY);
        getNodeByCoord(g, 3, 5).setAttribute("weight", Double.POSITIVE_INFINITY);
        getNodeByCoord(g, 6, 4).setAttribute("weight", Double.POSITIVE_INFINITY);

        getNodeByCoord(g, 8, 18).setAttribute("weight", Double.POSITIVE_INFINITY);
        getNodeByCoord(g, 9, 18).setAttribute("weight", Double.POSITIVE_INFINITY);
        getNodeByCoord(g, 10, 18).setAttribute("weight", Double.POSITIVE_INFINITY);
        
        getNodeByCoord(g, 7, 20).setAttribute("weight", Double.POSITIVE_INFINITY);
        getNodeByCoord(g, 8, 20).setAttribute("weight", Double.POSITIVE_INFINITY);
        getNodeByCoord(g, 8, 21).setAttribute("weight", Double.POSITIVE_INFINITY);
        getNodeByCoord(g, 9, 20).setAttribute("weight", Double.POSITIVE_INFINITY);
        
        getNodeByCoord(g, 7, 19).setAttribute("weight", Double.POSITIVE_INFINITY);
        getNodeByCoord(g, 8, 19).setAttribute("weight", Double.POSITIVE_INFINITY);
        
        getNodeByCoord(g, 9, 19).setAttribute("weight", Double.POSITIVE_INFINITY);
        getNodeByCoord(g, 16, 24).setAttribute("weight", Double.POSITIVE_INFINITY);

        for (Node n : g.getNodeSet()) {
       //     n.setAttribute("label", n.getId());
            System.out.println(n.getAttribute("weight").toString());
            if (Double.valueOf(n.getAttribute("weight").toString()) >= Double.POSITIVE_INFINITY) {
//                n.setAttribute("ui.style", "fill-color: blue;");
                System.out.println("Setando classe->" + n.toString());
                n.setAttribute("ui.class", "infinity");
            } else {
            }
        }

        g.addAttribute("ui.quality");
        g.addAttribute("ui.antialias");
        return g;
    }
}
