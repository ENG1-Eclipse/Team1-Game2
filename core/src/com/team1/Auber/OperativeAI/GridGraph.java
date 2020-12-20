package com.team1.Auber.OperativeAI;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.team1.Auber.MapRenderer;
import com.team1.Auber.Operative;

/**
 *  Used to calculate how the operative needs to move by following the path. This is the main pathfinding class used
 *  by the {@link Operative}.
 *
 * @author Adam Wiegrand
 */
public class GridGraph implements IndexedGraph<GridNode>{
    GridHeur gridHeuristic = new GridHeur();
    Array<Connection<GridNode>>[][] paths;
    public GridNode[][] GridMap;
    MapRenderer map;
    private int lastNodeIndex = 0;

    /**
     * Create the graph from the map.
     *
     * @param map the map representation
     * @param x The initial X coordinate
     * @param y The initial Y coordinate
     */
    public GridGraph(MapRenderer map, int x, int y){
        this.map = map;
        GridMap = new GridNode[map.intMap.length][map.intMap[0].length];
        paths = new Array[map.intMap[0].length][];

        //Create connections between all touching tiles on the map
        for (int i = 0; i < map.intMap.length; i++) {
            paths[i] = new Array[map.intMap[0].length];
            for (int j = 0; j < map.intMap.length ; j++) {
                paths[i][j] = new Array<Connection<GridNode>>();
            }
        }

        addNode(x,y);
        Tree(x,y);
    }

    /**
     * Creates a tree with all the different paths to the coordinates (X,Y)
     *
     * @param x The x coordinate
     * @param y The Y coordinate
     */
    private void Tree(int x, int y){
        for (GridNode.Pos coord : GridMap[x][y].ConnectingCoords()) {
            if (GridMap[coord.x][coord.y] == null){
                addNode(coord.x,coord.y);
                paths[x][y].add(new GridPath(GridMap[x][y],GridMap[coord.x][coord.y]));
                Tree(coord.x,coord.y);
            } else {
                paths[x][y].add(new GridPath(GridMap[x][y],GridMap[coord.x][coord.y]));
            }
        }
    }

    /**
     * Adds a node. This could the starting position of a operative, or the position of a system. A node is a
     * starting point or destination.
     *
     * @param x The x coordinate of the node
     * @param y The Y coordinate of the node
     */
    public void addNode(int x, int y){
        GridNode node = new GridNode(map,x,y);
        node.index = lastNodeIndex;
        lastNodeIndex++;
        GridMap[x][y] = node;
    }

    /**
     *  Finds the path between two nodes. This uses a A* search algorithm to find the most optimum route.
     *
     * @param startNode The start node as a GridNode object
     * @param goalNode The end node as a GridNode object
     * @return the best path
     */
    public GraphPath<GridNode> findPath(GridNode startNode, GridNode goalNode){
        GraphPath<GridNode> path = new DefaultGraphPath<>();
        new IndexedAStarPathFinder<>(this).searchNodePath(startNode, goalNode, gridHeuristic, path);
        return path;
    }

    /**
     *  Finds the path between two points.
     *
     * @param startX The X coordinate of the start position
     * @param startY The Y coordinate of the start position
     * @param goalX The X coordinate of the end position
     * @param goalY The Y coordinate of the end position
     * @return the best path
     */
    public GraphPath<GridNode> findPath(int startX,int startY, int goalX, int goalY){// map coords
        GraphPath<GridNode> path = new DefaultGraphPath<>();
        new IndexedAStarPathFinder<>(this).searchNodePath(GridMap[startX][startY], GridMap[goalX][goalY], gridHeuristic, path);
        return path;
    }

    @Override
    public Array<Connection<GridNode>> getConnections(GridNode fromNode) {
        return paths[fromNode.x][fromNode.y];
    }

    @Override
    public int getIndex(GridNode node) {
        return node.index;
    }

    @Override
    public int getNodeCount() {
        return lastNodeIndex;
    }
}
