package pong;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseAdapter;
/**
 *
 * @author William Franz
 */
public class Input extends MouseAdapter implements KeyListener{
    private Cena cena;
    private float mouseX, mouseY, velX, velY;
    private boolean stop = false;
    
    public Input(Cena cena){
        this.cena = cena;
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        // Fechar o jogo
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
            System.exit(0);
        }
        // Menu do jogo
        if(e.getKeyCode() == KeyEvent.VK_ENTER){   
            stop = !stop;
            if (cena.ballVelX != 0){
                velX = cena.ballVelX;
                velY = cena.ballVelY;
                cena.ballVelX = cena.ballVelY = 0;
            }
            else{
                cena.ballVelX = velX;
                cena.ballVelY = velY;
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            cena.ballSpin += 1;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = (float)e.getX();
        if(stop != true){
            cena.movePaddle = ( (2 * 12.7f * mouseX) / cena.larguraFrame) - 12.7f;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {   
        int botao = e.getButton();
        if(stop == true && botao == MouseEvent.BUTTON1){
            mouseX = ((2 * 12.7f * (float)e.getX()) / cena.larguraFrame) - 12.7f;
            mouseY = (((2 * 10f * (float)e.getY()) / cena.alturaFrame) - 10f) * -1;

            if((mouseX >= -3 && mouseX <= 3) && (mouseY >= 4 && mouseY <= 6)){
                System.out.println("You pressed an invisible button!");
                cena.ballVelX = velX;
                cena.ballVelY = velY;
                stop = false;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) { }
}
