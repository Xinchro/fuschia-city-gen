package CityGen;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Xinchro
 */
public class CityNode {

    public enum Type {

	BUILDING, PROP, ROAD, GRASS, AIR
    }
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
    CityNode underNode;
    ArrayList<CityNode> surroundingNodes = new ArrayList<CityNode>();
    float[] weightsArr = new float[5];
    
    ArrayList<CityNode> buildingBlocks = new ArrayList<CityNode>();

    public CityNode() {
	setDefaultWeights();

	fillWeightArray();

	type = null;
	pos = new Vector3f(0.0f, 0.0f, 0.0f);
	color = white;
	//doWeights();
    }

    public CityNode(Vector3f pos) {
	setDefaultWeights();

	fillWeightArray();

	type = null;
	this.pos = pos;
	//doWeights();
	//System.out.println("Node(vec3f) - new node at " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
    }
    
    void setDefaultWeights(){
	weightRoad = 0.4f;
	weightBuilding = 0.5f;
	weightProp = 0.4f;
	weightGrass = 0.2f;
	weightAir = 0.1f;
    }
    
    void randomizeWeights(){
	Random rand = new Random();
	float value = rand.nextFloat();

	weightBuilding += rand.nextFloat();
	//weightBuilding = 0.0f;
	weightProp += rand.nextFloat();
	//weightProp = 0.0f;
	weightRoad += rand.nextFloat();
	//weightRoad = 0.0f;
	weightGrass += rand.nextFloat();
	//weightGrass = 0.0f;
	weightAir += rand.nextFloat();
	//weightAir = 0.0f;
    }

    void fillWeightArray() {
	weightsArr[0] = weightRoad;
	weightsArr[1] = weightBuilding;
	weightsArr[2] = weightProp;
	weightsArr[3] = weightGrass;
	weightsArr[4] = weightAir;
    }

    void normalizeWeights() {
	//int numberOfWeights = 5;
	float roadPercent = 0;
	float buildingPercent = 0;
	float propPercent = 0;
	float grassPercent = 0;
	float airPercent = 0;
	
	float totalValue =
		weightRoad
		+ weightBuilding
		+ weightProp
		+ weightGrass
		+ weightAir;
	
	roadPercent = weightRoad/totalValue;
	buildingPercent = weightBuilding/totalValue;
	propPercent = weightProp/totalValue;
	grassPercent = weightGrass/totalValue;
	airPercent = weightAir/totalValue;
	
//	System.out.println("CityNode.normalizeWeights - percentages");
//	System.out.println("CityNode.normalizeWeights - roadPercent     - " + roadPercent);
//	System.out.println("CityNode.normalizeWeights - buildingPercent - " + buildingPercent);
//	System.out.println("CityNode.normalizeWeights - propPercent     - " + propPercent);
//	System.out.println("CityNode.normalizeWeights - grassPercent    - " + grassPercent);
//	System.out.println("CityNode.normalizeWeights - airPercent      - " + airPercent);
	
	weightRoad = roadPercent;
	weightBuilding = buildingPercent;
	weightProp = propPercent;
	weightGrass = grassPercent;
	weightAir = airPercent;
	
    }
    
    boolean edge = false;
    public void setCityEdge(boolean edge){
	this.edge = edge;
    }

    boolean roadReWeighted = false;
    
    public void doWeights() {

	randomizeWeights();
	
	normalizeWeights();

	fillWeightArray();

	//System.out.println("Node.doWeights - float: " + value);

	//globalWeights[0] = (1.0f/(0.5f+0.4f+0.3f+0.2f+0.1f))*globalWeights[0];
	//globalWeights[1] = (1.0f/(0.5f+0.4f+0.3f+0.2f+0.1f))*globalWeights[1];
	//globalWeights[2] = (1.0f/(0.5f+0.4f+0.3f+0.2f+0.1f))*globalWeights[2];
	//globalWeights[3] = (1.0f/(0.5f+0.4f+0.3f+0.2f+0.1f))*globalWeights[3];
	//globalWeights[4] = (1.0f/(0.5f+0.4f+0.3f+0.2f+0.1f))*globalWeights[4]; 

	//weightBuilding = globalWeights[0];
	//weightProp = globalWeights[1];
	//weightRoad = globalWeights[2];
	//weightGrass = globalWeights[3];
	//weightAir = globalWeights[4];

	float dist = Math.abs(weightsArr[0] - 1);
	float tempDist = 0;
	int index = 0;

	for (int i = 0; i < weightsArr.length; i++) {
	    tempDist = Math.abs(weightsArr[i] - 1);
	    if (tempDist < dist) {
		//System.out.println("CityNode.doWeights - BEFORE tempDist " + tempDist + " dist " + dist);
		dist = tempDist;
		index = i;
		//System.out.println("CityNode.doWeights - AFTER tempDist " + tempDist + " dist " + dist);
		//System.out.println("CityNode.doWeights - index picked " + index);
	    }
	}
	
	ArrayList<CityNode> tempRoadList = new ArrayList();
	for(int i=0;i<surroundingNodes.size();i++){
	    if(surroundingNodes.get(i).getType() == Type.ROAD){
		tempRoadList.add(surroundingNodes.get(i));
	    }
	}
	if((tempRoadList.size() == 2 )
		//|| tempRoadList.size() == 1)
		&& pos.getY() == 0){
//	    weightRoad *= 100;
//	    if(!roadReWeighted){
//		roadReWeighted = true;
//		doWeights();
//	    }
	    index = 0;
	}
	
	if(edge
		&& pos.getY() == 0){
	    index = 0;
	}

//	if (weightsArr[index] == 0.0f) {
//	    System.out.println("CityNode.doWeights - re-doing weights");
//	    doWeights();
//	}

	//System.out.println("CityNode.doWeights - setting type ");
	//System.out.println("CityNode.doWeights - value " + value);
	//System.out.println("CityNode.doWeights - road picked");
	switch (index) {
	    case 0:
		//road
		//System.out.println("CityNode.doWeights - road picked");
		type = Type.ROAD;
		break;
	    case 1:
		//building
		//System.out.println("CityNode.doWeights - building picked");
		type = Type.BUILDING;
		break;
	    case 2:
		//prop
		//System.out.println("CityNode.doWeights - prop picked");
		type = Type.PROP;
		break;
	    case 3:
		//grass
		//System.out.println("CityNode.doWeights - grass picked");
		type = Type.GRASS;
		break;
	    case 4:
		//air
		//System.out.println("CityNode.doWeights - air picked");
		type = Type.AIR;
		break;
	}

//	if (value > 0.8f && value <= 1.0f) {
//	    type = Type.AIR;
//	}
//	if (value > 0.6f && value <= 0.8f) {
//	    type = Type.GRASS;
//	}
//	if (value > 0.4f && value <= 0.6f) {
//	    type = Type.ROAD;
//	}
//	if (value > 0.2f && value <= 0.4f) {
//	    type = Type.PROP;
//	}
//	if (value >= 0.0f && value <= 0.2f) {
//	    type = Type.BUILDING;
//	}
//	if(value > 0.8f && value <= 1.0f){
//	    type = Type.AIR;
//	}
//	if(value > 0.6f && value <= 0.8f){
//	    type = Type.GRASS;
//	}
//	if(value > 0.4f && value <= 0.6f){
//	    type = Type.ROAD;
//	}
//	if(value > 0.2f && value <= 0.4f){
//	    type = Type.PROP;
//	}
//	if(value >= 0.0f && value <= 0.2f){
//	    type = Type.BUILDING;
//	}

	checkType();
    }

    public void checkType() {
	//no grass past ground level (y = 0)
	if (pos.getY() >= 1 && (type == Type.GRASS || type == Type.ROAD)) {
	    doWeights();
	}
	//no air at ground level (y = 0)
	if (pos.getY() == 0 && type == Type.AIR) {
	    doWeights();
	}
	//no props past level 2 (y = 2)
	if (pos.getY() >= 2 && type == Type.PROP) {
	    doWeights();
	}
	//lose global weighting
//	System.out.println("CityNode.checkType - typeset " + type);
//	if(pos.getX() != 0){
//	    try{
//		Thread.sleep(1000);
//	    }catch(Exception e){
//		
//	    }
//	}
	switch (type) {
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

	if (underNode != null) {
	    //System.out.println("CityNode.checkType - typeset " + type);
	    //System.out.println("CityNode.checktype - undernode " + underNode.getType());
	}
    }

    public Type getType() {
	return type;
    }

    void doBuilding() {
	//System.out.println("Node.doBuilding");
	if (underNode != null) {
	    //System.out.println("Node.doBuilding - under node type: " + underNode.getType());
	    if (underNode.getType() != Type.BUILDING) {
		type = Type.AIR;
		//System.out.println("Node.doBuilding - under is not building");
	    } else {
		type = Type.BUILDING;
	    }
	} else {
	    //System.out.println("Node.doBuilding - under is null");
	}
    }

    void setUnderNode(CityNode underNode) {
	this.underNode = underNode;
	//doWeights();
    }
    
    void addBuildingBlocks(CityNode node){
	ArrayList<CityNode> nodesBlocks = node.getBuildingBlocks();
	ArrayList<CityNode> blocksToBeAdded = new ArrayList<CityNode>();
	
	if(!buildingBlocks.contains(node)){
	    blocksToBeAdded.add(node);
	}
	
	for(int i=0;i<nodesBlocks.size();i++){
	    CityNode tempNode = nodesBlocks.get(i);
	    if(!buildingBlocks.contains(tempNode)){
		blocksToBeAdded.add(tempNode);
	    }
	}
	
	buildingBlocks.addAll(blocksToBeAdded);
    }
    
    ArrayList<CityNode> getBuildingBlocks(){
	return buildingBlocks;
    }

    void setSurroundingNodes(ArrayList nodes) {
	if (nodes != null) {
	    surroundingNodes.clear();
	    surroundingNodes.addAll(nodes);
	    for (int i = 0; i < surroundingNodes.size(); i++) {
		if (surroundingNodes.get(i).getPos().getX() == this.getPos().getX()
			&& surroundingNodes.get(i).getPos().getY() == this.getPos().getY() - 1
			&& surroundingNodes.get(i).getPos().getZ() == this.getPos().getZ()) {
		    underNode = surroundingNodes.get(i);
		    //System.out.println("CityNode.setUnderNode - undernode set " + underNode.getType());
		} else {
		    //underNode = null;
		}
	    }
	} else {
	}
	if(!generated){
	    doWeights();
	    generated = true;
	}
    }
    
    boolean generated = false;

    ArrayList<CityNode> getSurroundingNodes() {
	return surroundingNodes;
    }
    
    void checkForBrethren(){
	if(type != type.AIR){
	    //System.out.println("CityNode.checkForBrethren type " + type);
	}
	buildingBlocks.clear();
	if(type != type.AIR){
	    for(int i=0;i<surroundingNodes.size();i++){
		//System.out.println("CityNode.checkForBrethren - in loop");
		//System.out.println("CityNode.checkForBrethren - checking against type " + surroundingNodes.get(i).getType());
		if(surroundingNodes.get(i).getType().equals(this.type)
			&& !buildingBlocks.contains(surroundingNodes.get(i))){
		    buildingBlocks.add(surroundingNodes.get(i));
		    //surroundingNodes.get(i).checkForBrethren();
		    checkNodeForSiblings(surroundingNodes.get(i));
		    //System.out.println("CityNode.checkForBrethren - match! " + type + " to " + surroundingNodes.get(i).getType());
		}
	    }
	}
	if(type != type.AIR){
	    //System.out.println("CityNode.checkForBrethren types");

	    //System.out.print("CityNode.checkForBrethren ");
	    for(int i=0;i<buildingBlocks.size();i++){
		//System.out.print(buildingBlocks.get(i).getType() + " ");
	    }
	    //System.out.println("");
	}
    }
    
    void checkNodeForSiblings(CityNode node){
	CityNode tempNode = null;
	for(int j=0;j<node.getBuildingBlocks().size();j++){
	    tempNode = node.getBuildingBlocks().get(j);
	    this.addSibling(tempNode);
	    //System.out.println("CityNode.checkForSiblings - adding temp node");
	}
    }
    
    void addSibling(CityNode node){
	if(!buildingBlocks.contains(node)){
	    //System.out.println("CityNode.addSibling - adding sibling manually");
	    buildingBlocks.add(node);
	    node.addSibling(this);
	    checkNodeForSiblings(node);
	}
    }

    void doProp() {
	if (underNode != null) {
	    //System.out.println("Node.doBuilding - under node type: " + underNode.getType());
	    if (underNode.getType() != Type.PROP) {
		type = Type.AIR;
		//System.out.println("Node.doBuilding - under is not building");
	    } else {
		type = Type.PROP;
	    }
	} else {
	    //System.out.println("Node.doBuilding - under is null");
	}
    }

    void doRoad() {
	type = Type.ROAD;
    }

    void doGrass() {
	type = Type.GRASS;
    }

    void doAir() {
	type = Type.AIR;
    }

    public Vector3f getPos() {
	return this.pos;
    }

    public void setGlobals(float[] globals) {
	if (globals.length == 5) {
	    globalWeights = globals;
	    weightBuilding = globals[0];
	    weightProp = globals[1];
	    weightRoad = globals[2];
	    weightGrass = globals[3];
	    weightAir = globals[4];
	} else {
	    System.out.println("CityGen.Node.setGlobals - Invalid number of global weights!");
	}
    }
    
    ArrayList<Geometry> surroundingNodeBlocks = new ArrayList<Geometry>();
    Geometry block = null;
    
    void setBlock(Geometry block){
	this.block = block;
	this.pos.set(block.getWorldTranslation());
    }
    
    Geometry getBlock(){
	return block;
    }
    
    void setType(Type type){
	this.type = type;
    }
    
}
