package com.team1.Auber.OperativeAI;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.math.Vector2;

/**
 *  A connection between two points of the map
 *
 * @author Adam Wiegrand (Team 4)
 */
public class GridPath implements Connection<GridNode>{
    GridNode from;
    GridNode to;
    float cost;

    /**
     * Create a path between two nodes
     *
     * @param from the start node
     * @param to the end node
     */
    public GridPath(GridNode from, GridNode to){
        this.from = from;
        this.to = to;
        cost = Vector2.dst(from.x, from.y, to.x, to.y);
    }

    @Override
    public float getCost() {
        return cost;
    }

    @Override
    public GridNode getFromNode() {
        return from;
    }

    @Override
    public GridNode getToNode() {
        return to;
    }
    
}
