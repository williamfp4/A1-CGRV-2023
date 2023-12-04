package pong;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;
/**
 *
 * @author William Franz
 */
public class Renderer {
    private static GLWindow window = null;
    public static int screenWidth = 1280;
    public static int screenHeight = 720; 

    //Cria a janela de rendeziração do JOGL
    public static void init(){        
        GLProfile.initSingleton();
        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities caps = new GLCapabilities(profile);        
        window = GLWindow.create(caps);
        window.setSize(screenWidth, screenHeight);
        window.setTitle("PokéOut!");
        
        Cena cena = new Cena();
        Input inputs = new Input(cena);
        new Menu(cena);
        
        window.addGLEventListener(cena);
        window.addMouseListener(inputs);
        window.addKeyListener(inputs);
        
        
        FPSAnimator animator = new FPSAnimator(window, 60);
        animator.start();
        
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDestroyNotify(WindowEvent e) {
                animator.stop();
                System.exit(0);
            }
        });       
        
        // Se preferir colocar em Janela, mudar para false
        window.setFullscreen(false);
        window.setVisible(true);
    }
  
    public static void main(String[] args) {
        init();
    }
}
