package Main;

import com.jme3.animation.LoopMode;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

/**
 *
 * @author Xinchro
 */
public class Input {
    Main main;

    public Input(Main main){
	this.main = main;
    }
    
    boolean spacePressed;
    
    private ActionListener actionListener = new ActionListener() {
	public void onAction(String name, boolean keyPressed, float tpf) {
	    switch(name){
		case "Space":
		    if(spacePressed){
			//System.out.println("rootNode children: " + rootNode.getQuantity());
			//System.out.println("building array: " + buildings.size());
			//System.out.println("road arrays: " + roadsX.size() + " " + roadsZ.size());
			spacePressed = false;
		    }else{
			spacePressed = true;
			main.genNewRandomCity();
		    }
		    break;
		case "Anim":	
		    if (!main.getChannel().getAnimationName().equals("Forward")) {
			main.getChannel().setAnim("Forward", 0.50f);
			main.getChannel().setLoopMode(LoopMode.Loop);
			main.getChannel().setSpeed(0.5f);
		      }else{
			main.getChannel().setAnim("Still", 0.50f);
			main.getChannel().setSpeed(0.5f);
			main.getChannel().setLoopMode(LoopMode.Loop);
			}
		    break;
		    
	    }
	}
      };
    
    public void initKeys(){
	main.getInputManager().addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
	
	main.getInputManager().addListener(actionListener,"Space");
	
	main.getInputManager().addMapping("Anim", new KeyTrigger(KeyInput.KEY_E));
	
	main.getInputManager().addListener(actionListener,"Anim");
    }
}
