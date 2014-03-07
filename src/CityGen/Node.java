package CityGen;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import java.util.Random;

/**
 *
 * @author Xinchro
 */
public class Node{

    public enum Type{BUILDING, PROP, ROAD, GRASS, AIR}
    
    float weightRoad;
    float weightBuilding;
    float weightProp;
    float weightGrass;
    float weightAir;
    float[] globalWeights;
    Vector3f pos;
    Type type;
    
    ColorRGBA color;
    ColorRGBA white = new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f);
    ColorRGBA black = new ColorRGBA(0.0f, 0.0f, 0.0f, 1.0f);
    ColorRGBA red = new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f);
    ColorRGBA blue = new ColorRGBA(0.0f, 0.0f, 1.0f, 1.0f);
    ColorRGBA green = new ColorRGBA(0.0f, 1.0f, 0.0f, 1.0f);
    
    Node underNode;
    
    public Node(){
	weightRoad = 0.0f;
	weightBuilding = 0.0f;
	weightProp = 0.0f;
	weightGrass = 0.0f;
	weightAir = 0.0f;
	type = Type.AIR;
	pos = new Vector3f(0.0f, 0.0f, 0.0f);
	color = white;
	doWeights();
    }
    
    public Node(Vector3f pos){
	weightRoad = 0.0f;
	weightBuilding = 0.0f;
	weightProp = 0.0f;
	weightGrass = 0.0f;
	weightAir = 0.0f;
	type = Type.AIR;
	this.pos = pos;
	doWeights();
	//System.out.println("Node(vec3f) - new node at " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
    }
    
    public void doWeights(){
	Random rand = new Random();
	float value  = rand.nextFloat();
	//System.out.println("Node.doWeights - float: " + value);
	
	if(value >= 0.8f && value <= 1.0f){
	    type = Type.AIR;
	}
	if(value >= 0.6f && value <= 0.8f){
	    type = Type.GRASS;
	}
	if(value >= 0.4f && value <= 0.6f){
	    type = Type.ROAD;
	}
	if(value >= 0.2f && value <= 0.4f){
	    type = Type.PROP;
	}
	if(value >= 0.0f && value <= 0.2f){
	    type = Type.BUILDING;
	}
	
	checkType();	
    }
    
    public void checkType(){
	//no grass past ground level (y = 0)
	if(pos.getY() >= 1 && (type == Type.GRASS || type == Type.ROAD)){
	    doWeights();
	}
	//no air at ground level (y = 0)
	if(pos.getY() == 0 && type == Type.AIR){
	    doWeights();
	}
	//no props past level 2 (y = 2)
	if(pos.getY() >= 2 && type == Type.PROP){
	    doWeights();
	}
	//lose global weighting
	switch(type){
	    case BUILDING:
		doBuilding();
		break;
	    case PROP:
		doProp();
		break;
	    case ROAD:
		doRoad();
		break;
	    case GRASS:
		doGrass();
		break;
	    case AIR:
		doAir();
		break;
	}
	
		
    }
    
    public Type getType(){
	return type;
    }
    
    void doBuilding(){
	//System.out.println("Node.doBuilding");
	if(underNode != null){
	    //System.out.println("Node.doBuilding - under node type: " + underNode.getType());
	    if(underNode.getType() != Type.BUILDING){
		type = Type.AIR;
		//System.out.println("Node.doBuilding - under is not building");
	    }else{
		type = Type.BUILDING;
	    }	    
	}else{
	    //System.out.println("Node.doBuilding - under is null");
	}
    }
    
    void setUnderNode(Node underNode){
	this.underNode = underNode;
	doWeights();
    }
    
    void doProp(){
	if(underNode != null){
	    //System.out.println("Node.doBuilding - under node type: " + underNode.getType());
	    if(underNode.getType() != Type.PROP){
		type = Type.AIR;
		//System.out.println("Node.doBuilding - under is not building");
	    }else{
		type = Type.PROP;
	    }	    
	}else{
	    //System.out.println("Node.doBuilding - under is null");
	}
    }
    
    void doRoad(){
	type = Type.ROAD;
    }
    
    void doGrass(){
	type = Type.GRASS;
    }
    
    void doAir(){
	type = Type.AIR;
    }
    
    public Vector3f getPos(){
	return this.pos;
    }
    
    public void setGlobals(float[] globals){
	if(globals.length == 5){
	    globalWeights = globals;
	    weightRoad = globals[0];
	    weightBuilding = globals[1];
	    weightProp = globals[2];
	    weightGrass = globals[3];
	    weightAir = globals[4];
	}else{
	    System.out.println("CityGen.Node.setGlobals - Invalid number of global weights!");
	}
    }
}
