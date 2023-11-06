package pong;
import java.util.Random;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
/**
 *
 * @author William Franz
 */
public class Cena implements GLEventListener {    
    public float movePaddle, ballVelX, ballVelY, ballSpeed, mouseX, root, larguraFrame, alturaFrame;
    public int pkmnHealth, gen;
    private float xMin, xMax, yMin, yMax, zMin, zMax, aspect, ballX, ballY;
    private String ballSound;
    private String[] background = new String[]{"A1-CGRV-2023/images/kanto.jpg","A1-CGRV-2023/images/johto.jpg","A1-CGRV-2023/images/hoenn.jpg","A1-CGRV-2023/images/sinnoh.jpg","A1-CGRV-2023/images/unova.jpg"};
    private String[] music = new String[]{"A1-CGRV-2023/audio/r1Kanto.wav","A1-CGRV-2023/audio/r101.wav","A1-CGRV-2023/audio/r29.wav","A1-CGRV-2023/audio/r201.wav","A1-CGRV-2023/audio/r1Unova.wav"};
    private Clip soundTrack, ball;
    private boolean levelChange;
    BufferedImage image;
    Paddle pad = new Paddle(-1.5f, 1.5f, 0.2f);
    Random rn = new Random();
    Texture texture, scene;
    GLU glu;
        
    @Override
    public void init(GLAutoDrawable drawable) {
        glu = new GLU();
        texture = new Texture(pkmnHealth);
        //Estabelece as coordenadas do SRU (Sistema de Referencia do Universo)
        xMin = yMin = zMin = -8;
        xMax = yMax = zMax = 8;
        movePaddle = 0;
        ballX = ballY = -0.05f;
        ballSpeed = ballVelX = ballVelY = 0.125f;
        pkmnHealth = 0;
        gen = 1;
        root = (float) xMax * aspect;
        ballSound = "A1-CGRV-2023/audio/ball.wav"; // Kanto = kanto1; Johto = kanto 101; Hoenn = kanto 29; Sinnoh = kanto 201
        try {
            soundTrack = playSound(music[0],0.0f);
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void display(GLAutoDrawable drawable) {  
        GLUT glut = new GLUT();
        GL2 gl = drawable.getGL().getGL2();      
        gl.glClearColor(0, 0, 0, 0);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);       
        gl.glLoadIdentity(); 
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        // REVER O POLYGON MODE NAS PRÓXIMAS IMPLEMENTAÇÕES
        //gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

        try {
            ball = playSound(ballSound, -10.0f);
            if(pkmnHealth == 0) encounter(gl);
            if(levelChange) changeLevel();
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
        
        drawScene(gl);
        drawPkmn(gl);
        drawGame(gl,glut);
    
        gl.glFlush();
    }

    private void encounter(GL2 gl) throws IOException {
        int num = 0;
        switch (gen) {
            case 1: num = rn.nextInt(151) + 1; break; //Kanto
            case 2: num = rn.nextInt(100) + 152; break; //Johto
            case 3: num = rn.nextInt(135) + 252; break; //Hoenn
            case 4: num = rn.nextInt(107) + 387; break; //Sinnoh
            case 5: num = rn.nextInt(156) + 494; break; //Sinnoh
            default: num = rn.nextInt(649) + 1; break;
        }
        texture = getPokeImage(gl, num);
        pkmnHealth = 100;
        levelChange = true;
    }

    private Texture getPokeImage(GL2 gl, int dexNumber) throws IOException {
        String getPkmn = "https://pokeapi.co/api/v2/pokemon/" + dexNumber + "/";
        URL url = new URL(getPkmn);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();

        String json = response.toString();
        String imageUrl = json.split("\"front_default\":\"")[1].split("\"")[0];

        URL imgURL = new URL(imageUrl);
        BufferedImage img = ImageIO.read(imgURL);

        Texture texture = AWTTextureIO.newTexture(GLProfile.getDefault(), img, true);

        return texture;
    }

    private void drawPkmn(GL2 gl){
        gl.glPushMatrix();
            texture.enable(gl);
            texture.bind(gl);
            gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
            gl.glRotatef(180, 1, 0, 0);
            gl.glTranslatef(9, -5, 0);
            gl.glColor4f(0, 0, 0, 1f);
            gl.glBegin(GL2.GL_QUADS);
                gl.glTexCoord2f(0, 0); gl.glVertex2f(-3, -3);
                gl.glTexCoord2f(1, 0); gl.glVertex2f(3, -3);
                gl.glTexCoord2f(1, 1); gl.glVertex2f(3, 3);
                gl.glTexCoord2f(0, 1); gl.glVertex2f(-3, 3);
            gl.glEnd();
            texture.disable(gl);
        gl.glPopMatrix();
    }

    private void drawScene(GL2 gl){
        gl.glPushMatrix();
            scene = AWTTextureIO.newTexture(gl.getGLProfile(), image, false);
            scene.enable(gl);
            scene.bind(gl);
            gl.glRotatef(180, 1, 0, 0);
            gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_DECAL);
            gl.glBegin(GL2.GL_QUADS);
                gl.glTexCoord2f(0, 0); gl.glVertex2f(xMin*aspect, yMin);
                gl.glTexCoord2f(1, 0); gl.glVertex2f(xMax*aspect, yMin);
                gl.glTexCoord2f(1, 1); gl.glVertex2f(xMax*aspect, yMax);
                gl.glTexCoord2f(0, 1); gl.glVertex2f(xMin*aspect, yMax);
            gl.glEnd();
            scene.disable(gl);
        gl.glPopMatrix();
    }

    private void drawGame(GL2 gl, GLUT glut){
        float xL = pad.getxLeft();
        float xR = pad.getxRight();
        float y = pad.getY();
        gl.glPushMatrix();
            gl.glColor3f(0.0f,0.5f,0.5f);
            gl.glTranslatef(movePaddle, -6.0f, 0.0f);
            gl.glBegin(GL2.GL_QUADS);
                gl.glVertex2f(xL, -y);
                gl.glVertex2f(xR, -y);
                gl.glVertex2f(xR, y);
                gl.glVertex2f(xL, y);
            gl.glEnd();
        gl.glPopMatrix();


        if (ballX >= 14f || ballX <= -14f) 
        {
            ballX = Math.signum(ballX) * 14f;
            ballVelX = -ballVelX;
            ball.start();
        }
        if (ballY >= 7.95f) 
        {
            ballVelY = -ballVelY;
            pkmnHealth -= 25;
            ball.start();
        }
        else if (ballY < -8.9f)
        {
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
            ball.start();
        }   
        
        gl.glPushMatrix();
            gl.glColor3f(1.0f,0f,0f);
            gl.glTranslatef(ballX, ballY, 0.0f);
            glut.glutSolidSphere(0.3f, 70, 50);
        gl.glPopMatrix();

        ballX -= ballVelX;
        ballY += ballVelY;
    }

    private void changeLevel() throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        if(gen == 6){
            gen = 1;
        }
        soundTrack.stop();
        soundTrack = playSound(music[gen-1],0.0f);
        soundTrack.start();
        levelChange = false;
        image = ImageIO.read(new File(background[gen-1]));
        gen++;
    }
    
    public static Clip playSound(String location, float volume) throws LineUnavailableException, UnsupportedAudioFileException, IOException{
        File file = new File(location);
        Clip clip = AudioSystem.getClip();

        if(!(file.exists())){
            System.out.println("Arquivo não encontrado! Verifique se o áudio se encontra dentro da pasta Raiz do projeto.");
        }
        AudioInputStream audio = AudioSystem.getAudioInputStream(file);
        clip.open(audio);
        FloatControl vol = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        vol.setValue(volume);
        return clip;
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {    
        GL2 gl = drawable.getGL().getGL2();  
        
        if(height == 0) height = 1;
        aspect = (float) width / height;
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
