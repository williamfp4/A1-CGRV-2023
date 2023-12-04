package pong;
import java.util.Random;
import java.awt.image.BufferedImage;
import java.awt.Color;
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
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
/**
 *
 * @author William Franz
 */
public class Cena implements GLEventListener {    
    public float movePaddle, ballVelX, ballVelY, ballSpeed, mouseX, root, larguraFrame, alturaFrame;
    public float xMin, xMax, yMin, yMax, zMin, zMax, aspect;
    private int pkmnHealth, points, gen, lives;
    private float ballX, ballY, ballSpin, spinY;
    private String ballSound;
    private String[] background = new String[]{"A1-CGRV-2023/images/kanto.jpg","A1-CGRV-2023/images/johto.jpg","A1-CGRV-2023/images/hoenn.jpg","A1-CGRV-2023/images/sinnoh.jpg","A1-CGRV-2023/images/unova.jpg"};
    private String[] music = new String[]{"A1-CGRV-2023/audio/r1Kanto.wav","A1-CGRV-2023/audio/r101.wav","A1-CGRV-2023/audio/r29.wav","A1-CGRV-2023/audio/r201.wav","A1-CGRV-2023/audio/r1Unova.wav"};
    private Clip soundTrack, ball;
    private boolean resumeSound, levelChange, findPkmn;
    BufferedImage image, ballImage;
    Paddle pad = new Paddle(-1.5f, 1.5f, 0.2f);
    Random rn = new Random();
    Texture texture, scene, pokeBall;
    GLU glu;
    GL2 gl;
        
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
        ballSpin = 1;
        spinY = 0;
        lives = 5;
        points = 0;
        pkmnHealth = 0;
        gen = 0;
        levelChange = true;
        findPkmn = false;
        root = (float) xMax * aspect;
        ballSound = "A1-CGRV-2023/audio/ball.wav"; // Kanto = kanto1; Johto = kanto 101; Hoenn = kanto 29; Sinnoh = kanto 201
        try {
            ballImage = ImageIO.read(new File("A1-CGRV-2023/images/pokeball2.png"));
            soundTrack = playSound(music[0],0.0f);
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override   
    public void display(GLAutoDrawable drawable) {  
        GLUT glut = new GLUT();
        gl = drawable.getGL().getGL2();      
        gl.glClearColor(0, 0, 0, 0);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);       
        gl.glLoadIdentity(); 
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        // REVER O POLYGON MODE NAS PRÓXIMAS IMPLEMENTAÇÕES
        //gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

        if(Menu.gameStart){
            try {
                if(resumeSound){
                    soundTrack.start();
                    resumeSound = false;
                }
                ball = playSound(ballSound, -10.0f);
                if(points != 0 && points % 200 == 0){
                    findPkmn = true;
                } 

                if(levelChange){
                    changeLevel();
                } 

                drawScene(gl);

                if(findPkmn){
                    if(pkmnHealth == 0){
                        encounter(gl);
                        soundTrack.stop();
                        soundTrack = playSound("A1-CGRV-2023/audio/encounterEffect.wav",-2.0f);
                        soundTrack.start();
                        Thread.sleep(2500);
                        soundTrack.stop();
                        soundTrack = playSound("A1-CGRV-2023/audio/encounter.wav",-5.0f);
                        soundTrack.start();
                        image = ImageIO.read(new File("A1-CGRV-2023/images/encounter.jpeg"));
                        pkmnHealth = 100;
                    }else{
                        drawPkmn(gl);
                    }
                }

                drawGameBar(gl);
                drawGame(gl,glut);
        
            } catch (LineUnavailableException | UnsupportedAudioFileException | IOException | InterruptedException e) {
                e.printStackTrace();
            }

            if(lives == 0){
                System.exit(0);
            }
        
            gl.glFlush();
        }else{ 
            try {
                soundTrack.stop();
                resumeSound = true;
                Menu.showMenu(gl);
                if(Menu.seeRules == true){
                    gl.glColor4f(1, 1, 1, 1f);
                    gl.glBegin(GL2.GL_QUADS);
                        gl.glVertex2f(-6, -6);
                        gl.glVertex2f(-6, 3);
                        gl.glVertex2f(6, 3);
                        gl.glVertex2f(6, -6);
                    gl.glEnd();
                    Menu.drawText(gl, "-Para movimentar o bastão utilize o mouse;", Color.BLACK, 20, 420, 400);
                    Menu.drawText(gl, "-Receba 50 pontos a cada rebatida de bola;", Color.BLACK, 20, 420, 360);
                    Menu.drawText(gl, "-Um pokémon aparece a cada 200 pontos;", Color.BLACK, 20, 420, 320);
                    Menu.drawText(gl, "-Os pokémons tem 100 pontos de vida;", Color.BLACK, 20, 420, 280);
                    Menu.drawText(gl, "-São capturados os que chegarem a 0 de vida;", Color.BLACK, 20, 420, 240);
                    Menu.drawText(gl, "-Caso perca 5 pokébolas, o jogo acaba.", Color.BLACK, 20, 420, 200);
                    Menu.drawText(gl, "< Voltar >", Color.BLUE, 25, 580, 130);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }

    private void encounter(GL2 gl) throws IOException {
        int num = 0;
        switch (gen) {
            case 1: num = rn.nextInt(151) + 1; break; //Kanto
            case 2: num = rn.nextInt(100) + 152; break; //Johto
            case 3: num = rn.nextInt(135) + 252; break; //Hoenn
            case 4: num = rn.nextInt(107) + 387; break; //Sinnoh
            case 5: num = rn.nextInt(156) + 494; break; //Unova
            default: num = rn.nextInt(649) + 1; break;
        }
        texture = getPokeImage(gl, num);
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
            gl.glTranslatef(7, -2.5f, 0);
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
            if(!findPkmn){
                points += 50;
            }else{
                pkmnHealth -= 25;
            }
            if(findPkmn == true && pkmnHealth == 0){
                findPkmn = false;
                levelChange = true;
                points += 100;
            }
            ball.start();
        }
        else if (ballY < -8.9f)
        {
            ballX = 0f; ballY = 5f;
            lives -= 1;
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

        drawBall(gl);
        ballX -= ballVelX;
        ballY += ballVelY;
        ballSpin += 1;
        spinY += 3;
    }

    private void drawGameBar(GL2 gl) throws IOException {
        gl.glPushMatrix();
            gl.glColor4f(0, 0, 0, 0.4f);
            gl.glTranslatef(0, 14, 0);
            gl.glBegin(GL2.GL_QUADS);
                gl.glVertex2f(-16, -7);
                gl.glVertex2f(16, -7);
                gl.glVertex2f(16, -6);
                gl.glVertex2f(-16, -6);
            gl.glEnd();
        gl.glPopMatrix();

        BufferedImage lifeImage = ImageIO.read(new File("A1-CGRV-2023/images/pokeball.png"));
        Texture lifeTexture = AWTTextureIO.newTexture(gl.getGLProfile(), lifeImage, false);

        gl.glPushMatrix();
            lifeTexture.enable(gl);
            lifeTexture.bind(gl);
            gl.glColor4f(1, 1, 1, 1f);
            gl.glTranslatef(0, 14, 0);
            gl.glRotatef(180, 1, 0, 0);
            gl.glTranslatef(0, 13, 0);
            gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
            if(lives >= 1){
                gl.glBegin(GL2.GL_QUADS);
                    gl.glTexCoord2f(0, 0); gl.glVertex2f(-14f, -7);
                    gl.glTexCoord2f(1, 0); gl.glVertex2f(-13f, -7);
                    gl.glTexCoord2f(1, 1); gl.glVertex2f(-13f, -6);
                    gl.glTexCoord2f(0, 1); gl.glVertex2f(-14f, -6);
                gl.glEnd();
            }
            if(lives >= 2){
                gl.glBegin(GL2.GL_QUADS);
                    gl.glTexCoord2f(0, 0); gl.glVertex2f(-13f, -7);
                    gl.glTexCoord2f(1, 0); gl.glVertex2f(-12f, -7);
                    gl.glTexCoord2f(1, 1); gl.glVertex2f(-12f, -6);
                    gl.glTexCoord2f(0, 1); gl.glVertex2f(-13f, -6);
                gl.glEnd();
            }
            if(lives >= 3){
                gl.glBegin(GL2.GL_QUADS);
                    gl.glTexCoord2f(0, 0); gl.glVertex2f(-12f, -7);
                    gl.glTexCoord2f(1, 0); gl.glVertex2f(-11f, -7);
                    gl.glTexCoord2f(1, 1); gl.glVertex2f(-11f, -6);
                    gl.glTexCoord2f(0, 1); gl.glVertex2f(-12f, -6);
                gl.glEnd();
            }
            if(lives >= 4){
                gl.glBegin(GL2.GL_QUADS);
                    gl.glTexCoord2f(0, 0); gl.glVertex2f(-11f, -7);
                    gl.glTexCoord2f(1, 0); gl.glVertex2f(-10f, -7);
                    gl.glTexCoord2f(1, 1); gl.glVertex2f(-10f, -6);
                    gl.glTexCoord2f(0, 1); gl.glVertex2f(-11f, -6);
                gl.glEnd();
            }
            if(lives == 5){
                gl.glBegin(GL2.GL_QUADS);
                    gl.glTexCoord2f(0, 0); gl.glVertex2f(-10f, -7);
                    gl.glTexCoord2f(1, 0); gl.glVertex2f(-9f, -7);
                    gl.glTexCoord2f(1, 1); gl.glVertex2f(-9f, -6);
                    gl.glTexCoord2f(0, 1); gl.glVertex2f(-10f, -6);
                gl.glEnd();
            }
            lifeTexture.disable(gl);
        gl.glPopMatrix();

        Menu.drawText(gl, "Points: "+String.valueOf(points), Color.CYAN, 30, 1002, 687);
    }

    private void drawBall(GL2 gl) {
        gl.glPushMatrix();
            /* float luzDifusa[] = {1f, 0f, 0f, 1.0f}; //cor
            float posicaoLuz[] = {-50.0f, 0.0f, 100.0f, 0.0f}; //1.0 pontual */
            gl.glColor3f(1.0f,0f,0f);
            /* gl.glEnable(GL2.GL_COLOR_MATERIAL);
            gl.glEnable(GL2.GL_LIGHTING);
            gl.glEnable(GL2.GL_LIGHT0);
            gl.glShadeModel(GL2.GL_FLAT); */
            gl.glTranslatef(ballX, ballY, 0.0f);
            gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
            /* gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, luzDifusa, 0);
            gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, posicaoLuz, 0); */
            pokeBall = AWTTextureIO.newTexture(gl.getGLProfile(), ballImage, false);
            pokeBall.enable(gl);
            pokeBall.bind(gl);
            gl.glRotatef(90 , 1, 0, 0);
            gl.glRotatef(ballSpin,0,0,1);
            gl.glRotatef(spinY, 0,1,0);
            if(ballSpin >= 360.0f){
                ballSpin = 0;
            }
            glu = new GLU();   
                GLUquadric quadObj = glu.gluNewQuadric();   
                glu.gluQuadricDrawStyle(quadObj, GLU.GLU_FILL);   
                glu.gluQuadricNormals(quadObj, GLU.GLU_FLAT);   
                glu.gluQuadricTexture(quadObj, true);
                glu.gluSphere(quadObj, 0.3f, 30, 50);
            pokeBall.disable(gl);
        gl.glPopMatrix();
    }

    private void changeLevel() throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        if(gen == 5){
            gen = 0;
        }
        soundTrack.stop();
        soundTrack = playSound(music[gen],0.0f);
        soundTrack.start();
        levelChange = false;
        image = ImageIO.read(new File(background[gen]));
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
