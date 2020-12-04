package com.group4.Auber.OperativeAI;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import com.group4.Auber.MapRenderer;

/**
 * A representation of a node with in the map. This could the starting position of a operative, or the position of a system. A node is a
 * starting point or destination on the map.
 *
 * @author Adam Wiegrand
 */
public class GridNode {
    protected MapRenderer map;
    public int x;
    public int y;
    public int index;
    private Set<Pos> coords;
    private boolean calced = false;

    /**
     * Create the grid node a point (X,Y) on the map
     *
     * @param map The map representation
     * @param x The X coordinate of the node
     * @param y The Y coordinate of the node
     */
    public GridNode(MapRenderer map, int x, int y){
        this.map = map;
        this.x = x;
        this.y = y;
    }

    /**
     * Set the index of the node
     * @param index the new index
     */
    public void setIndex(int index){
        this.index = index;
    }

    /**
     * Get a set of the connecting cordinates
     *
     * @return A set of Positions
     */
    public Set<Pos> ConnectingCoords(){
        if (calced){
            return coords;
        }
        return _ConnectingCoords(1);
    }

    /**
     * A class to represent a position
     */
    static class Pos{
        int x;
        int y;
        Pos(int x, int y){
            this.x = x;
            this.y = y;
        }
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Pos){
                Pos other = (Pos) obj;
                return x == other.x && y == other.y;
            }
            return false;
        }
    }

    /**
     * Get a set of adjacent coordinates
     * @param x The X Coordinate of the center square
     * @param y The Y Coordinate of the center square
     * @return A set of positions
     */
    private Set<Pos> Adjacent(int x, int y){
        HashSet<Pos> coords = new HashSet<Pos>();
        coords.add(new Pos(x + 1, y));
        coords.add(new Pos(x - 1, y));
        coords.add(new Pos(x, y + 1));
        coords.add(new Pos(x, y - 1));
        return coords;
    }

    /**
     * Get a set of connection positions.
     *
     * @param antiSquareness how not on a grid the AI will move (higher = less = more intensive). This must be less than or equal to 1.
     * @return A set of positions
     */
    private Set<Pos> _ConnectingCoords(int antiSquareness){
        calced = true;
        if (antiSquareness < 1){
            throw new IllegalArgumentException("antiSquareness must be atleast 1");
        }
        coords = new HashSet<Pos>();
        ArrayList<Pos> blottPool = new ArrayList<Pos>();
        for (Pos pos : Adjacent(x,y)) {
            int nx = pos.x;
            int ny = pos.y;
            if (map.Empty(nx,ny)){
                coords.add(new Pos(nx, ny));
                for (Pos pos2 : Adjacent(nx,ny)) {
                    if ((!(coords.contains(pos2) && blottPool.contains(pos2))) && map.Empty(pos2.x,pos2.y)){
                        blottPool.add(pos2);
                    }
                }
            }
        }

        //end early if the antiSquareness is 1 as there are none
        if (antiSquareness == 1) {return coords;}

        //remove weirds
        for (Pos pos : coords) {
            blottPool.remove(pos);
        }

        //perimeter based on antiSquareness
        for (int i = 0; i < 4*(antiSquareness); i++) {
            Pos coord = blottPool.get(0);
            blottPool.remove(0);

            //Check the north east coords
            if (coord.y > y & coord.x > x){
                if (map.Empty(coord.x - 1,coord.y - 1) && map.Empty(coord.x - 1,coord.y) && map.Empty(coord.x,coord.y - 1)){
                    coords.add(coord);
                    for (Pos pos2 : Adjacent(coord.x,coord.y)) {
                        if ((!(coords.contains(pos2) && blottPool.contains(pos2))) && map.Empty(pos2.x,pos2.y)){
                            blottPool.add(pos2);
                        }
                    }
                }
            }

            //Check the South East coords
            else if (coord.y < y & coord.x > x){//SE
                if (map.Empty(coord.x - 1,coord.y + 1) && map.Empty(coord.x - 1,coord.y) && map.Empty(coord.x,coord.y + 1)){
                    coords.add(coord);
                    for (Pos pos2 : Adjacent(coord.x,coord.y)) {
                        if ((!(coords.contains(pos2) && blottPool.contains(pos2))) && map.Empty(pos2.x,pos2.y)){
                            blottPool.add(pos2);
                        }
                    }
                }
            }

            //Check the south west coords
            else if (coord.y < y & coord.x < x){//SW
                if (map.Empty(coord.x + 1,coord.y + 1) && map.Empty(coord.x + 1,coord.y) && map.Empty(coord.x,coord.y + 1)){
                    coords.add(coord);
                    for (Pos pos2 : Adjacent(coord.x,coord.y)) {
                        if ((!(coords.contains(pos2) && blottPool.contains(pos2))) && map.Empty(pos2.x,pos2.y)){
                            blottPool.add(pos2);
                        }
                    }
                }
            }

            //Check the north west coords
            else if(coord.y > y & coord.x < x){//NW
                if (map.Empty(coord.x + 1,coord.y - 1) && map.Empty(coord.x + 1,coord.y) && map.Empty(coord.x,coord.y - 1)){
                    coords.add(coord);
                    for (Pos pos2 : Adjacent(coord.x,coord.y)) {
                        if ((!(coords.contains(pos2) && blottPool.contains(pos2))) && map.Empty(pos2.x,pos2.y)){
                            blottPool.add(pos2);
                        }
                    }
                }
            }
        }
        return coords;
    }
}
