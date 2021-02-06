package com.team1.Auber.OperativeAI;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.math.Vector2;

/**
 A {@code Heuristic} generates estimates of the cost to move from a given node to the goal.
 *
 * @author Adam Wiegrand (Team 4)
 */
public class GridHeur implements Heuristic<GridNode>{
    @Override
    public float estimate(GridNode node, GridNode endNode) {
        return Vector2.dst(node.x, node.y, endNode.x, endNode.y);
    }
}
