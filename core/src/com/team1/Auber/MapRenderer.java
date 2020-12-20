package com.team1.Auber;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The map render. Extended {@link com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer} and then added functionality to detect whether walls,
 * and healing etc
 *
 * @author Adam Wiegrand
 * @author Robert Watts
 */
public class MapRenderer extends OrthogonalTiledMapRenderer {

    public int[][] intMap;
    public HashSet[][] objMap;
    MapProperties properties;

    //Each one of these is used in the string representation of the map for collision detection
    final int WALL = 1;
    final int EMPTY = 0;
    final int HEAL = 2;

    /**
     * Create the map render and convert the string representation for use in collision detection.
     *
     * @param map A titled map object that contains the visual representation of the map
     * @param strMap A string representation of of the map (from a file), using numbers. Each tile is a number
     *               with what each number means being set above
     */
    public MapRenderer(TiledMap map, String strMap) {
        super(map);
        properties = map.getProperties();

        String[] lines = strMap.split("\\r?\\n");
        Collections.reverse(Arrays.asList(lines));

        intMap = new int[lines[0].length()][];
        objMap = new HashSet[lines[0].length()][];

        for (int i = 0; i < lines[0].length(); i++) {
            intMap[i]= new int[lines.length];
            objMap[i] = new HashSet[lines.length];

            for (int j = 0; j < lines.length ; j++) {
                intMap[i][j] = Integer.parseInt(String.valueOf(lines[j].charAt(i)));
                objMap[i][j] = new HashSet<Actor>();
            }
        }

    }

    /**
     * Determines whether a map tile empty and whether can this be walked into
     * @param x The x cordinate relative to the map
     * @param y The y cordinate relative to the map
     * @return boolean
     */
    public boolean Empty(int x, int y){
        if (InBounds(x,y)){
            return intMap[x][y] != WALL;
        } else{
            return false;
        }
    }

    /**
     * Determines whether a map tile empty and whether can this be walked into
     * @param x the x coordinate of rectangle relative to the world
     * @param y the x coordinate of rectangle relative to the world
     * @param w width of rectangle
     * @param h height of rectangle
     * @return boolean if the square can be walked into
     */
    public boolean Empty(float x, float y, float w, float h){

        for (int i = gridPos(x); i <= gridPos(x + w); i++) {
            for (int j = gridPos(y); j <= gridPos(y + h); j++) {
                if (!Empty(i,j)){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * return true if every tile the cordinates rectangle are in is a effect tile
     *
     * @param effect the effect you are looking for e.g 2 for heal. (set in class variables)
     * @param x the x coordinate of rectangle relative to the world
     * @param y the x coordinate of rectangle relative to the world
     * @param w width of rectangle
     * @param h height of rectangle
     * @return boolean if the rectangle is in a effect square
     */
    public boolean Effect(int effect,float x, float y, float w, float h){//
        for (int i = gridPos(x); i <= gridPos(x + w); i++) {
            for (int j = gridPos(y); j <= gridPos(y + h); j++) {
                if (intMap[i][j] != effect){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * return true if every tile an actor are in is a effect tile
     *
     * @param effect the effect you are looking for e.g 2 for heal. (set in class variables)
     * @param actor An actor to test
     * @return boolean if the actor is in a effect square
     */
    public boolean Effect(int effect,Actor actor){//
        float x = actor.getX();
        float y = actor.getY();
        float w = actor.getWidth();
        float h = actor.getHeight();
        return Effect(effect,x,y,w,h);
    }

    /**
     * return a set of all entities in that tile overlapping that area
     *
     * @param x the x coordinate relative to the map
     * @param y the y coordinate relative to the map
     * @return returns a hash set of all the actors at the coordinates
     */
    public Set<Actor> GetEnts(int x, int y){
        if (InBounds(x,y)){
            return objMap[x][y];
        } else{
            return new HashSet<Actor>();
        }

    }

    /**
     * return a set of all entities in tiles overlapping that area
     *
     * @param x the x coordinate of rectangle relative to the map
     * @param y the x coordinate of rectangle relative to the map
     * @param w width of rectangle
     * @param h height of rectangle
     * @return returns a hash set of all the actors at the coordinates
     */
    public Set<Actor> GetEnts(float x, float y, float w, float h){
        Set<Actor> ents = new HashSet<Actor>();
        for (int i = gridPos(x); i <= gridPos(x + w); i++) {
            for (int j = gridPos(y); j <= gridPos(y + h); j++) {
                if (InBounds(i, j)){
                    ents.addAll(GetEnts(i,j));
                }
            }
        }
        return ents;
    }

    /**
     * return a set of entities in the given area
     *
     * @param x the x coordinate of rectangle relative to the map
     * @param y the x coordinate of rectangle relative to the map
     * @param w width of rectangle
     * @param h height of rectangle
     * @return returns a set of entities in the given area
     */
    public Set<Actor> InArea(float x, float y, float w, float h){
        return InArea(GetEnts(x,y,w,h),x,y,w,h);
    }

    /**
     * return a set of entities in the given area from the given set
     *
     * @param entities A set of actors
     * @param x the x coordinate of rectangle relative to the map
     * @param y the x coordinate of rectangle relative to the map
     * @param w width of rectangle
     * @param h height of rectangle
     * @return returns a set of entities in the given area from the given set
     */
    public Set<Actor> InArea(Set<Actor> entities, float x, float y, float w, float h){
        Set<Actor> ents = new HashSet<Actor>();
        for (Actor ent : entities) {
            float x2 = ent.getX();
            float y2 = ent.getY();
            float w2 = ent.getWidth();
            float h2 = ent.getHeight();
            if (!(x >= x2 + w2 || x2 >= x + w || y >= y2 + h2 || y2 >= y + h)) {
                ents.add(ent);
            }
        }
        return ents;
    }

    /**
     * return a set of all entities the given entity is touching
     *
     * @param entity the actor to check
     * @return a set of all actors
     */
    public Set<Actor> Touching(Actor entity){
        float x = entity.getX();
        float y = entity.getY();
        float w = entity.getWidth();
        float h = entity.getHeight();
        return InArea(x,y,w,h);
    }

    /**
     *  Move the player into a tile at coordinates (x,y)
     *
     * @param x The x coordinate of the actor relative to the map
     * @param y The y coordinate of the actor relative to the map
     * @param actor The actor to move into the tile
     */
    public void Enter(int x, int y,Actor actor){
        objMap[x][y].add(actor);
    }

    /**
     *  Move the player out of a tile at cordinates (x,y)
     *
     * @param x The x coordinate of the actor relative to the map
     * @param y The y coordinate of the actor relative to the map
     * @param actor The actor to move into the tile
     */
    public void Leave(int x, int y,Actor actor){
        objMap[x][y].remove(actor);
    }


    /**
     * converts position in pixels to position in grid
     *
     * @param worldPos the coordinate relative to the world
     * @return An integer of the grid coordinate relative to the map
     */
    public int gridPos(float worldPos){
        return (int) worldPos /properties.get("tilewidth", Integer.class);
    }

    /**
     * Converts a world position into a grid position
     *
     * @param gridPos the coordinate relative to the map
     * @return An a float of the grid coordinate relative to the world
     */
    public float worldPos(float gridPos){
        return gridPos*properties.get("tilewidth", Integer.class);
    }

    /**
     * Leave all required tiles for this actor
     *
     * @param entity The actor
     */
    public void autoLeave(Actor entity){
        float x = entity.getX();
        float y = entity.getY();
        float w = entity.getWidth();
        float h = entity.getHeight();
        autoLeave(entity,x,y,w,h);
    }
    /**
     *leave all required tiles for the given area
     *
     * @param entity The actor
     * @param x the x coordinate of rectangle relative to the map
     * @param y the x coordinate of rectangle relative to the map
     * @param w width of rectangle
     * @param h height of rectangle
     */
    public void autoLeave(Actor entity,float x, float y, float w, float h){//
        Leave(gridPos(x),gridPos(y),entity);
        Leave(gridPos(x),gridPos(y + h),entity);
        Leave(gridPos(x + w),gridPos(y),entity);
        Leave(gridPos(x + w),gridPos(y + h),entity);
    }
    /**
     * enter all required tiles for this actor
     *
     * @param entity The actor
     */
    public void autoEnter(Actor entity){//
        float x = entity.getX();
        float y = entity.getY();
        float w = entity.getWidth();
        float h = entity.getHeight();
        autoEnter(entity,x,y,w,h);
    }
    /**
     *enter all required tiles for the given area
     *
     * @param entity The actor
     * @param x the x coordinate of rectangle relative to the map
     * @param y the x coordinate of rectangle relative to the map
     * @param w width of rectangle
     * @param h height of rectangle
     */
    public void autoEnter(Actor entity,float x, float y, float w, float h){//
        Enter(gridPos(x),gridPos(y),entity);
        Enter(gridPos(x),gridPos(y + h),entity);
        Enter(gridPos(x + w),gridPos(y),entity);
        Enter(gridPos(x + w),gridPos(y + h),entity);
    }

    /**
     * Works out if a coordinate is within the map
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @return True if it is, False if its not
     */
    private boolean InBounds(int x, int y){
        return 0 <= x && x < intMap.length && 0 <= y && y < intMap[0].length;
    }



}
