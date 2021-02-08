package com.team1.Auber.HUD;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Creates a scrolling notification window that can have notifications added to it in diffrent colours.
 *
 * @author Robert Watts (Team 4)
 */
public class NotificationWindow extends ScrollPane {
    protected float width;
    protected float height;
    protected Table table = new Table();
    protected BitmapFont font;

    /**
     * The padding in each cell of the table
     */
    final int tablePadding = 5;

    /**
     * The background colour of the table
     */
    protected final Color bgColour = new Color(0,0,0,0.5f);

    /**
     * The text colour of the timestamp
     */
    protected final Color timestampColor = new Color(1,1,1,1);

    /**
     * The notification sound. This is played when a notification is added to the window.
     */
    protected final Sound notificationSound = Gdx.audio.newSound(Gdx.files.internal("audio/notification.mp3"));

    /**
     * Construct the scroll pane, adding the event listener for the mouse input events for scrolling.
     *
     * @param height height of the window
     * @param width width of the window
     * @param font font of the window
     */
    public NotificationWindow(float height, float width, BitmapFont font) {
        super(null);
        setSize(width, height);
        this.height = height;
        this.width = width;
        this.font = font;

        //Set the bg and variables
        table.setBackground(generateTableTexture(bgColour));
        setActor(table);

        //Set Scroll Functions and listeners
        this.setForceScroll(false, true);
        this.setFlickScroll(true);
        this.setOverscroll(false, false);
        addListener(new InputListener() {
            //When mouse enters, set the scroll focus and when it leaves remove the scroll focus
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                getStage().setScrollFocus(NotificationWindow.this);
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                getStage().setScrollFocus(null);
            }
        });
    }

    /**
     * Create the scroll pane with the LibGDX default font from {@link com.badlogic.gdx.graphics.g2d.BitmapFont}.
     *
     * @param height height of the window
     * @param width width of the window
     */
    public NotificationWindow(float height, float width) {
        this(height,width,new BitmapFont());
    }

    /**
     * Add a white notification
     *
     * @param text the notification text (string)
     */
    public void addNotification(String text) {
        addNotification(text, Color.WHITE);
    }

    /**
     * Add a notification of a specified colour
     *
     * @param text the notification text (string)
     * @param color A colour object of the colour that the text should be
     */
    public void addNotification(String text, Color color) {
        //Add the timestamp row
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        Label timestamp = new Label("[" + dateFormatter.format(now) + "]", new Label.LabelStyle(font, timestampColor));
        table.add(timestamp).top().left().pad(tablePadding );

        //Create the notification and container (the container is used for text wrapping)
        Label notification = new Label(text,new Label.LabelStyle(font, color));
        notification.setWrap(true);
        Container container = new Container(notification);
        container.width(width - 4*tablePadding - 2*timestamp.getPrefWidth());

        //Add to table and get a new row
        table.add(container).center().left().expandX().pad(tablePadding).padBottom(2*tablePadding );
        table.row();

        //Play sound + scroll to bottom to display the new message
        if(! com.team1.Auber.AuberGame.isGameMuted){
            notificationSound.play(0.13f);
        }

        this.scrollTo(0, 0, 0, 0);
    }

    /**
     * Generate a texture for the table (used for the background)
     *
     * @param color the background colour as a Colour object
     * @return a TextureRegionDrawable object
     */
    private TextureRegionDrawable generateTableTexture(Color color){
        Pixmap bgPixmap = new Pixmap(1,1, Pixmap.Format.Alpha);
        bgPixmap.setColor(new Color(0,0,0,0.5f));
        bgPixmap.fill();
        bgPixmap.setBlending(Pixmap.Blending.SourceOver);
        return new TextureRegionDrawable(new TextureRegion(new Texture(bgPixmap)));
    }
}
