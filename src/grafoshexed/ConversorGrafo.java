/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grafoshexed;

import static grafoshexed.Test.SINK_X;
import static grafoshexed.Test.SINK_Y;
import static grafoshexed.Test.SOURCE_X;
import static grafoshexed.Test.SOURCE_Y;
import java.util.ArrayList;
import java.util.List;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.Graphs;
import org.graphstream.graph.implementations.SingleGraph;
import scala.collection.Iterator;

/**
 *
 * @author Andre
 */
public class ConversorGrafo {

    Graph g;

    public void converter(Graph gOriginal) {
        g = new SingleGraph("GrafoLinha");

        g = Graphs.clone(gOriginal);
//        System.out.println("Total de NOS(ORIGINAL):" + g.getNodeSet().size());

        for (Edge e : g.getEdgeSet()) {
//            grafoA.addNode(id)
        }
        List<Node> listaNos = new ArrayList<>(g.getNodeSet());
        for (Node n : listaNos) {
            List<Node> tmpEntrada = new ArrayList<>();
            List<Node> tmpSaida = new ArrayList<>();

            List<Edge> l = new ArrayList<>(n.getLeavingEdgeSet());
            List<Edge> e = new ArrayList<>(n.getEnteringEdgeSet());

//            System.out.println("Convertendo no:" + n.getId());
//            System.out.println("Convertendo  Entering no:" + n.getId());
            for (Edge en : e) {
                Node tmp = g.addNode(n.getId() + "-" + en.getId());
                tmp.setAttribute("weight", n.getAttribute("weight").toString());
//                System.out.println("Adicinando no:" + tmp);
                tmp.setAttribute("noOriginal", n.getId());
                tmp.setAttribute("arestaOriginal", en.getId());
                String tmpId = en.getId();
                Node sTmp = en.getSourceNode();
                g.removeEdge(en.getId());
                Edge tmpe = g.addEdge(tmpId, sTmp, tmp, true);

                tmpe.setAttribute("weight", 1);
                tmpEntrada.add(tmp);
                gOriginal.getNode(n.getId()).setAttribute("destinoNovo", tmp.getId());

            }
//            System.out.println("Convertendo  Leaving no:" + n.getId());
            for (Edge sn : l) {
                Node tmp = g.addNode(n.getId() + "/" + sn.getId());
                tmp.setAttribute("weight", n.getAttribute("weight").toString());
//                System.out.println("Adicinando no:" + tmp);
                tmp.setAttribute("noOriginal", n.getId());
                tmp.setAttribute("arestaOriginal", sn.getId());
                String tmpId = sn.getId();
                Node dTmp = sn.getTargetNode();
                g.removeEdge(sn.getId());
                Edge etmp = g.addEdge(tmpId, tmp, dTmp, true);
                etmp.setAttribute("weight", 1);
//                System.out.println("adicionando aresta:" + tmpId + " - " + tmp + " -> " + dTmp);
                tmpSaida.add(tmp);
            }

            for (Node entrada : tmpEntrada) {
                for (Node saida : tmpSaida) {
                    Integer diagonalEntrada = gOriginal.getEdge(entrada.getAttribute("arestaOriginal")).getAttribute("diagonal");
                    Integer diagonalSaida = gOriginal.getEdge(saida.getAttribute("arestaOriginal")).getAttribute("diagonal");
                    Double w = Double.valueOf(n.getAttribute("weight").toString());
                    Edge tmp = g.addEdge(entrada.getId() + "-" + saida.getId(), entrada, saida, true);

                    if (Math.abs(diagonalEntrada - diagonalSaida) > 0) {
                        tmp.setAttribute("weight", (diagonalEntrada + diagonalSaida + 1) * w);
                    }else{
                        tmp.setAttribute("weight", 1* w);
                    }

//                    if(diagonalEntrada > 0 && diagonalSaida > 0){
                    if (Math.abs(diagonalEntrada - diagonalSaida) > 0) {
                        tmp.addAttribute("torreVertice", true);
                    }
                    tmp.addAttribute("interno", true);
                }
            }
            g.removeNode(n);
//            System.out.println("Removido NO:" + n);
        }
//        System.out.println("Total de NOS:" + g.getNodeSet().size());

        for (Node n : g.getNodeSet()) {
//            System.out.println("NoNovo:" + n);
        }

//        g.display();
    }

    public Path caminhoMinimo(Graph original, Node origem, Node destino) {
        System.out.println("Escutei meu chamado");
        Node desNovo = g.getNode(destino.getAttribute("destinoNovo"));
        Node orNovo = g.getNode(origem.getAttribute("destinoNovo"));
        Dijkstra d = new Dijkstra(Dijkstra.Element.EDGE, null, "weight");

        d.init(g);

        d.setSource(orNovo);
//        System.out.println("Source:" + orNovo);
//        System.out.println("Des:" + desNovo);

        d.compute();

        Path path = d.getPath(desNovo);
//                java.util.List<Node> l = d.getPath(g.getNode(getNodeNameByCoord(SINK_X, SINK_Y))).getNodePath();

        for (Edge e : g.getEdgeSet()) {
            if (e.hasAttribute("weight")) {
//                System.out.println("olha meu weight:" + e.getId() + " = " + e.getAttribute("weight"));

            }
        }

        for (Node n : path.getNodeSet()) {
            n.setAttribute("ui.style", "fill-color:red;");
//            System.out.println(n);
//            System.out.println("Original:" + (String) n.getAttribute("noOriginal"));;
            original.getNode((String) n.getAttribute("noOriginal")).setAttribute("ui.style", "fill-color: RED; size: 10px;");
        }

        for (Edge n : path.getEdgeSet()) {
            n.setAttribute("ui.style", "fill-color:red;");
//            System.out.println(n);
//            System.out.println((String) n.getAttribute("noOriginal"));
        }
        return path;
    }
}
