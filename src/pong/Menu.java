package pong;

import com.jogamp.opengl.GL2;
import javax.imageio.ImageIO;

import java.awt.Font;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class Menu {
    private static Cena cena;
    static Color[] color = {Color.WHITE, Color.WHITE, Color.WHITE};
    private static BufferedImage image;
    private static TextRenderer textRenderer;
    private static Texture texture;
    public static boolean gameStart, seeRules;

    public Menu(Cena cena){
        Menu.cena = cena;
    }

    public static void showMenu(GL2 gl) throws IOException{
        gameStart = false;

        gl.glColor4f(0, 1, 1, 0.2f);
        gl.glBegin(GL2.GL_QUADS);
            gl.glVertex2f(cena.xMin*cena.aspect, cena.yMin);
            gl.glVertex2f(cena.xMax*cena.aspect, cena.yMin);
            gl.glVertex2f(cena.xMax*cena.aspect, cena.yMax);
            gl.glVertex2f(cena.xMin*cena.aspect, cena.yMax);
        gl.glEnd();

        gl.glColor4f(0, 1, 1, 0.15f);
        gl.glBegin(GL2.GL_QUADS);
            gl.glVertex2f(-6, -6);
            gl.glVertex2f(-6, 3);
            gl.glVertex2f(6, 3);
            gl.glVertex2f(6, -6);
        gl.glEnd();

        image = ImageIO.read(new File("A1-CGRV-2023/images/title.png"));
        texture = AWTTextureIO.newTexture(gl.getGLProfile(), image, false);
        gl.glPushMatrix();
            texture.enable(gl);
            texture.bind(gl);
            gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
            gl.glTranslatef(0, 11f, 0);
            gl.glColor4f(1, 1, 1, 1f);
            gl.glBegin(GL2.GL_QUADS);
                gl.glTexCoord2f(0, 0); gl.glVertex2f(-5, -5);
                gl.glTexCoord2f(1, 0); gl.glVertex2f(5, -5);
                gl.glTexCoord2f(1, 1); gl.glVertex2f(5, -7);
                gl.glTexCoord2f(0, 1); gl.glVertex2f(-5, -7);
            gl.glEnd();
            texture.disable(gl);
        gl.glPopMatrix();

        gl.glColor4f(0, 1, 1, 0.15f);
        gl.glBegin(GL2.GL_QUADS);
            gl.glVertex2f(-6, 0);
            gl.glVertex2f(-6, 3);
            gl.glVertex2f(6, 3);
            gl.glVertex2f(6, 0);
        gl.glEnd();
        drawText(gl,"Iniciar", color[0], 30, 605, 420);
        
        gl.glColor4f(0, 1, 1, 0.3f);
        gl.glBegin(GL2.GL_QUADS);
            gl.glVertex2f(-6, -3);
            gl.glVertex2f(-6, 0);
            gl.glVertex2f(6, 0);
            gl.glVertex2f(6, -3);
        gl.glEnd();
        drawText(gl,"Regras", color[1], 30, 595, 285);

        gl.glColor4f(0, 1, 1, 0.5f);
        gl.glBegin(GL2.GL_QUADS);
            gl.glVertex2f(-6, -6);
            gl.glVertex2f(-6, -3);
            gl.glVertex2f(6, -3);
            gl.glVertex2f(6, -6);
        gl.glEnd();
        drawText(gl,"Sair", color[2], 30, 612, 150);
    }

    public static void drawText(GL2 gl, String text, Color color, int size, int x, int y) {
        textRenderer = new TextRenderer(new Font(Font.SANS_SERIF, Font.BOLD, size));
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
        textRenderer.setColor(color);
        textRenderer.draw(text, x, y);
        textRenderer.endRendering();  
    }
}
