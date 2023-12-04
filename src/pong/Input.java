package pong;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseAdapter;
import java.awt.Color;
/**
 *
 * @author William Franz
 */
public class Input extends MouseAdapter implements KeyListener{
    private Cena cena;
    private float mouseX, mouseY, relativeMouseX, relativeMouseY;
    
    public Input(Cena cena){
        this.cena = cena;
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        // Menu do jogo
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
            Menu.gameStart = !Menu.gameStart;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = (float)e.getX();
        mouseY = (float)e.getY();
        relativeMouseX = ( (2 * 12.7f * mouseX) / cena.larguraFrame) - 12.7f;
        relativeMouseY = (( (2 * 10f * mouseY) / cena.alturaFrame) - 10f) * -1;
        
        if(Menu.gameStart == true){
            cena.movePaddle = relativeMouseX;
        }
        if(relativeMouseX <= 5.3f && relativeMouseX >= -5.3f){
            if(relativeMouseY < 3.75f && relativeMouseY > 0.0f){
                Menu.color[0] = Color.RED;
                Menu.color[1] = Color.WHITE;
                Menu.color[2] = Color.WHITE;
            }else if(relativeMouseY < 0.0f && relativeMouseY > -3.75f){
                Menu.color[0] = Color.WHITE;
                Menu.color[1] = Color.RED;
                Menu.color[2] = Color.WHITE;
            }else if(relativeMouseY < -3.75f && relativeMouseY > -7.5f){
                Menu.color[0] = Color.WHITE;
                Menu.color[1] = Color.WHITE;
                Menu.color[2] = Color.RED;
            }else{
                Menu.color[0] = Color.WHITE;
                Menu.color[1] = Color.WHITE;
                Menu.color[2] = Color.WHITE;
            }
        } else{
            Menu.color[0] = Color.WHITE;
            Menu.color[1] = Color.WHITE;
            Menu.color[2] = Color.WHITE;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {   
        int botao = e.getButton();
        if(Menu.gameStart == false && botao == MouseEvent.BUTTON1){
            if(relativeMouseX <= 5.3f && relativeMouseX >= -5.3f){
                if(relativeMouseY < 3.75f && relativeMouseY > 0.0f){
                    if(!(Menu.seeRules)) Menu.gameStart = true;
                }else if(relativeMouseY < 0.0f && relativeMouseY > -3.75f){
                    if(!(Menu.seeRules)) Menu.seeRules = true;
                }else if(relativeMouseY < -3.75f && relativeMouseY > -7.5f){
                    if(!(Menu.seeRules)){
                        System.exit(0);
                    } else{
                        Menu.seeRules = false;
                    }
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) { }

}
