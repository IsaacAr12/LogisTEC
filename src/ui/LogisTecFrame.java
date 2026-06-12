package ui;

import graph.Edge;
import graph.Graph;
import graph.Vertex;
import graph.VertexType;

import planner.RouteResult;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Interfaz gráfica de LogísTEC.
 *
 * Versión más clara:
 * - No muestra pesos de aristas para evitar saturación visual.
 * - Permite ver todas las rutas o una ruta específica.
 * - Numera el orden de visita cuando se muestra una ruta individual.
 * - Mantiene el grafo base en gris claro.
 */
public class LogisTecFrame extends JFrame {

    public LogisTecFrame(Graph graph, RouteResult[] routes, String[] truckIds) {
        setTitle("LogísTEC - Visualización del grafo y rutas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        GraphPanel graphPanel = new GraphPanel(graph, routes, truckIds);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(235, 240, 248));

        JButton baseButton = new JButton("Grafo limpio");
        JButton allButton = new JButton("Todas las rutas");

        buttonPanel.add(baseButton);
        buttonPanel.add(allButton);

        baseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                graphPanel.setSelectedRoute(-2);
            }
        });

        allButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                graphPanel.setSelectedRoute(-1);
            }
        });

        if (routes != null) {
            for (int i = 0; i < routes.length; i++) {
                if (routes[i] != null) {
                    final int index = i;

                    String name = "Camión " + (i + 1);

                    if (truckIds != null && i < truckIds.length && truckIds[i] != null) {
                        name = truckIds[i];
                    }

                    JButton routeButton = new JButton(name);

                    routeButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            graphPanel.setSelectedRoute(index);
                        }
                    });

                    buttonPanel.add(routeButton);
                }
            }
        }

        add(buttonPanel, BorderLayout.NORTH);
        add(graphPanel, BorderLayout.CENTER);

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

        /**
         * -2 = solo grafo limpio
         * -1 = todas las rutas
         *  0 o más = ruta específica
         */
        private int selectedRoute = -2;

        private final int canvasWidth = 1280;
        private final int canvasHeight = 760;

        private final int mapX = 35;
        private final int mapY = 65;
        private final int mapWidth = 890;
        private final int mapHeight = 640;

        private final int sideX = 950;
        private final int sideY = 65;
        private final int sideWidth = 285;
        private final int sideHeight = 640;

        private final int graphMarginX = 55;
        private final int graphMarginY = 70;

        private int minX;
        private int maxX;
        private int minY;
        private int maxY;

        private final Color[] routeColors = {
                new Color(210, 45, 45),
                new Color(40, 90, 220),
                new Color(35, 150, 80),
                new Color(155, 70, 190),
                new Color(225, 135, 25)
        };

        public GraphPanel(Graph graph, RouteResult[] routes, String[] truckIds) {
            this.graph = graph;
            this.routes = routes;
            this.truckIds = truckIds;

            setPreferredSize(new Dimension(canvasWidth, canvasHeight));
            setBackground(new Color(235, 240, 248));

            calculateBounds();
        }

        public void setSelectedRoute(int selectedRoute) {
            this.selectedRoute = selectedRoute;
            repaint();
        }

        private void calculateBounds() {
            boolean first = true;

            for (Vertex vertex : graph.getVertices()) {
                if (first) {
                    minX = vertex.getX();
                    maxX = vertex.getX();
                    minY = vertex.getY();
                    maxY = vertex.getY();
                    first = false;
                } else {
                    if (vertex.getX() < minX) {
                        minX = vertex.getX();
                    }

                    if (vertex.getX() > maxX) {
                        maxX = vertex.getX();
                    }

                    if (vertex.getY() < minY) {
                        minY = vertex.getY();
                    }

                    if (vertex.getY() > maxY) {
                        maxY = vertex.getY();
                    }
                }
            }

            if (minX == maxX) {
                maxX = minX + 1;
            }

            if (minY == maxY) {
                maxY = minY + 1;
            }
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            Graphics2D g2 = (Graphics2D) graphics;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            drawBackgroundPanels(g2);
            drawTitle(g2);
            drawEdges(g2);
            drawRoutes(g2);
            drawVertices(g2);
            drawRouteNumbers(g2);
            drawSidePanel(g2);
        }

        private void drawBackgroundPanels(Graphics2D g2) {
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(mapX, mapY, mapWidth, mapHeight, 18, 18);

            g2.setColor(new Color(205, 215, 230));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(mapX, mapY, mapWidth, mapHeight, 18, 18);

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(sideX, sideY, sideWidth, sideHeight, 18, 18);

            g2.setColor(new Color(205, 215, 230));
            g2.drawRoundRect(sideX, sideY, sideWidth, sideHeight, 18, 18);

            g2.setStroke(new BasicStroke(1));
        }

        private void drawTitle(Graphics2D g2) {
            g2.setColor(new Color(25, 35, 55));
            g2.setFont(new Font("Arial", Font.BOLD, 19));
            g2.drawString("Mapa de ciudad", mapX + 20, mapY + 30);

            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            g2.setColor(new Color(80, 90, 110));

            if (selectedRoute == -2) {
                g2.drawString("Vista limpia: se muestran calles, depósito, entregas e intersecciones.", mapX + 20, mapY + 50);
            } else if (selectedRoute == -1) {
                g2.drawString("Vista general: se muestran todas las rutas calculadas.", mapX + 20, mapY + 50);
            } else {
                g2.drawString("Vista individual: se resalta una ruta y se numera el orden de visita.", mapX + 20, mapY + 50);
            }
        }

        private void drawEdges(Graphics2D g2) {
            g2.setStroke(new BasicStroke(1));
            g2.setColor(new Color(205, 210, 220));

            for (Edge edge : graph.getEdges()) {
                Vertex u = edge.getU();
                Vertex v = edge.getV();

                int x1 = scaleX(u.getX());
                int y1 = scaleY(u.getY());
                int x2 = scaleX(v.getX());
                int y2 = scaleY(v.getY());

                g2.drawLine(x1, y1, x2, y2);
            }
        }

        private void drawRoutes(Graphics2D g2) {
            if (routes == null || selectedRoute == -2) {
                return;
            }

            if (selectedRoute == -1) {
                for (int i = 0; i < routes.length; i++) {
                    drawSingleRoute(g2, i, false);
                }
            } else {
                drawSingleRoute(g2, selectedRoute, true);
            }
        }

        private void drawSingleRoute(Graphics2D g2, int routeIndex, boolean strong) {
            if (routeIndex < 0 || routeIndex >= routes.length) {
                return;
            }

            RouteResult route = routes[routeIndex];

            if (route == null) {
                return;
            }

            Color color = routeColors[routeIndex % routeColors.length];

            if (strong) {
                g2.setStroke(new BasicStroke(7, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 95));
                drawRouteLines(g2, route, false);

                g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(color);
                drawRouteLines(g2, route, true);
            } else {
                g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 155));
                drawRouteLines(g2, route, true);
            }

            g2.setStroke(new BasicStroke(1));
        }

        private void drawRouteLines(Graphics2D g2, RouteResult route, boolean arrows) {
            int[] vertices = route.getRoute();

            for (int i = 0; i < vertices.length - 1; i++) {
                Vertex a = graph.getVertexByIndex(vertices[i]);
                Vertex b = graph.getVertexByIndex(vertices[i + 1]);

                if (a == null || b == null) {
                    continue;
                }

                int x1 = scaleX(a.getX());
                int y1 = scaleY(a.getY());
                int x2 = scaleX(b.getX());
                int y2 = scaleY(b.getY());

                g2.drawLine(x1, y1, x2, y2);

                if (arrows) {
                    drawArrowHead(g2, x1, y1, x2, y2);
                }
            }
        }

        private void drawArrowHead(Graphics2D g2, int x1, int y1, int x2, int y2) {
            double angle = Math.atan2(y2 - y1, x2 - x1);

            int arrowLength = 12;

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
            int size = 30;

            g2.setColor(new Color(255, 215, 70));
            g2.fillOval(x - size / 2, y - size / 2, size, size);

            g2.setColor(new Color(110, 80, 0));
            g2.setStroke(new BasicStroke(3));
            g2.drawOval(x - size / 2, y - size / 2, size, size);

            g2.setFont(new Font("Arial", Font.BOLD, 15));
            g2.setColor(new Color(35, 35, 35));
            drawCenteredText(g2, "D", x, y + 5);

            g2.setFont(new Font("Arial", Font.BOLD, 11));
            drawCenteredText(g2, vertex.getId(), x, y - 20);

            g2.setStroke(new BasicStroke(1));
        }

        private void drawDelivery(Graphics2D g2, Vertex vertex, int x, int y) {
            int size = 22;

            g2.setColor(new Color(75, 170, 90));
            g2.fillOval(x - size / 2, y - size / 2, size, size);

            g2.setColor(new Color(20, 90, 40));
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(x - size / 2, y - size / 2, size, size);

            g2.setFont(new Font("Arial", Font.BOLD, 10));
            g2.setColor(new Color(25, 25, 25));
            drawCenteredText(g2, vertex.getId(), x, y - 14);

            g2.setStroke(new BasicStroke(1));
        }

        private void drawIntersection(Graphics2D g2, Vertex vertex, int x, int y) {
            int size = 14;

            g2.setColor(new Color(90, 120, 180));
            g2.fillOval(x - size / 2, y - size / 2, size, size);

            g2.setColor(new Color(35, 55, 110));
            g2.drawOval(x - size / 2, y - size / 2, size, size);

            g2.setFont(new Font("Arial", Font.PLAIN, 9));
            g2.setColor(new Color(45, 45, 45));
            drawCenteredText(g2, vertex.getId(), x, y - 11);
        }

        private void drawRouteNumbers(Graphics2D g2) {
            if (selectedRoute < 0 || routes == null || selectedRoute >= routes.length) {
                return;
            }

            RouteResult route = routes[selectedRoute];

            if (route == null) {
                return;
            }

            int[] vertices = route.getRoute();

            for (int i = 0; i < vertices.length; i++) {
                Vertex vertex = graph.getVertexByIndex(vertices[i]);

                if (vertex == null) {
                    continue;
                }

                int x = scaleX(vertex.getX());
                int y = scaleY(vertex.getY());

                String number;

                if (i == 0) {
                    number = "Inicio";
                } else if (i == vertices.length - 1) {
                    number = "Fin";
                } else {
                    number = String.valueOf(i);
                }

                drawNumberBubble(g2, number, x + 14, y + 18);
            }
        }

        private void drawNumberBubble(Graphics2D g2, String text, int x, int y) {
            g2.setFont(new Font("Arial", Font.BOLD, 10));

            int width = g2.getFontMetrics().stringWidth(text) + 12;
            int height = 18;

            g2.setColor(new Color(255, 255, 255, 235));
            g2.fillRoundRect(x - width / 2, y - height / 2, width, height, 10, 10);

            g2.setColor(new Color(40, 40, 40));
            g2.drawRoundRect(x - width / 2, y - height / 2, width, height, 10, 10);

            drawCenteredText(g2, text, x, y + 4);
        }

        private void drawSidePanel(Graphics2D g2) {
            int x = sideX + 22;
            int y = sideY + 35;

            g2.setColor(new Color(25, 35, 55));
            g2.setFont(new Font("Arial", Font.BOLD, 17));
            g2.drawString("Resumen visual", x, y);

            y += 35;

            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            g2.setColor(new Color(65, 75, 90));
            g2.drawString("Vértices: " + graph.vertexCount(), x, y);
            y += 20;
            g2.drawString("Aristas: " + graph.edgeCount(), x, y);

            y += 38;

            g2.setColor(new Color(25, 35, 55));
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString("Leyenda", x, y);

            y += 28;

            drawLegendCircle(g2, x, y, new Color(255, 215, 70), "Depósito");
            y += 28;
            drawLegendCircle(g2, x, y, new Color(75, 170, 90), "Entrega");
            y += 28;
            drawLegendCircle(g2, x, y, new Color(90, 120, 180), "Intersección");

            y += 42;

            g2.setColor(new Color(25, 35, 55));
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString("Vista actual", x, y);

            y += 24;

            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            g2.setColor(new Color(65, 75, 90));

            if (selectedRoute == -2) {
                g2.drawString("Grafo limpio", x, y);
                y += 18;
                g2.drawString("No hay rutas resaltadas.", x, y);
            } else if (selectedRoute == -1) {
                g2.drawString("Todas las rutas", x, y);
                y += 18;
                g2.drawString("Use botones para ver una ruta.", x, y);
            } else {
                drawSelectedRouteInfo(g2, x, y);
            }

            y = sideY + 380;

            g2.setColor(new Color(25, 35, 55));
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString("Rutas disponibles", x, y);

            y += 28;

            drawRoutesList(g2, x, y);
        }

        private void drawSelectedRouteInfo(Graphics2D g2, int x, int y) {
            if (routes == null || selectedRoute >= routes.length || routes[selectedRoute] == null) {
                g2.drawString("Ruta no disponible.", x, y);
                return;
            }

            RouteResult route = routes[selectedRoute];
            Color color = routeColors[selectedRoute % routeColors.length];

            String truckName = getTruckName(selectedRoute);

            g2.setColor(color);
            g2.setStroke(new BasicStroke(5));
            g2.drawLine(x, y - 5, x + 42, y - 5);
            g2.setStroke(new BasicStroke(1));

            g2.setColor(new Color(40, 40, 40));
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString(truckName, x + 52, y);

            y += 22;

            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            g2.setColor(new Color(65, 75, 90));
            g2.drawString("Heurística: " + route.getHeuristicName(), x, y);

            y += 20;
            g2.drawString("Distancia: " + route.getTotalDistance() + " m", x, y);

            y += 28;
            g2.drawString("Los números indican el", x, y);
            y += 18;
            g2.drawString("orden de visita.", x, y);
        }

        private void drawRoutesList(Graphics2D g2, int x, int y) {
            if (routes == null) {
                g2.setFont(new Font("Arial", Font.PLAIN, 12));
                g2.drawString("No hay rutas.", x, y);
                return;
            }

            for (int i = 0; i < routes.length; i++) {
                if (routes[i] == null) {
                    continue;
                }

                Color color = routeColors[i % routeColors.length];

                g2.setColor(color);
                g2.setStroke(new BasicStroke(5));
                g2.drawLine(x, y - 5, x + 36, y - 5);
                g2.setStroke(new BasicStroke(1));

                g2.setFont(new Font("Arial", Font.BOLD, 12));
                g2.setColor(new Color(40, 40, 40));
                g2.drawString(getTruckName(i), x + 48, y);

                y += 17;

                g2.setFont(new Font("Arial", Font.PLAIN, 11));
                g2.setColor(new Color(85, 85, 85));
                g2.drawString(routes[i].getHeuristicName() + " | " + routes[i].getTotalDistance() + " m", x + 48, y);

                y += 28;
            }
        }

        private void drawLegendCircle(Graphics2D g2, int x, int y, Color color, String text) {
            g2.setColor(color);
            g2.fillOval(x, y - 13, 16, 16);

            g2.setColor(new Color(40, 40, 40));
            g2.drawOval(x, y - 13, 16, 16);

            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            g2.drawString(text, x + 26, y);
        }

        private String getTruckName(int index) {
            if (truckIds != null && index < truckIds.length && truckIds[index] != null) {
                return truckIds[index];
            }

            return "Camión " + (index + 1);
        }

        private void drawCenteredText(Graphics2D g2, String text, int x, int y) {
            int width = g2.getFontMetrics().stringWidth(text);
            g2.drawString(text, x - width / 2, y);
        }

        private int scaleX(int originalX) {
            double normalized = (originalX - minX) / (double) (maxX - minX);
            return mapX + graphMarginX + (int) (normalized * (mapWidth - graphMarginX * 2));
        }

        private int scaleY(int originalY) {
            double normalized = (originalY - minY) / (double) (maxY - minY);
            return mapY + graphMarginY + (int) (normalized * (mapHeight - graphMarginY * 2));
        }
    }
}