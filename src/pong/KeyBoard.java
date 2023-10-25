package pong;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
/**
 *
 * @author William Franz
 */
public class KeyBoard implements KeyListener{
    private Cena cena;
    
    public KeyBoard(Cena cena){
        this.cena = cena;
    }
    
    @Override
    public void keyPressed(KeyEvent e) {     
        //System.out.println("Key pressed: " + e.getKeyCode());
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
            System.exit(0);
        if(e.getKeyChar() == 'a' || e.getKeyCode() == 149){
            if(!(cena.pad.getxLeft() + cena.movePaddle <= -14.2f)){
                cena.movePaddle -= 0.5f;
            }
        }
        if(e.getKeyChar() == 'd' || e.getKeyCode() == 151){
            if(!(cena.pad.getxRight() + cena.movePaddle >= 14.2f)){
                cena.movePaddle += 0.5f;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) { }

}
