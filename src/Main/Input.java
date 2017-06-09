package Main;

import com.jme3.animation.LoopMode;
import com.jme3.input.InputManager;
import com.jme3.input.Joystick;
import com.jme3.input.KeyInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.JoyAxisTrigger;
import com.jme3.input.controls.JoyButtonTrigger;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.sun.org.apache.bcel.internal.generic.MULTIANEWARRAY;

/**
 *
 * @author Xinchro
 */
public class Input implements AnalogListener, ActionListener, RawInputListener{

    private float movementSpeed = 0.05f;
    private float rotationSpeed = 0.05f;
    
    @Override
    public void onAnalog(String name, float value, float tpf) {
//	throw new UnsupportedOperationException("Not supported yet. onAnalog");
	Vector3f movement;
	switch(name){
	    case "up":
		movement = new Vector3f(0.0f,0.0f,movementSpeed);
		//System.out.println("Up pressed");
		main.playerNode().setLocalTranslation(main.playerNode().localToWorld(movement,movement));
		break;
	    case "left":
		movement = new Vector3f(movementSpeed,0.0f,0.0f);
		//System.out.println("Left pressed");
		main.playerNode().setLocalTranslation(main.playerNode().localToWorld(movement,movement));
		break;
	    case "down":
		movement = new Vector3f(0.0f,0.0f,movementSpeed);
		//System.out.println("Down pressed");
		main.playerNode().setLocalTranslation(main.playerNode().localToWorld(movement.negate(),movement.negate()));
		break;
	    case "right":
		movement = new Vector3f(movementSpeed,0.0f,0.0f);
		//System.out.println("Right pressed");
		main.playerNode().setLocalTranslation(main.playerNode().localToWorld(movement.negate(),movement.negate()));
		break;
	    case "rotU":
		//System.out.println("RotU pressed");
		main.playerNode().rotate(rotationSpeed, 0, 0);
		break;
	    case "rotL":
		//System.out.println("RotL pressed");
		main.playerNode().rotate(0, rotationSpeed, 0);
		break;
	    case "rotD":
		//System.out.println("RotD pressed");
		main.playerNode().rotate(-rotationSpeed, 0, 0);
		break;
	    case "rotR":
		//System.out.println("RotR pressed");
		main.playerNode().rotate(0, -rotationSpeed, 0);
		break;
	    case "rotCW":
		//System.out.println("RotCW pressed");
		main.playerNode().rotate(0, -rotationSpeed, 0);
		break;
	    case "rotCCW":
		//System.out.println("RotCCW pressed");
		main.playerNode().rotate(0, rotationSpeed, 0);
		break;
	}
	//System.out.println("Player pos: " + main.playerNode.getWorldTranslation());
    }

    @Override
    public void beginInput() {
	//throw new UnsupportedOperationException("Not supported yet. beginInput");
    }

    @Override
    public void endInput() {
	//throw new UnsupportedOperationException("Not supported yet. endInput");
    }

    
    public void moveCharacter(){
	
	
	if(speedToggle){
	    movementSpeed = 2f;
	}else{
	    movementSpeed = 0.5f;
	}
	
	if(movement != null){
	    Vector3f camDir = main.getCamera().getDirection();
	    Vector3f camLeft = main.getCamera().getLeft().clone().multLocal(0.1f);

	    main.playerNode().setLocalTranslation(main.playerNode().localToWorld(movement.negate().mult(movementSpeed),movement.negate().mult(movementSpeed)));
	    //System.out.println("Move character");
	    //System.out.println("Player local trans: " + main.playerNode.getLocalTranslation());
	    //System.out.println("Player world trans: " + main.playerNode.getWorldTranslation());
	    //System.out.println("Moving by: " + movement);
	}
	if(rotation != null){
	    main.playerNode().rotate(-rotation.getY()*rotationSpeed
		    ,0
		    ,rotation.getX()*rotationSpeed
			    );
	    
	}
	
	if(xRotRActual < deadZone || xRotRActual > -deadZone){
	    if(rotation != null){
		rotation = new Vector3f(0,yRotR,zRotR);
	    }
	}
	
	if(yRotRActual < deadZone || yRotRActual > -deadZone){
	    if(rotation != null){
		rotation = new Vector3f(xRotR,0,zRotR);
	    }
	}
	
	if(zRotRActual < deadZone || zRotRActual > -deadZone){
	    if(rotation != null){
		rotation = new Vector3f(xRotR,yRotR,0);
	    }
	}
    }
    
    private Vector3f movement;
    private Vector3f rotation;
    private float xRotL = 0.0f;
    private float yRotL = 0.0f;
    private float zRotL = 0.0f;
    private float xRotR = 0.0f;
    private float yRotR = 0.0f;
    private float zRotR = 0.0f;
    
    private float xRotRActual = 0.0f;
    private float yRotRActual = 0.0f;
    private float zRotRActual = 0.0f;
    
    @Override
    public void onJoyAxisEvent(JoyAxisEvent evt) {
	//throw new UnsupportedOperationException("Not supported yet. onJoyAxisEvent");
	//for movement
	if((evt.getAxis().getAxisId() == 0 && (evt.getValue() > deadZone || evt.getValue() < -deadZone))
		|| (evt.getAxis().getAxisId() == 1 && (evt.getValue() > deadZone || evt.getValue() < -deadZone))){
	    
	    
		if(evt.getAxis().getLogicalId().equalsIgnoreCase("x")){
			xRotL = evt.getValue();//*movementSpeed;
			//System.out.println("Moving X by " + evt.getValue());
		}
		if(evt.getAxis().getLogicalId().equalsIgnoreCase("y")){
			yRotL = evt.getValue();//*movementSpeed;
			//System.out.println("Moving Y by " + evt.getValue());
		}
		if(evt.getAxis().getLogicalId().equalsIgnoreCase("z")){
			zRotL = evt.getValue();//*movementSpeed;
			//System.out.println("Moving Z by " + evt.getValue());
		}
	    
	    movement = new Vector3f(xRotL,zRotL,yRotL);
	    //System.out.println("Move Axis: " + evt.getAxis() + " " +  evt.getValue());
	}else{
	    if(evt.getAxis().getLogicalId().equalsIgnoreCase("x")){
			xRotL = 0.0f;
			//System.out.println("Moving X by " + evt.getValue());
		}
		if(evt.getAxis().getLogicalId().equalsIgnoreCase("y")){
			yRotL = 0.0f;
			//System.out.println("Moving Y by " + evt.getValue());
		}
		if(evt.getAxis().getLogicalId().equalsIgnoreCase("z")){
			zRotL = 0.0f;
			//System.out.println("Moving Z by " + evt.getValue());
		}
	    
	    movement = new Vector3f(xRotL,zRotL,yRotL);
	}
	//for rotation
	
	if((evt.getAxis().getAxisId() == 2)
		|| (evt.getAxis().getAxisId() == 3)){
	    
	    if(evt.getAxis().getLogicalId().equalsIgnoreCase("rx")){
			xRotRActual = evt.getValue();
		}
		if(evt.getAxis().getLogicalId().equalsIgnoreCase("ry")){
			yRotRActual = evt.getValue();
		}
		if(evt.getAxis().getLogicalId().equalsIgnoreCase("rz")){
			zRotRActual = evt.getValue();
		}
	}
	
	
	if((evt.getAxis().getAxisId() == 2 && (evt.getValue() > deadZone || evt.getValue() < -deadZone))
		|| (evt.getAxis().getAxisId() == 3 && (evt.getValue() > deadZone || evt.getValue() < -deadZone))){
	    
	    
		if(evt.getAxis().getLogicalId().equalsIgnoreCase("rx")){
			xRotR = evt.getValue();
		}
		if(evt.getAxis().getLogicalId().equalsIgnoreCase("ry")){
			yRotR = evt.getValue();
		}
		if(evt.getAxis().getLogicalId().equalsIgnoreCase("rz")){
			zRotR = evt.getValue();
		}
	    rotation = new Vector3f(xRotR,yRotR,zRotR);
	    //System.out.println("Rot Axis: " + evt.getAxis() + " " +  evt.getValue());
	    
	}else{
	    if(evt.getAxis().getLogicalId().equalsIgnoreCase("rx")){
			xRotR = 0.0f;
			//System.out.println("Moving X by " + evt.getValue());
		}
		if(evt.getAxis().getLogicalId().equalsIgnoreCase("ry")){
			yRotR = 0.0f;
			//System.out.println("Moving Y by " + evt.getValue());
		}
		if(evt.getAxis().getLogicalId().equalsIgnoreCase("rz")){
			zRotR = 0.0f;
			//System.out.println("Moving Z by " + evt.getValue());
		}
		rotation = new Vector3f(xRotR,yRotR,zRotR);
	}
    }

    @Override
    public void onJoyButtonEvent(JoyButtonEvent evt) {
	//throw new UnsupportedOperationException("Not supported yet. onJoyButtonEvent");
	System.out.println("Button: " + evt.getButton().getName());
    }

    @Override
    public void onMouseMotionEvent(MouseMotionEvent evt) {
	//throw new UnsupportedOperationException("Not supported yet. onMouseMotionEvent");
    }

    @Override
    public void onMouseButtonEvent(MouseButtonEvent evt) {
	//throw new UnsupportedOperationException("Not supported yet. onMouseButtonEvent");
    }

    @Override
    public void onKeyEvent(KeyInputEvent evt) {
	//throw new UnsupportedOperationException("Not supported yet. onKeyEvent");
    }

    @Override
    public void onTouchEvent(TouchEvent evt) {
	//throw new UnsupportedOperationException("Not supported yet. onTouchEvent");
    }
    private Main main;
    private InputManager inMan;
    private Joystick pad;
    private float deadZone = 0.2f;

    public Input(Main main){
	this.main = main;
	this.inMan = main.getInputManager();
	
	this.inMan.addRawInputListener(this);
	
	//this.pad = this.inMan.getJoysticks()[0];
    }
    
    
    private boolean speedToggle = false;
    
    public void onAction(String name, boolean keyPressed, float tpf) {
	switch(name){
	    case "space":
		if(keyPressed){
		    //System.out.println("rootNode children: " + rootNode.getQuantity());
		    //System.out.println("building array: " + buildings.size());
		    //System.out.println("road arrays: " + roadsX.size() + " " + roadsZ.size());
		}else{
		    main.genNewRandomCity();
		}
		break;
	    case "lookat":
		if(keyPressed){
		}else{
		    main.camNode().lookAt(main.playerNode().getWorldTranslation(), Vector3f.UNIT_Z);
		}
		break;
	    case "speedBoost":
		if(keyPressed){
		    speedToggle = true;
		    System.out.println("Speeding up");
		}else{
		    speedToggle = false;
		    System.out.println("Slowing down");
		}
		break;
	    case "anim":
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

    
    public void initKeys(){
	main.getFlyByCamera().setEnabled(false);
	
	inMan.addMapping("space", new KeyTrigger(KeyInput.KEY_SPACE));
	inMan.addListener(this,"space");
	
	inMan.addMapping("anim", new KeyTrigger(KeyInput.KEY_R));
	inMan.addListener(this,"anim");
	
	inMan.addMapping("up", new KeyTrigger(KeyInput.KEY_W));
	inMan.addMapping("left", new KeyTrigger(KeyInput.KEY_A));
	inMan.addMapping("down", new KeyTrigger(KeyInput.KEY_S));
	inMan.addMapping("right", new KeyTrigger(KeyInput.KEY_D));

	inMan.addMapping("rotU", new KeyTrigger(KeyInput.KEY_UP));
	inMan.addMapping("rotL", new KeyTrigger(KeyInput.KEY_LEFT));
	inMan.addMapping("rotD", new KeyTrigger(KeyInput.KEY_DOWN));
	inMan.addMapping("rotR", new KeyTrigger(KeyInput.KEY_RIGHT));
	inMan.addMapping("rotCW", new KeyTrigger(KeyInput.KEY_E));
	inMan.addMapping("rotCCW", new KeyTrigger(KeyInput.KEY_Q));
	
	inMan.addMapping("speedBoost", new KeyTrigger(KeyInput.KEY_LSHIFT));
	
	//left stick
	//inMan.addMapping("up", new JoyAxisTrigger(pad.getJoyId(), 0, true));
	//inMan.addMapping("left", new JoyAxisTrigger(pad.getJoyId(), 1, true));
	//inMan.addMapping("down", new JoyAxisTrigger(pad.getJoyId(), 0, false));
	//inMan.addMapping("right", new JoyAxisTrigger(pad.getJoyId(), 1, false));
	//inMan.addMapping("anim", new JoyAxisTrigger(pad.getJoyId(), 0, true));
	
	//right stick
	//inMan.addMapping("rotU", new JoyAxisTrigger(pad.getJoyId(), 2, true));
	//inMan.addMapping("rotL", new JoyAxisTrigger(pad.getJoyId(), 3, true));
	//inMan.addMapping("rotD", new JoyAxisTrigger(pad.getJoyId(), 2, false));
	//inMan.addMapping("rotR", new JoyAxisTrigger(pad.getJoyId(), 3, false));
	
	//triggers
	//inMan.addMapping("speedBoost", new JoyButtonTrigger(pad.getJoyId(), 0));
	//inMan.addMapping("rotL", new JoyAxisTrigger(pad.getJoyId(), 4, false));
	
	//inMan.addMapping("up", new JoyButtonTrigger(pad.getJoyId(), 0));
	//inMan.addMapping("left", new JoyButtonTrigger(pad.getJoyId(), 2));
	//inMan.addMapping("down", new JoyButtonTrigger(pad.getJoyId(), 3));
	//inMan.addMapping("right", new JoyButtonTrigger(pad.getJoyId(), 1));
	//inMan.addMapping("lookat", new JoyButtonTrigger(pad.getJoyId(), 0));
	
	//inMan.addMapping("rotCW", new JoyButtonTrigger(pad.getJoyId(), 5));
	//inMan.addMapping("rotCCW", new JoyButtonTrigger(pad.getJoyId(), 4));
	inMan.addListener(this,"up");
	inMan.addListener(this,"left");
	inMan.addListener(this,"down");
	inMan.addListener(this,"right");
	inMan.addListener(this,"rotU");
	inMan.addListener(this,"rotL");
	inMan.addListener(this,"rotD");
	inMan.addListener(this,"rotR");
	inMan.addListener(this,"rotCW");
	inMan.addListener(this,"rotCCW");
	
	inMan.addListener(this,"speedBoost");
    }
}
