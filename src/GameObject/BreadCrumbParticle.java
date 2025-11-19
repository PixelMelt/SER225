package GameObject;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

public class BreadCrumbParticle {

    public float x, y;
    private float velocityX, velocityY;
    private int life;
    private static final Random rand = new Random();

    public BreadCrumbParticle(float startX, float startY) {
        this.x = startX;
        this.y = startY;

        // random horizontal velocity
        this.velocityX = (rand.nextFloat() - 0.5f) * 1.5f;
        // random downward velocity
        this.velocityY = rand.nextFloat() * 2 + 1;

        // life in frames before disappearing
        this.life = 30 + rand.nextInt(20); 
    }

    // updates the particles 
    
    public boolean update() {
        x += velocityX;
        y += velocityY;

        velocityX *= 0.95f;

        life--;
        return life <= 0;
    }

    // draw
    public void draw(Graphics g, int offsetX, int offsetY) {
        g.setColor(new Color(139, 69, 19)); // brown color 
        g.fillRect(Math.round(x) - offsetX, Math.round(y) - offsetY, 2, 2);
    }
}
