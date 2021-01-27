package com.team1.Auber.Smoke;

import com.team1.Auber.Player;

import java.security.Provider;

import javax.print.FlavorException;
import javax.swing.UIDefaults.ProxyLazyValue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.decorator.Random;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.FloatTextureData;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.FloatArray;


/**
 * A simple smoke particle system to display smoke
 * @author Jamie Hewison
 * 
 */
public class Smoke extends Actor {

    private float xVel,yVel;
    private float xRate = 0.9f;
    private float yRate = 0.95f;
    private float time;
    private float ttl = 15; //TimeToLive
    private Texture texture;
    private float alpha = 1;
    private java.util.Random rand;

    public Smoke(float x , float y , float xVel, float yVel){
        texture = new Texture(Gdx.files.internal("img/smoke.png"));
        this.setX(x);
        this.setY(y);
        this.xVel = xVel;
        this.yVel = yVel;
        rand = new java.util.Random();
        size = (int)(rand.nextFloat()*size);
        ttl = rand.nextFloat()*ttl;
        xVel = xVel + 5*(1-2*rand.nextFloat());
    }


    int size = 50;
    float delta;
    Color c;
    public void draw(Batch batch, float parentAlpha) {
        delta = Gdx.graphics.getDeltaTime();
        time += delta;
        if(time>=ttl){
            this.remove();
        }

        xVel = xVel*(1-xRate*delta);
        yVel = yVel*(1-yRate*delta);
        setX(getX()+xVel*delta);
        setY(getY()+yVel*delta);

        alpha = alpha*(1-time/ttl);

        //Draw the smoke
        c = batch.getColor();
        batch.setColor(c.r, c.g, c.b, alpha);//set alpha to 0.3
        batch.draw(texture, getX(), getY(), (float)(size*Math.log(time+1)),(float)(size*Math.log(time+1)));
        batch.setColor(c.r, c.g, c.b, 1f);//set alpha to 1
    }
}
