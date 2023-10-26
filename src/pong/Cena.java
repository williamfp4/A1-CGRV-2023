package pong;

import java.util.Random;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
/**
 *
 * @author William Franz
 */
public class Cena implements GLEventListener{    
    private float xMin, xMax, yMin, yMax, zMin, zMax, ballX, ballY;
    public float angle, lower, movePaddle, ballAccelX, ballAccelY, colorR, colorG, colorB, mouseX, larguraFrame, alturaFrame;
    Paddle pad = new Paddle(-1.5f, 1.5f, 0.2f);
    Random rn = new Random();
    GLU glu;
        
    @Override
    public void init(GLAutoDrawable drawable) {
        glu = new GLU();
        //Estabelece as coordenadas do SRU (Sistema de Referencia do Universo)
        xMin = yMin = zMin = -8;
        xMax = yMax = zMax = 8;
        lower = movePaddle = angle = 0;
        colorR = 0.5f;
        colorG = colorB = 0f;
        ballX = -0.05f;
        ballY = -0.05f;
        ballAccelX = 0.1f;
        ballAccelY = 0.1f;
    }

    @Override
    public void display(GLAutoDrawable drawable) {  
        GLUT glut = new GLUT();
        GL2 gl = drawable.getGL().getGL2();       
        gl.glClearColor(0, 0, 0, 0);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);       
        gl.glLoadIdentity(); 
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

        drawGame(gl,glut);

        gl.glFlush();
    }

    private void drawGame(GL2 gl, GLUT glut){
        float xL = pad.getxLeft();
        float xR = pad.getxRight();
        float y = pad.getY();
        float num = rn.nextFloat() * 5f;
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

        if (ballY >= 8.0f) {
            ballAccelY = -ballAccelY;
        }else if (ballY < -8.9f){
            ballX = 0f; ballY = 5f;
            gl.glTranslatef(ballX, ballY, 0.0f);
            ballAccelX = -ballAccelX;
        }
        if (ballX >= 14.2f || ballX <= -14.2f) {
            ballAccelX = -ballAccelX;
        }
        if ((ballY >= -6.0f && ballY <= -5.8f) && (ballX >= xL+movePaddle && ballX <= xR+movePaddle)){
            colorR = num;
            num = rn.nextFloat() * 5f;
            colorG = num;
            num = rn.nextFloat() * 5f;
            colorB = num;
            ballAccelY = -ballAccelY;
        }       

        gl.glColor3f(colorR,colorG,colorB);
        gl.glTranslatef(ballX, ballY, 0.0f);
        gl.glPushMatrix();
            gl.glRotated(0, 0, 5, 1);
            glut.glutSolidSphere(0.2f, 70, 50);
        gl.glPopMatrix();

        ballX -= ballAccelX;
        ballY += ballAccelY;
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
}
