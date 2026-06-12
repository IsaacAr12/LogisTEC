package ui;

import graph.Edge;
import graph.Graph;
import graph.Vertex;
import graph.VertexType;

import planner.RouteResult;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Ventana básica para visualizar el grafo y las rutas calculadas.
 *
 * Esta interfaz no busca ser compleja ni bonita, solo interpretable
 * durante la defensa del proyecto.
 */
public class LogisTecFrame extends JFrame {

    public LogisTecFrame(Graph graph, RouteResult[] routes, String[] truckIds) {
        setTitle("LogísTEC - Visualización de rutas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        GraphPanel panel = new GraphPanel(graph, routes, truckIds);
        add(panel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    public static void showWindow(Graph graph, RouteResult[] routes, String[] truckIds) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LogisTecFrame frame = new LogisTecFrame(graph, routes, truckIds);
                frame.setVisible(true);
            }
        });
    }

    private static class GraphPanel extends JPanel {

        private final Graph graph;
        private final RouteResult[] routes;
        private final String[] truckIds;

        private final Color[] routeColors = {
                new Color(220, 40, 40),
                new Color(40, 90, 220),
                new Color(30, 150, 70),
                new Color(180, 80, 200),
                new Color(230, 140, 30)
        };

        public GraphPanel(Graph graph, RouteResult[] routes, String[] truckIds) {
            this.graph = graph;
            this.routes = routes;
            this.truckIds = truckIds;

            setPreferredSize(new Dimension(1000, 720));
            setBackground(new Color(245, 247, 250));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            Graphics2D g2 = (Graphics2D) graphics;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            drawTitle(g2);
            drawEdges(g2);
            drawRoutes(g2);
            drawVertices(g2);
            drawLegend(g2);
        }

        private void drawTitle(Graphics2D g2) {
            g2.setColor(new Color(30, 30, 30));
            g2.setFont(new Font("Arial", Font.BOLD, 22));
            g2.drawString("LogísTEC - Grafo de ciudad y rutas planificadas", 25, 35);

            g2.setFont(new Font("Arial", Font.PLAIN, 13));
            g2.drawString("Gris: calles del grafo | Colores: mejor ruta calculada para cada camión", 25, 58);
        }

        private void drawEdges(Graphics2D g2) {
            g2.setStroke(new BasicStroke(1));
            g2.setColor(new Color(180, 180, 180));

            for (Edge edge : graph.getEdges()) {
                Vertex u = edge.getU();
                Vertex v = edge.getV();

                int x1 = scaleX(u.getX());
                int y1 = scaleY(u.getY());
                int x2 = scaleX(v.getX());
                int y2 = scaleY(v.getY());

                g2.drawLine(x1, y1, x2, y2);

                int midX = (x1 + x2) / 2;
                int midY = (y1 + y2) / 2;

                g2.setFont(new Font("Arial", Font.PLAIN, 10));
                g2.setColor(new Color(110, 110, 110));
                g2.drawString(String.valueOf(edge.getDistance()), midX, midY);

                g2.setColor(new Color(180, 180, 180));
            }
        }

        private void drawRoutes(Graphics2D g2) {
            if (routes == null) {
                return;
            }

            g2.setStroke(new BasicStroke(4));

            for (int i = 0; i < routes.length; i++) {
                RouteResult route = routes[i];

                if (route == null) {
                    continue;
                }

                Color color = routeColors[i % routeColors.length];
                g2.setColor(color);

                int[] vertices = route.getRoute();

                for (int j = 0; j < vertices.length - 1; j++) {
                    Vertex a = graph.getVertexByIndex(vertices[j]);
                    Vertex b = graph.getVertexByIndex(vertices[j + 1]);

                    if (a != null && b != null) {
                        int x1 = scaleX(a.getX());
                        int y1 = scaleY(a.getY());
                        int x2 = scaleX(b.getX());
                        int y2 = scaleY(b.getY());

                        g2.drawLine(x1, y1, x2, y2);
                        drawArrowHead(g2, x1, y1, x2, y2);
                    }
                }
            }

            g2.setStroke(new BasicStroke(1));
        }

        private void drawArrowHead(Graphics2D g2, int x1, int y1, int x2, int y2) {
            double angle = Math.atan2(y2 - y1, x2 - x1);

            int arrowLength = 12;
            int arrowWidth = 7;

            int ax1 = (int) (x2 - arrowLength * Math.cos(angle - Math.PI / 6));
            int ay1 = (int) (y2 - arrowLength * Math.sin(angle - Math.PI / 6));

            int ax2 = (int) (x2 - arrowLength * Math.cos(angle + Math.PI / 6));
            int ay2 = (int) (y2 - arrowLength * Math.sin(angle + Math.PI / 6));

            g2.drawLine(x2, y2, ax1, ay1);
            g2.drawLine(x2, y2, ax2, ay2);
        }

        private void drawVertices(Graphics2D g2) {
            for (Vertex vertex : graph.getVertices()) {
                int x = scaleX(vertex.getX());
                int y = scaleY(vertex.getY());

                if (vertex.getType() == VertexType.DEPOT) {
                    drawDepot(g2, vertex, x, y);
                } else if (vertex.getType() == VertexType.DELIVERY) {
                    drawDelivery(g2, vertex, x, y);
                } else {
                    drawIntersection(g2, vertex, x, y);
                }
            }
        }

        private void drawDepot(Graphics2D g2, Vertex vertex, int x, int y) {
            int size = 24;

            g2.setColor(new Color(255, 210, 60));
            g2.fillOval(x - size / 2, y - size / 2, size, size);

            g2.setColor(new Color(120, 90, 0));
            g2.setStroke(new BasicStroke(3));
            g2.drawOval(x - size / 2, y - size / 2, size, size);

            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.setColor(new Color(30, 30, 30));
            g2.drawString(vertex.getId(), x - 13, y - 17);

            g2.setStroke(new BasicStroke(1));
        }

        private void drawDelivery(Graphics2D g2, Vertex vertex, int x, int y) {
            int size = 18;

            g2.setColor(new Color(80, 170, 90));
            g2.fillOval(x - size / 2, y - size / 2, size, size);

            g2.setColor(new Color(20, 90, 40));
            g2.drawOval(x - size / 2, y - size / 2, size, size);

            g2.setFont(new Font("Arial", Font.PLAIN, 11));
            g2.setColor(new Color(30, 30, 30));
            g2.drawString(vertex.getId(), x - 12, y - 13);
        }

        private void drawIntersection(Graphics2D g2, Vertex vertex, int x, int y) {
            int size = 13;

            g2.setColor(new Color(90, 120, 180));
            g2.fillOval(x - size / 2, y - size / 2, size, size);

            g2.setColor(new Color(30, 50, 100));
            g2.drawOval(x - size / 2, y - size / 2, size, size);

            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            g2.setColor(new Color(40, 40, 40));
            g2.drawString(vertex.getId(), x - 10, y - 10);
        }

        private void drawLegend(Graphics2D g2) {
            int x = 25;
            int y = 620;

            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.setColor(new Color(30, 30, 30));
            g2.drawString("Leyenda", x, y);

            y += 22;

            drawLegendCircle(g2, x, y, new Color(255, 210, 60), "Depósito");
            y += 22;

            drawLegendCircle(g2, x, y, new Color(80, 170, 90), "Punto de entrega");
            y += 22;

            drawLegendCircle(g2, x, y, new Color(90, 120, 180), "Intersección");
            y += 30;

            if (routes != null) {
                for (int i = 0; i < routes.length; i++) {
                    if (routes[i] == null) {
                        continue;
                    }

                    Color color = routeColors[i % routeColors.length];
                    g2.setColor(color);
                    g2.setStroke(new BasicStroke(4));
                    g2.drawLine(x, y - 5, x + 35, y - 5);

                    g2.setStroke(new BasicStroke(1));
                    g2.setColor(new Color(30, 30, 30));
                    g2.setFont(new Font("Arial", Font.PLAIN, 12));

                    String truck = truckIds != null && truckIds[i] != null ? truckIds[i] : "Camión " + (i + 1);
                    g2.drawString(truck + " - " + routes[i].getHeuristicName()
                            + " (" + routes[i].getTotalDistance() + " m)", x + 45, y);

                    y += 22;
                }
            }
        }

        private void drawLegendCircle(Graphics2D g2, int x, int y, Color color, String text) {
            g2.setColor(color);
            g2.fillOval(x, y - 12, 14, 14);

            g2.setColor(new Color(40, 40, 40));
            g2.drawOval(x, y - 12, 14, 14);

            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            g2.drawString(text, x + 22, y);
        }

        private int scaleX(int originalX) {
            return originalX + 45;
        }

        private int scaleY(int originalY) {
            return originalY + 80;
        }
    }
}