import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

public class MyAdjacenyListGraph<T> {
    class Node<K> implements Comparable<Node<K>> {
        K connection;
        Integer weight;

        Node(K connection, int weight) {
            this.connection = connection;
            this.weight = weight;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !obj.getClass().equals(this.getClass())) {
                return false;
            } else {
                try {
                    @SuppressWarnings("unchecked")
                    Node<K> tempNode = (Node<K>) obj;
                    return this.connection.equals(tempNode.connection);
                } catch (Exception e) {
                    return false;
                }
            }
        }

        @Override
        public int compareTo(Node<K> o) {
            return this.weight.compareTo(o.weight);
        }
    }

    class dijkstrasNode<K> implements Comparable<dijkstrasNode<K>> {
        K node;
        Integer distance;
        boolean found = false;

        dijkstrasNode(K node, int distance) {
            this.node = node;
            this.distance = distance;
        }

        @Override
        public int compareTo(MyAdjacenyListGraph<T>.dijkstrasNode<K> o) {
            return this.distance.compareTo(o.distance);
        }
    }

    private HashMap<T, ArrayList<Node<T>>> graph = new HashMap<>();

    public int dijkstras(T source, T destionation) {
        if (graph.containsKey(source) && graph.containsKey(destionation)) {
            if (source.equals(destionation)) {
                return 0;
            } else {
                HashMap<T, dijkstrasNode<T>> distances = new HashMap<T, dijkstrasNode<T>>();
                for (T key : graph.keySet()) {
                    distances.put(key, new dijkstrasNode<T>(key, Integer.MAX_VALUE));
                }
                PriorityQueue<dijkstrasNode<T>> listOfNodes = new PriorityQueue<>();
                listOfNodes.add(new dijkstrasNode<T>(source, 0));
                distances.get(source).distance = 0;
                while (!listOfNodes.isEmpty()) {
                    T node = listOfNodes.poll().node;
                    if (!distances.get(node).found) {
                        distances.get(node).found = true;
                        for (Node<T> connectedNode : graph.get(node)) {
                            if (!distances.get(connectedNode.connection).found) {
                                int tempDistance = distances.get(node).distance + connectedNode.weight;
                                if (distances.get(connectedNode.connection).distance > tempDistance) {
                                    distances.get(connectedNode.connection).distance = tempDistance;
                                    listOfNodes.add(new dijkstrasNode<T>(connectedNode.connection, tempDistance));
                                }
                            }
                        }
                    }
                }
                if (distances.get(destionation).distance < Integer.MAX_VALUE) {
                    return distances.get(destionation).distance;
                }
            }
        }
        return -1;
    }

    public void print() {
        for (Map.Entry<T, ArrayList<Node<T>>> entry : graph.entrySet()) {
            String line = entry.getKey() + " | ";
            for (Node<T> node : entry.getValue()) {
                line = line + "<" + node.connection + "|" + node.weight + ">" + ",";
            }
            System.out.println(line);
        }
    }

    public int depthFirstSearch(T source, T destionation) {
        if (graph.containsKey(source) && graph.containsKey(destionation)) {
            HashMap<T, T> tempGraph = new HashMap<>();
            tempGraph.put(source, source);
            int distance = depthFirstSearch(source, tempGraph, destionation, 0);
            if (distance == Integer.MAX_VALUE) {
                return -1;
            } else {
                return distance;
            }
        }
        return -1;
    }

    public int depthFirstSearch(T currentNode, HashMap<T, T> found, T targetNode, int currentDistance) {
        int distance = Integer.MAX_VALUE;
        if (currentNode.equals(targetNode)) {
            found.remove(currentNode);
            return currentDistance;
        } else {
            for (Node<T> node : graph.get(currentNode)) {
                T value = node.connection;
                if (found.putIfAbsent(value, value) == null) {
                    int tempDistance = depthFirstSearch(value, found, targetNode, currentDistance + 1);
                    if (tempDistance < distance) {
                        distance = tempDistance;
                    }
                }
            }
        }
        found.remove(currentNode);
        return distance;
    }

    public int breadthFirstSearch(T source, T destionation) {
        HashMap<T, T> foundNode = new HashMap<>();
        int distance = 0;
        if (graph.containsKey(source) && graph.containsKey(destionation)) {
            if (source.equals(destionation)) {
                return 0;
            }
            Queue<T> nodes = new LinkedList<>();
            nodes.offer(source);
            foundNode.put(source, source);
            while (!nodes.isEmpty()) {
                /*
                 * / for(T value : nodes){
                 * System.out.print(value+",");
                 * }
                 * System.out.println("");
                 */
                distance++;
                Queue<T> tempNodes = new LinkedList<>();
                for (T value : nodes) {

                    for (Node<T> connectionNode : graph.get(value)) {
                        if (foundNode.putIfAbsent(connectionNode.connection, connectionNode.connection) == null) {
                            tempNodes.offer(connectionNode.connection);
                            if (connectionNode.connection.equals(destionation)) {
                                return distance;
                            }
                        }
                    }
                }
                nodes = tempNodes;
            }
        }
        return -1;
    }

    public boolean addVertex(T data) {
        if (graph.get(data) == null) {
            graph.put(data, new ArrayList<Node<T>>());
            return true;
        } else {
            return false;
        }
    }

    public boolean removeVertex(T data) {
        if (graph.remove(data) != null) {
            for (ArrayList<Node<T>> connectionNodes : graph.values()) {
                connectionNodes.remove(new Node<T>(data, -1));
            }
            return true;
        }
        return false;
    }

    public boolean addEdge(T vert1, T vert2, boolean directed) {
        ArrayList<Node<T>> connections1 = graph.get(vert1);
        ArrayList<Node<T>> connections2 = graph.get(vert2);
        if (connections1 != null && connections2 != null) {
            if (!directed) {
                Node<T> tempNode = new Node<T>(vert1, 1);
                int index = connections2.indexOf(tempNode);
                if (index != -1) {
                    connections2.get(index).weight++;
                } else {
                    connections2.add(tempNode);
                }
            }
            Node<T> tempNode = new Node<T>(vert2, 1);
            int index = connections1.indexOf(tempNode);
            if (index != -1) {
                connections1.get(index).weight++;
            } else {
                //System.out.println(vert1+"|"+vert2);
                connections1.add(tempNode);
            }
            return true;
        }
        return false;
    }

    public boolean setEdge(T vert1, T vert2) {
        return setEdge(vert1, vert2, 1, false);
    }

    public boolean setEdge(T vert1, T vert2, int weight) {
        return setEdge(vert1, vert2, weight, false);
    }

    public boolean setEdge(T vert1, T vert2, int weight, boolean directed) {
        ArrayList<Node<T>> connections1 = graph.get(vert1);
        ArrayList<Node<T>> connections2 = graph.get(vert2);
        if (connections1 != null && connections2 != null) {
            if (!directed) {
                Node<T> tempNode = new Node<T>(vert1, weight);
                connections2.remove(tempNode);
                connections2.add(tempNode);
            }
            Node<T> tempNode = new Node<T>(vert2, weight);
            connections1.remove(tempNode);
            connections1.add(tempNode);
            return true;
        }
        return false;
    }

    public boolean removeEdge(T vert1, T vert2) {
        ArrayList<Node<T>> connections1 = graph.get(vert1);
        ArrayList<Node<T>> connections2 = graph.get(vert2);
        if (connections1 != null && connections2 != null) {
            boolean returnValue1 = connections1.remove(new Node<T>(vert2, -1));
            boolean returnValue2 = connections2.remove(new Node<T>(vert1, -1));
            return returnValue1 || returnValue2;
        }
        return false;
    }

    public static void printSeperator() {
        System.out.println("------------------------------------");
    }

    public static void main(String[] args) {
        MyAdjacenyListGraph<Integer> graph = new MyAdjacenyListGraph<>();
        for (int i = 0; i < 7; i++) {
            graph.addVertex(i);
        }
        graph.setEdge(0, 1);
        graph.setEdge(0, 2);
        graph.setEdge(1, 2);
        graph.setEdge(1, 3);
        graph.setEdge(2, 4);
        graph.setEdge(3, 4);
        graph.setEdge(4, 5);
        graph.setEdge(5, 6);
        // graph.print();
        System.out.println("Expected: 4");
        System.out.println(graph.breadthFirstSearch(0, 6));
        printSeperator();
        System.out.println("Expected: 2");
        System.out.println(graph.breadthFirstSearch(0, 3));
        printSeperator();
        System.out.println("Expected: 4");
        System.out.println(graph.depthFirstSearch(0, 6));
        printSeperator();
        System.out.println("Expected: 2");
        System.out.println(graph.depthFirstSearch(0, 3));
        printSeperator();
        System.out.println("Expected: 4");
        System.out.println(graph.dijkstras(0, 6));
        printSeperator();
        System.out.println("Expected: 2");
        System.out.println(graph.dijkstras(0, 3));
    }
}
