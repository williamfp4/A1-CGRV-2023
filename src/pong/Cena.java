package pong;

import java.util.Random;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JOptionPane;

// import com.jogamp.graph.font.Font;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;
import java.awt.Color;
import java.awt.Font;
/**
 *
 * @author William Franz
 */
public class Cena implements GLEventListener {    
    private float xMin, xMax, yMin, yMax, zMin, zMax, ballX, ballY,points,playerLife;
    public float movePaddle, ballVelX, ballVelY, ballSpeed, colorR, colorG, colorB, mouseX, larguraFrame, alturaFrame;
    private TextRenderer textRenderer;

    Paddle pad = new Paddle(-1.5f, 1.5f, 0.2f);
    Random rn = new Random();
    GLU glu;
        
    @Override
    public void init(GLAutoDrawable drawable) {
        glu = new GLU();
       
        textRenderer = new TextRenderer(new Font("Comic Sans MS Negrito", Font.PLAIN, 15));
        points = 0;
playerLife = 3;
        //Estabelece as coordenadas do SRU (Sistema de Referencia do Universo)
        xMin = yMin = zMin = -8;
        xMax = yMax = zMax = 8;
        movePaddle = 0;
        colorR = 0.5f;
        colorG = colorB = 0f;
        ballX = ballY = -0.05f;
        ballSpeed = ballVelX = ballVelY = 0.125f;
    }

    @Override
    public void display(GLAutoDrawable drawable) {  
        GLUT glut = new GLUT();
        GL2 gl = drawable.getGL().getGL2();       
        gl.glClearColor(0, 0, 0, 0);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);       
        gl.glLoadIdentity(); 
        // REVER O POLYGON MODE NAS PRÓXIMAS IMPLEMENTAÇÕES
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

        drawGame(gl,glut);

        gl.glFlush();
    }

    private void drawGame(GL2 gl, GLUT glut){
        String filepath = "ball.wav";
        float xL = pad.getxLeft();
        float xR = pad.getxRight();
        float y = pad.getY();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
        textRenderer.setColor(Color.BLUE);
        textRenderer.draw( Float.toString(points), 0, 0);
        textRenderer.endRendering();
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
        textRenderer.setColor(Color.BLUE);
        String lifeText = Float.toString(playerLife) + " \u2665";
        textRenderer.draw(lifeText, 0, 10);
        textRenderer.endRendering();

        float scale = 1280 / 20.0f;

        gl.glColor3f(1f, 0f, 0f); 
        gl.glBegin(GL2.GL_POINTS);
        
        for (float t = 0.0f; t <= 2 * Math.PI; t += 0.01f) {
            float x = 16 * (float) Math.pow(Math.sin(t), 3);
            float yt = 13 * (float) Math.cos(t) - 5 * (float) Math.cos(2*t) - 2 * (float) Math.cos(3*t) - (float) Math.cos(4*t);
            gl.glVertex2d(x / scale, yt / scale);
        }
        
        gl.glEnd();
        gl.glColor3f(0.0f,0.5f,0.5f);
        gl.glPushMatrix();
            gl.glTranslatef(movePaddle, -6.0f, 0.0f);
            gl.glBegin(GL2.GL_QUADS);
                gl.glVertex2f(xL, -y);
                gl.glVertex2f(xR, -y);
                gl.glVertex2f(xR, y);
                gl.glVertex2f(xL, y);
            gl.glEnd();
        gl.glPopMatrix();
        if(playerLife<=0){
            textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);       
            textRenderer.setColor(Color.BLUE);
            textRenderer.draw( "Você perdeu!", 500, 500);
            textRenderer.endRendering();
            // System.exit(0);
        }

        if (ballX >= 14.2f || ballX <= -14.2f) {
            ballVelX = -ballVelX;
            PlayMusic(filepath);
        }
        if (ballY >= 8.0f) {
            ballVelY = -ballVelY;
            points += 100;
            PlayMusic(filepath);
        }
        else if (ballY < -8.9f)
        {
            playerLife -= 1;
            ballX = 0f; ballY = 5f;
            ballSpeed = 0.125f;
            ballVelX = -ballVelX;
        }
        else if ((ballY >= -6.15f && ballY <= -5.8f) && (ballX >= xL + movePaddle && ballX <= xR + movePaddle)) 
        {
            // Cálculo da posição relativa do impacto da bola
            float positionHit = (ballX - (xL + movePaddle)) / (xR - xL);
            // Aumentar a velocidade após atingir a bola
            ballSpeed = 0.2f;
            // Aqui fazemos o cálculo do Ângulo de Reflexão com base no impacto da bola
            float bounceAngle = (float) (Math.PI * (positionHit)) * -1;

            /* ADICIONAR INFORMAÇÕES AO README.md:
            Esta seção limita os ângulos a serem obtidos para maiores que -17° e menores que -154°
            melhorando o desempenho das trajetórias, evitando estagnar o jogo.
            https://www.rapidtables.com/convert/number/radians-to-degrees.html */
            if (bounceAngle < -2.7f) 
                bounceAngle = -2.7f;
            else if (bounceAngle > -0.3f)
                bounceAngle = -0.3f;

            // Utilizando o cosseno do ângulo, decompomos a velocidade horizontal (Eixo X)
            ballVelX = ballSpeed * (float) Math.cos(bounceAngle);
            // Utilizando o seno do ângulo, decompomos a velocidade vertical (Eixo Y)
            ballVelY = -ballSpeed * (float) Math.sin(bounceAngle);
        }   

        gl.glColor3f(colorR,colorG,colorB);
        gl.glTranslatef(ballX, ballY, 0.0f);
        gl.glPushMatrix();
            glut.glutSolidSphere(0.2f, 70, 50);
        gl.glPopMatrix();

        ballX -= ballVelX;
        ballY += ballVelY;
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {    
        GL2 gl = drawable.getGL().getGL2();  
        
        if(height == 0) height = 1;
        float aspect = (float) width / height;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);      
        gl.glLoadIdentity(); 

        if(width >= height)            
            gl.glOrtho(xMin * aspect, xMax * aspect, yMin, yMax, zMin, zMax);
        else        
            gl.glOrtho(xMin, xMax, yMin / aspect, yMax / aspect, zMin, zMax);
                
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        System.out.println("Reshape: " + width + ", " + height);

        larguraFrame = width;
        alturaFrame = height;
    }    
       
    @Override
    public void dispose(GLAutoDrawable drawable) {}       
    
    public static void PlayMusic(String location){
        try {
            File musicPath = new File(location);

            if(musicPath.exists()){
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.start();
            }else{
                System.out.println("Arquivo não encontrado");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
