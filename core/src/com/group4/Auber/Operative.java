package com.group4.Auber;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.audio.Sound;
import com.group4.Auber.HUD.HUD;
import com.group4.Auber.OperativeAI.GridGraph;
import com.group4.Auber.OperativeAI.GridNode;
import java.lang.Math;
import java.util.ArrayList;

/**
 * Creates an operative that goes to the systems and attacks them. It will also fight the player if attacked.
 *
 * @author Adam Wiegand
 * @author Bogdan Bodnariu-Lescinschi
 */
public class Operative extends Actor {
  public static AuberGame game;
  private MapRenderer map;
  private Systems target;
  public static GridGraph pathfinder;
  private GraphPath<GridNode> currentPath;
  private int nodeNum;
  private HUD hud;

  /**
   * The number of remaining operative that are alive
   */
  public static int remainingOpers = 0;

  /**
   * A flag as to whether the operative is currently hacking a system
   */
  private boolean isHacking = false;

  /**
   * A flag as to whether the operative is currently in combat with the player
   */
  private boolean combat = false;

  /**
   * The targets that have not been attacked already
   */
  private static ArrayList<Systems> untargetedSystems = new ArrayList<Systems>();;

  /**
   *  used as a timer
   */
  private int delay = 0;

  /**
   * the health of the operative
   */
  private int health = 100;

  /**
   * used to make the hitbox center on the image
   */
  private float hitboxOffset = 6;

  /**
   * Set the speed of the player
   */
  private final float moveSpeed = 1.2f;

  /**
   * the sound that is made when the operative has been attacked by the player
   */
  private final Sound metalDeath = Gdx.audio.newSound(Gdx.files.internal("audio/metalDeath.mp3"));

  /**
   * The image of the operative
   */
  private final Texture image = new Texture(Gdx.files.internal("img/operative.png"));

  /**
   * The background to the operative when attacking
   */
  private final Texture imageAttack = new Texture(Gdx.files.internal("img/operative_attack.png"));

  /**
   * is this operative dead?
   */
  private boolean dead = false;

  /**
   * Create the operative at starting point
   *
   * @param x The X coordinate of the starting position
   * @param y The Y coordinate of the starting position
   * @param map The map
   * @param hud the HUD
   */
  public Operative(int x, int y, MapRenderer map, HUD hud) {
    this.map = map;
    this.hud = hud;

    //Create the path finder
    if (pathfinder == null){pathfinder = new GridGraph(map,x,y);}

    //Add all the systems to untargetedSystems, if this is the first operative made
    if (remainingOpers == 0){untargetedSystems.addAll(Systems.systemsRemaining);}

    remainingOpers += 1;
    setBounds(map.worldPos(x), map.worldPos(y),20f,20f);
    chooseTarget();
  }

  /**
   * Select a system to attack
   */
  public void chooseTarget() {
    //Make sure their are still systems to attack
    if (untargetedSystems.size() == 0){
      //End the game if there are none left
      if (Systems.systemsRemaining.size() == 0){
        game.setScreen(new GameEndScreen(game, false));
      } else{
        target = Systems.systemsRemaining.get((int) Math.round(Math.random() * (Systems.systemsRemaining.size() - 1)));
      }
    } else{
      target = untargetedSystems.get((int) Math.round(Math.random() * (untargetedSystems.size() - 1)));
      untargetedSystems.remove(target);
    }
    currentPath = pathfinder.findPath(map.gridPos(getX()),map.gridPos(getY()), target.gridX,target.gridY);
    nodeNum = 0;
  }

  @Override
  public void draw(Batch batch, float parentAlpha) {
    if (dead){//prevents problems caused by race conditions (where they are killed and then draw is run)
      return;
    }
    //If the operative is hacking
    if (isHacking){
      if (target.health <= 0){//reached an already killed system
        isHacking = false;
        chooseTarget();
      } else{
        //delay == A - 1, A is the number of frames an oponent must spend hacking to damage the system
        if (delay == 18 - 1){

          //damage dealt per A frames
          target.onHit(this, 1);

          batch.draw(imageAttack,getX() - hitboxOffset,getY() - hitboxOffset,32,32);

          //is the target dead
          if (target.health <= 0){
            isHacking = false;
            chooseTarget();
          }
          delay = 0;
        } else{
          delay += 1;
        }
      }
    }

    //is the player in combat
    else if (combat){
      //attack?
      Player player = null;
      for (Actor thing : map.InArea(getX() - hitboxOffset,getY() - hitboxOffset,31,31)) {
        if (thing instanceof Player && delay == 0){
          player = (Player) thing;
          player.onHit(this,15);
          batch.draw(imageAttack,getX() - hitboxOffset,getY() - hitboxOffset,32,32);
          delay = 60;
          break;//only one player
        }
      }
      if (delay > 0){delay -= 1;}
      if (player == null){
        //check if player still nearby
        float size = 32*10;
        for (Actor thing : map.InArea(getX() + getWidth()/2 - size/2 - hitboxOffset,getY() + getHeight()/2 - size/2 - hitboxOffset,size,size)) {
          if (thing instanceof Player){
            player = (Player) thing;
            break;//only one player
          }
        }
        if (player == null){//end combat
          combat = false;
          delay = 0;
          chooseTarget();
        } else {//player still nearby
          //if (true){return;} //uncomment to kneecap them
          //move
          if (nodeNum >= currentPath.getCount()){
            currentPath = pathfinder.findPath(map.gridPos(getX()),map.gridPos(getY()), map.gridPos(player.getX()),map.gridPos(player.getY()));
            nodeNum = 0;
          }
          move();
        }
      }
    }
    else{
      //If the player is not in combat or attacking a system it must be moving
      move();

      //Check if we should start hacking
      if (getX() - hitboxOffset == target.getX() && getY() - hitboxOffset == target.getY()){
        isHacking = true;
      }
    }
    // Draw the image
    batch.draw(image, getX() - hitboxOffset, getY() - hitboxOffset, image.getWidth(), image.getHeight());
  }

  /**
   * Move the operative based on the path finder
   */
  private void move(){
    //Get the path
    GridNode curNode = currentPath.get(nodeNum);
    float xdif = map.worldPos(curNode.x) - getX() + hitboxOffset;
    float ydif = map.worldPos(curNode.y) - getY() + hitboxOffset;

    //Find the amount to move based on the speed
    float deltaX;
    float deltaY;
    if (xdif >= 0){
      deltaX = Math.min(moveSpeed,xdif);
    } else{
      deltaX = Math.max(-moveSpeed,xdif);
    }
    if (ydif >= 0){
      deltaY = Math.min(moveSpeed,ydif);
    } else{
      deltaY = Math.max(-moveSpeed,ydif);
    }

    // Check the space is empty before moving into it
    if (map.Empty(getX() + deltaX, getY() + deltaY, getWidth(), getHeight())) {
      map.autoLeave(this,getX(),getY(), getWidth(), getHeight());
      moveBy(deltaX, deltaY);
      map.autoEnter(this,getX(),getY(), getWidth(), getHeight());
    } else {
      throw new RuntimeException("Path finding error");
    }
    if (getX() - hitboxOffset == map.worldPos(curNode.x) && getY() - hitboxOffset == map.worldPos(curNode.y)){//next node
      nodeNum += 1;
    }
  }

  /**
   * The operative has been attacked so decreases the health
   *
   * @param by the actor that attacked the operative
   * @param amount the amount to reduce the health by
   */
  public void onHit(Actor by,int amount) {
    if (by instanceof Player){
      //Stop the operative hacking and move into combat mode
      isHacking = false;
      untargetedSystems.add(target);
      delay = 0;
      if (combat == false){//combat just entered
        currentPath = pathfinder.findPath(map.gridPos(getX()),map.gridPos(getY()), map.gridPos(by.getX()),map.gridPos(by.getY()));
      }
      combat = true;
      nodeNum = 0;

      //Reduce the health if the player, and make sure the operative is not dead
      health -= amount;
      if (health <= 0) {
        onDeath();
      }
    }
  }

  /**
   * Called when the operative dies
   */
  public void onDeath(){
    dead = true;
    map.autoLeave(this,getX(),getY(), getWidth(), getHeight());
    remainingOpers -= 1;

    //so its .draw() isn't called
    remove();
    image.dispose();

    metalDeath.play(0.3f);
    if (remainingOpers == 0){
      game.setScreen(new GameEndScreen(game, true));
    }

    //Add a success notification in the heads up display
    hud.successNotification("You apprehended an operative.");
  }
}