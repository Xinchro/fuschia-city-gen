package CityGen;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 *
 * @author Xinchro
 */
public class City extends SimpleApplication implements RawInputListener {

    float globalWeightBuilding = (1.0f / (0.5f + 0.4f + 0.3f + 0.2f + 0.1f)) * 0.5f;
    float globalWeightProp = (1.0f / (0.5f + 0.4f + 0.3f + 0.2f + 0.1f)) * 0.4f;
    float globalWeightRoad = (1.0f / (0.5f + 0.4f + 0.3f + 0.2f + 0.1f)) * 0.3f;
    float globalWeightGrass = (1.0f / (0.5f + 0.4f + 0.3f + 0.2f + 0.1f)) * 0.2f;
    float globalWeightAir = (1.0f / (0.5f + 0.4f + 0.3f + 0.2f + 0.1f)) * 0.1f;
    //float globals[] = new float[5];
    float[] globals = {globalWeightBuilding, globalWeightProp, globalWeightRoad, globalWeightGrass, globalWeightAir};
    int width, length, height;
    ArrayList<CityNode> nodes = new ArrayList<CityNode>();
    ArrayList<CityNode> renderableNodes = new ArrayList<CityNode>();
    ArrayList<Geometry> nodeCubes = new ArrayList<Geometry>();
    ArrayList<Geometry> renderableCubes = new ArrayList<Geometry>();
    boolean closed = false;

    public static void main(String[] args) {
	City app = new City();
	AppSettings settings = new AppSettings(true);
	app.setPauseOnLostFocus(false);
	app.setSettings(settings);
	app.settings.setUseJoysticks(true);
	app.setShowSettings(false);
	app.setPauseOnLostFocus(false);
	app.setDisplayFps(true);
	app.setDisplayStatView(true);
	app.settings.setTitle("FUSCHIA City Generator");
	app.settings.setWidth(800);
	app.settings.setHeight(600);
	app.settings.setFrameRate(60);
	app.start();
    }

    @Override
    public void simpleInitApp() {
	//System.out.println("City - globalWeightBuilding: " + globalWeightBuilding);
	//System.out.println("City - glo    balWeightProp: " + globalWeightProp);
	//System.out.println("City - globalWeightRoad: " + globalWeightRoad);
	//System.out.println("City - globalWeightGrass: " + globalWeightGrass);
	//System.out.println("City - globalWeightAir: " + globalWeightAir);
	//System.out.println("City - globalWeight Total: "+(globalWeightBuilding+globalWeightProp+globalWeightRoad+globalWeightGrass+globalWeightAir));
	this.inputManager.addRawInputListener(this);
	java.util.logging.Logger.getLogger("").setLevel(Level.SEVERE);
	flyCam.setDragToRotate(true);
	flyCam.setMoveSpeed(100);
	
	debugText();
	//flyCam.setEnabled(false);
	cam.setLocation(new Vector3f(width + 20.0f, height + 10.0f, length + 20.0f));
	cam.lookAt(new Vector3f(width / 2, 0, length / 2), Vector3f.UNIT_Y);
	//throw new UnsupportedOperationException("Not supported yet.");
	/**
	 * Translucent/transparent cube. Uses Texture from jme3-test-data
	 * library!
	 */
//	Box boxshape3 = new Box(Vector3f.ZERO, 1f,1f,1f);
//	Geometry cube_translucent = new Geometry("translucent cube", boxshape3);
//	Material mat_tt = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//	//mat_tt.setTexture("ColorMap", assetManager.loadTexture("Textures/ColoredTex/Monkey.png"));
//	mat_tt.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
//	mat_tt.setColor("Color", new ColorRGBA(1f,1f,1f, 1f));
//	cube_translucent.setMaterial(mat_tt); 
//	rootNode.attachChild(cube_translucent); 
	viewPort.setBackgroundColor(ColorRGBA.Gray);
	cam.setFrustumFar(100);
	executor.submit(doNodes);
	//addSurroundingNodes();
	//nodeLoop();

	//GeometryBatchFactory fact = new GeometryBatchFactory();//.optimize(rootNode);
    }

    public City() {
	CityNode node = new CityNode();
	node.setGlobals(globals);

	width = 50;
	length = width;
	height = 5;
    }
    ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);

    //@Override
    public void destroy() {
	//executor.shutdown();
	closed = true;
	executor.shutdownNow();
	//executor.purge();
	//this.destroy();
	super.destroy();
    }

    
    boolean finished = false;
    
    @Override
    public void simpleUpdate(float tpf) {
	super.simpleUpdate(tpf);

	//executor.submit(doNodes);
	rootNode.detachAllChildren();
	for (int i = 0; i < renderableCubes.size()-1; i++) {
	    if(renderableNodes.get(i).type == CityNode.Type.AIR){
		renderableCubes.remove(i);
		renderableNodes.remove(i);
	    }else{
		//if(finished){
		    rootNode.attachChild(renderableCubes.get(i));
		    //reSkinNode(renderableCubes.get(i));
		//}
	    }
	}
	
	setDebugTextX(String.valueOf(x));
	setDebugTextY(String.valueOf(y));
	setDebugTextZ(String.valueOf(z));
	
	if(highBros){
	    highlightBrethren();
	}else if(highSurrs){
	    highlightSurroundingNodes();
	}else{
	    resetNodeScales();
	}
	//GeometryBatchFactory.optimize(rootNode);

//	if(y<height){
//	    if(x<width){
//		if(z<length){
//		    Node node = new Node(new Vector3f(x, y, z));
//		    node.setUnderNode(getSurroundingNode(
//			    new Vector3f(node.getPos().getX(), node.getPos().getY()-1, node.getPos().getZ())));
//		    makeCubeAt(node.getPos(), node.getType());
//		    nodes.add(node);
//		    nodes.trimToSize();
//		    z++;
//		    //System.out.println("City.simpleUpdate - z tick");
//		}else{
//		    z = 0;
//		    x++;
//		}
//	    }else{
//		x = 0;
//		y++;
//	    }
//	}

    }
    
    void reSkinNode(Geometry nodeCube){
	CityNode pickedNode = null;
	CityNode.Type type = CityNode.Type.AIR;
	
	for(int i=0;i<nodes.size();i++){
	    //if(nodes.get(i).getPos().distance(clickedGeo.getWorldTranslation())==0){
	    if(nodes.get(i).getBlock().getWorldTranslation().getX()==nodeCube.getWorldTranslation().getX()
		    && nodes.get(i).getBlock().getWorldTranslation().getY()==nodeCube.getWorldTranslation().getY()
		    && nodes.get(i).getBlock().getWorldTranslation().getZ()==nodeCube.getWorldTranslation().getZ()){
		pickedNode = nodes.get(i);
	    }
	}
	
	type = pickedNode.getType();
	
	switch (type) {
	    case BUILDING:
		pickedNode.getBlock().getMaterial().setColor("Color", ColorRGBA.Red);
		break;
	    case PROP:
		pickedNode.getBlock().getMaterial().setColor("Color", ColorRGBA.Blue);
		break;
	    case ROAD:
		pickedNode.getBlock().getMaterial().setColor("Color", ColorRGBA.Black);
		break;
	    case GRASS:
		pickedNode.getBlock().getMaterial().setColor("Color", ColorRGBA.Green);
		break;
	    case AIR:
		pickedNode.getBlock().getMaterial().setColor("Color", ColorRGBA.White);
		break;
	    default:
		System.out.println("City.makeCubeAt - null type");
		break;
	}
    }
    
    float x = 0, y = 0, z = 0;
    
    private Callable doNodes = new Callable() {
	//return this;
	@Override
	public String call() {
	    System.out.println("City.doNodes - making city");
	    finished = false;

	    
	    //enqueue(
	    //new Callable(){
	    //public String call(){
	    while (y < height && !closed) {
		while (x < width && !closed) {
		    while (z < length && !closed) {

			CityNode node = new CityNode(new Vector3f(x, y, z));
//			node.setUnderNode(getSurroundingNode(
//				new Vector3f(node.getPos().getX(), node.getPos().getY() - 1, node.getPos().getZ())));

			if((x == 0 || x==width-1)
				||(z == 0 || z ==length-1)){
			    node.setCityEdge(true);
			}
			node.setSurroundingNodes(getSurroundingNodes(node));
			nodes.add(node);
			node.setBlock(makeCubeAt(node.getPos(), node.getType()));   
			if(node.getType() != node.type.AIR){
			    renderableNodes.add(node);
			}
			nodes.trimToSize();
			renderableNodes.trimToSize();
			z++;
			//System.out.println("City.simpleUpdate - pos: " +x+" "+y+" "+z);
			//System.out.println("City.simpleUpdate - z tick");
			//}else{
			//z = 0;
			//x++;
		    }
		    x++;
		    z = 0;
		    //}else{
		    //x = 0;
		    //y++;
		}
		ArrayList<CityNode> tempNodeArr = new ArrayList<CityNode>();
		for(int a=0;a<nodes.size();a++){
		    CityNode tempNode = nodes.get(a);
		    if(tempNode.getType() == CityNode.Type.AIR
			    && tempNode.getPos().getY() == y
			    && !closed){
			tempNodeArr.add(tempNode);			
		    }
		}
		if(tempNodeArr.size() >= width*length && !closed){
		    System.out.println("City.doNodes - full air layer");
		    float tempY = height - y;
		    while(y < height && !closed){
			while (x < width && !closed) {
			    while (z < length && !closed) {
				CityNode node = new CityNode(new Vector3f(x, y, z));
				node.setType(CityNode.Type.AIR);
				nodes.add(node);
				nodes.trimToSize();
				z++;
			    }
			    x++;
			}
			y++;
		    }
	    	    break;
		}
		y++;
		x = 0;
		try{
		    //Thread.sleep(1000);
		}catch(Exception e){

		}
	    }
	    //return "a";
	    //}});
	    if(enableBrethren){
		for(int i=0;i<nodes.size();i++){
		    if(!closed){
			nodes.get(i).setSurroundingNodes(getSurroundingNodes(nodes.get(i)));
			//System.out.println("City.doNodes - getting nodes");
		    }
		}
	    }

//	    System.out.println("City.doNodes - node " + nodes.get(nodes.size()/2).getPos() + " " + nodes.get(nodes.size()/2).getType());
//	    for(int i=0;i<nodes.get(nodes.size()/2).getSurroundingNodes().size();i++){
//		System.out.println("City.doNodes - sur nodes " + nodes.get(nodes.size()/2).getSurroundingNodes().get(i).getPos()
//			+ " " + 
//			nodes.get(nodes.size()/2).getSurroundingNodes().get(i).getType());
//	    }
//	    System.out.println("City.doNodes - sur nodes " + nodes.get(nodes.size()/2).getSurroundingNodes());

//	    for(int i=0;i<nodes.size();i++){
//		nodes.get(i).setSurroundingNodes(getSurroundingNodes(nodes.get(i)));
//		//nodes.get(i).checkForBrethren();
//	    }
	    if(enableBrethren){
		for(int i=0;i<nodes.size();i++){
		    if(!closed){
			nodes.get(i).checkForBrethren();
		    }
		}
	    }
	    
	    //executor.shutdown();
	    //executor.purge();
	    System.out.print("City.doNodes - arrays: ");
	    System.out.print(" renderableNodes " + renderableNodes.size());
	    System.out.print(" renderableCubes " + renderableCubes.size());
	    System.out.print(" nodeCubes " + nodeCubes.size());
	    System.out.print(" nodes " + nodes.size());
	    System.out.println("");
	    finished = true;
	    x=0;
	    y=0;
	    z=0;
	    return "Finished";
	}
    };

    public ArrayList<CityNode> getSurroundingNodes(CityNode node) {
	CityNode tempNode;
	Vector3f tempPos;
	Vector3f nodePos = node.getPos();
	ArrayList<CityNode> surNodes = new ArrayList<CityNode>();

	float tempX = 0, tempY = 0, tempZ = 0;

	//System.out.println("City.getSurroundingNodes - getting surrounding nodes =======");
	//System.out.println("City.getSurroundingNodes - node " + nodePos + " " + node.getType());
	
	boolean minusoneAdded = false;
	boolean zeroAdded = false;
	boolean plusoneAdded = false;
	
	int doX = -1;
	int doY = -1;
	int doZ = -1;

	for (int i = 0; i < nodes.size(); i++) {
	    if (closed) {
		break;
	    }
	    for(int j=0;j<27;j++){
		tempNode = nodes.get(i);
		tempPos = tempNode.getPos();

//		System.out.println("City.getSurroundingNodes - the do-s "
//			+ doX 
//			+ " " 
//			+ doY
//			+ " "
//			+ doZ);

		if (tempPos.getX() + doX == nodePos.getX()
			    && tempPos.getY() + doY == nodePos.getY()
			    && tempPos.getZ() + doZ == nodePos.getZ()) {
		//System.out.println("City.getSurroundingNodes - node match " + tempPos + " type " + tempNode.getType());
		    if((tempPos.getX() + doX == 0)&&(tempPos.getY() + doY == 0)&&(tempPos.getZ() + doZ ==0)){
		    }else{
			surNodes.add(tempNode);
		    }
		}else{
//    			System.out.println("City.getSurroundingNodes - adding " + tempPos.getX() 
//    				+ " "
//    				+ tempPos.getY() 
//    				+ " " 
//    				+ tempPos.getZ() 
//    				+ " ");
		    

		}

		doZ++;
		if(doX<2){
		    if(doY<2){
			if(doZ<2){
			}else{
			    doZ=-1;
			    doY++;
			}
		    }else{
			doY=-1;
			doX++;
		    }
		}else{
		    doX = -1;
		}
	    }
	}
	
	
//	for (int i = 0; i < nodes.size(); i++) {
//	    if (closed) {
//		break;
//	    }
//	    tempNode = nodes.get(i);
//	    tempPos = tempNode.getPos();
//
//	    
//	    //System.out.println("City.getSurroundingNodes - adding to" + nodePos);
//	    
//	    //would normally be 27, for the 27 nodes can "only check" 18
//	    //but 3 nodes (-1,-1,-1) (1,1,1) are repeated 3 times
//	    //also should not include itself (0,0,0) (1 of the 3 mentioned above)
//	    
//	    //map for nodes checking
//	    //(-1,-1,-1) (0,-1,-1) (1,-1,-1) (1 to 3)
//	    //(-1,-1,-1) (-1,0,-1) (-1,1,-1) (4 to 6)
//	    //(-1,-1,-1) (-1,-1,0) (-1,-1,1) (7 to 9)
//	    
//	    //(-1,0,0) (0,0,0) (1,0,0) (10 to 12)
//	    //(0,-1,0) (0,0,0) (0,1,0) (13 to 15)
//	    //(0,0,-1) (0,0,0) (0,0,1) (16 to 18)
//	    
//	    //(-1,1,1) (0,1,1) (1,1,1) (19 to 21)
//	    //(1,-1,1) (1,0,1) (1,1,1) (21 to 24)
//	    //(1,1,-1) (1,1,0) (1,1,1) (25 to 27)
//	    
//	    //specially added:
//	    //(-1,-1,-1) (0,0,0) (1,1,1) (makes total 21) (stop adding itself! (0,0,0))
//	    //(0,-1,1) (-1,0,1) (-1,1,0) (makes total 24)
//	    //(0,1,-1) (1,0,-1) (1,-1,0) (makes total 27) (26 without itself)
//	    
//	    for (int j = 0; j < 27; j++) {
//		if (closed) {
//		    break;
//		}
//		//System.out.println("City.getSurroundingNodes - j " + j + " " + "jmod3 " + ((j % 3) - 1));
//		if (j <= 2) {
//		    tempX = (j % 3) - 1;
//		    tempY = -1;
//		    tempZ = -1;
//		    //System.out.println("1 to 3");
////		    System.out.println("City.getSurroundingNodes - pos offset " + tempX
////			    + " "
////			    + tempY
////			    + " " 
////			    + tempZ
////			    + " ");
//		} else if (j <= 5) {
//		    tempX = -1;
//		    tempY = (j % 3) - 1;
//		    tempZ = -1;
//		    //System.out.println("4 to 6");
//		} else if (j <= 8) {
//		    tempX = -1;
//		    tempY = -1;
//		    tempZ = (j % 3) - 1;
//		    //System.out.println("7 to 9");
//		} else if (j <= 11) {
//		    tempX = (j % 3) - 1;
//		    tempY = 0;
//		    tempZ = 0;
//		    //System.out.println("10 to 12");
//		} else if (j <= 14) {
//		    tempX = 0;
//		    tempY = (j % 3) - 1;
//		    tempZ = 0;
//		    //System.out.println("13 to 15");
//		} else if (j <= 17) {
//		    tempX = 0;
//		    tempY = 0;
//		    tempZ = (j % 3) - 1;
//		    //System.out.println("16 to 18");
//		} else if (j <= 20) {
//		    tempX = (j % 3) - 1;
//		    tempY = 1;
//		    tempZ = 1;
//		    //System.out.println("19 to 21");
//		} else if (j <= 23) {
//		    tempX = 1;
//		    tempY = (j % 3) - 1;
//		    tempZ = 1;
//		    //System.out.println("22 to 24");
//		} else if (j <= 26) {
//		    tempX = 1;
//		    tempY = 1;
//		    tempZ = (j % 3) - 1;
//		    //System.out.println("25 to 27");
////		    System.out.println("City.getSurroundingNodes - pos offset " + tempX
////			    + " "
////			    + tempY
////			    + " " 
////			    + tempZ
////			    + " ");
//		}
//
//		if (tempPos.getX() + tempX == nodePos.getX()
//			&& tempPos.getY() + tempY == nodePos.getY()
//			&& tempPos.getZ() + tempZ == nodePos.getZ()) {
//		    //System.out.println("City.getSurroundingNodes - node match " + tempPos + " type " + tempNode.getType());
//		    if((tempX == -1 && tempY == -1 && tempZ == -1)
//			    || (tempX == 0 && tempY == 0 && tempZ == 0)
//			    || (tempX == 1 && tempY == 1 && tempZ == 1)){
//			
//			
//			if(!minusoneAdded){
//			    if(tempX == -1 && tempY == -1 && tempZ == -1){
////				System.out.println("City.getSurroundingNodes - adding " + tempPos.getX() 
////					+ " "
////					+ tempPos.getY() 
////					+ " " 
////					+ tempPos.getZ() 
////					+ " ");
//				surNodes.add(tempNode);
//				minusoneAdded = true;
//			    }
//			}
//			//stop adding itself!
////			if(!zeroAdded){
////			    if(tempX == 0 && tempY == 0 && tempZ == 0){
//////				System.out.println("City.getSurroundingNodes - adding " + tempPos.getX() 
//////					+ " "
//////					+ tempPos.getY() 
//////					+ " " 
//////					+ tempPos.getZ() 
//////					+ " ");
////				surNodes.add(tempNode);
////				zeroAdded = true;
////			    }
////			}
//			if(!plusoneAdded){
//			    if(tempX == 1 && tempY == 1 && tempZ == 1){
////				System.out.println("City.getSurroundingNodes - adding " + tempPos.getX() 
////					+ " "
////					+ tempPos.getY() 
////					+ " " 
////					+ tempPos.getZ() 
////					+ " ");
//				surNodes.add(tempNode);
//				plusoneAdded = true;
//			    }
//			}
//			
//			
//		    }else{
////			System.out.println("City.getSurroundingNodes - adding " + tempPos.getX() 
////				+ " "
////				+ tempPos.getY() 
////				+ " " 
////				+ tempPos.getZ() 
////				+ " ");
//			surNodes.add(tempNode);
//
//		    }
//		} else {
//		    //System.out.println("City.getSurroundingNodes - dud " + tempX + " " + tempY + " " + tempZ);
//		}
//
//	    }
//
//
////	    if ((tempPos.getX() == nodePos.getX() - 1 || tempPos.getX() == nodePos.getX() || tempPos.getX() == nodePos.getX() + 1)
////		    && (tempPos.getY() == nodePos.getY() - 1 || tempPos.getY() == nodePos.getY() || tempPos.getY() == nodePos.getY() + 1)
////		    && (tempPos.getZ() == nodePos.getZ() - 1 || tempPos.getZ() == nodePos.getZ() || tempPos.getZ() == nodePos.getZ() + 1)
////		    && (tempPos.getX() != nodePos.getX() && tempPos.getY() != nodePos.getY() && tempPos.getZ() != nodePos.getZ())) {
////		System.out.println("City.getSurroundingNodes - tempPos " + tempPos);
////		System.out.println("City.getSurroundingNodes - tempNodeType " + tempNode.getType());
////	    }
////	    if ((tempPos.getX() == nodePos.getX() - 1 || tempPos.getX() == nodePos.getX() || tempPos.getX() == nodePos.getX() + 1)
////		    && (tempPos.getY() == nodePos.getY() - 1 || tempPos.getY() == nodePos.getY() || tempPos.getY() == nodePos.getY() + 1)
////		    && (tempPos.getZ() == nodePos.getZ() - 1 || tempPos.getZ() == nodePos.getZ() || tempPos.getZ() == nodePos.getZ() + 1)
////		    && (tempPos.getX() != nodePos.getX() && tempPos.getY() != nodePos.getY() && tempPos.getZ() != nodePos.getZ())) {
////		//System.out.println("City.getSurroundingNodes - tempPos " + tempPos);
////		//System.out.println("City.getSurroundingNodes - tempNodeType " + tempNode.getType());
////	    }
//	}
	//System.out.println("City.getSurroundingNodes - endding surrounding nodes =======");

	return surNodes;
    }

    public void nodeLoop() {
	float x = 0, y = 0, z = 0;
	for (int a = 0; a < height; a++) {
	    for (int b = 0; b < width; b++) {
		for (int c = 0; c < length; c++) {
		    CityNode node = new CityNode(new Vector3f(x, y, z));
//		    node.setUnderNode(getSurroundingNode(
//			    new Vector3f(node.getPos().getX(), node.getPos().getY() - 1, node.getPos().getZ())));
		    makeCubeAt(node.getPos(), node.getType());
		    nodes.add(node);
		    nodes.trimToSize();
		    z++;
		}
		z = 0;
		x++;
	    }
	    x = 0;
	    y++;
	}
    }

    int cubeIndex = 0;
    
    public Geometry makeCubeAt(Vector3f pos, CityNode.Type type) {
	Box boxshape3 = new Box(new Vector3f(0,0,0), 0.5f, 0.5f, 0.5f);
	cubeIndex++;
	Geometry cube_translucent = new Geometry(type + " " + String.valueOf(cubeIndex), boxshape3);
	cube_translucent.move(pos);
	Material mat_tt = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
	//mat_tt.setTexture("ColorMap", assetManager.loadTexture("Textures/ColoredTex/Monkey.png"));
	mat_tt.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
	//mat_tt.getAdditionalRenderState().setWireframe(true);
	switch (type) {
	    case BUILDING:
		mat_tt.setColor("Color", ColorRGBA.Red);
		break;
	    case PROP:
		mat_tt.setColor("Color", ColorRGBA.Blue);
		break;
	    case ROAD:
		mat_tt.setColor("Color", ColorRGBA.Black);
		break;
	    case GRASS:
		mat_tt.setColor("Color", ColorRGBA.Green);
		break;
	    case AIR:
		mat_tt.setColor("Color", ColorRGBA.White);
		break;
	    default:
		System.out.println("City.makeCubeAt - null type");
		break;
	}
	cube_translucent.setMaterial(mat_tt);
	nodeCubes.add(cube_translucent);
	if (type != CityNode.Type.AIR) {
	    //rootNode.attachChild(cube_translucent);
	    renderableCubes.add(cube_translucent);
	}
	
	return cube_translucent;
    }

    @Override
    public void beginInput() {
	//throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void endInput() {
	//throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onJoyAxisEvent(JoyAxisEvent evt) {
	//throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onJoyButtonEvent(JoyButtonEvent evt) {
	//throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onMouseMotionEvent(MouseMotionEvent evt) {
	//throw new UnsupportedOperationException("Not supported yet.");
	//System.out.println("City.onMouseMotionEvent - move");
	//resetNodeScales();
    }

    
    Geometry clickedGeo = null;
    ArrayList<Geometry> clickedGeos = new ArrayList<Geometry>();
    CityNode clickedNode = null;
    
    Vector2f mouseCoords;
    Vector3f mouseWorldCoords;
    Vector3f dir;
    
    @Override
    public void onMouseButtonEvent(MouseButtonEvent evt) {
	//throw new UnsupportedOperationException("Not supported yet.");
	//System.out.println("City.onMouseButtonEvent - click " + evt.getButtonIndex());

	//lookAtMouse(this.board.player.currentPiece.cube);

	switch (evt.getButtonIndex()) {
	    case 0:
		break;
	    case 1:
		if (evt.isPressed()) {
		    //highlightBrethren();
		    highBros = true;
		}
		if(evt.isReleased())
		{
		    highBros = false;
		    //resetNodeScales();
		}
		break;
	    case 2:
		if (evt.isPressed()) {
		    //highlightSurroundingNodes();
		    highSurrs = true;

		}
		if(evt.isReleased())
		{
		    highSurrs = false;
		    //resetNodeScales();
		}
		break;
	}
    }

    @Override
    public void onKeyEvent(KeyInputEvent evt) {
	//throw new UnsupportedOperationException("Not supported yet.");
	//System.out.println("City.onKeyEvent - " + evt.getKeyCode());
	if(evt.isPressed()){
	    switch(evt.getKeyCode()){
//		for(int i=0;i<10;   i++){
//		    System.out.println("City.onKeyEvent - spacebar " + nodes.get(i).getType());
//		}
		case KeyInput.KEY_SPACE:
			//nodes.get(i).setSurroundingNodes(getSurroundingNodes(nodes.get(i)));
			executor.submit(
				new Callable(){ 
				    
				    @Override
				    public String call() {
					for(int i=0;i<nodes.size();i++){
					    nodes.get(i).setSurroundingNodes(getSurroundingNodes(nodes.get(i)));
					    nodes.get(i).checkForBrethren();
					}
					return "";
				    }});
			//nodes.get(i).setSurroundingNodes(getSurroundingNodes(nodes.get(i)));
			//nodes.get(i).checkForBrethren();
		    //}
		    break;
		case KeyInput.KEY_K:
		    if(executor.getActiveCount()<=0){
			System.out.println("City.onKeyEvent - clearing arrays");
			renderableNodes.clear();
			renderableCubes.clear();
			nodeCubes.clear();
			nodes.clear();
			System.out.print("City.onKeyEvent - arrays: ");
			System.out.print(" renderableNodes " + renderableNodes.size());
			System.out.print(" renderableCubes " + renderableCubes.size());
			System.out.print(" nodeCubes " + nodeCubes.size());
			System.out.print(" nodes " + nodes.size());
			System.out.println("");
			//if(executor.isShutdown()){
			    //executor = new ScheduledThreadPoolExecutor(10);
			    executor.submit(doNodes);
			//}else{
			    //System.out.println("City.onKeyEvent - exectuor still alive");
			//}
		    }
		    break;
		case KeyInput.KEY_L:
		    if(enableBrethren){
			enableBrethren = false;
		    }else{
			enableBrethren = true;
		    }
		    break;
	    }
	}
    }

    boolean enableBrethren = false;
    
    @Override
    public void onTouchEvent(TouchEvent evt) {
	//throw new UnsupportedOperationException("Not supported yet.");
    }
    
    BitmapText debugText1;
    BitmapText debugTextX, debugTextY, debugTextZ;
    
    public void debugText(){
	debugText1 = new BitmapText(guiFont, false);
	debugText1.setSize(20);      // font size
	debugText1.setColor(ColorRGBA.Red);                             // font color
	debugText1.setText("Debug!");             // the text
	debugText1.setLocalTranslation(settings.getWidth()-debugText1.getLineWidth(), debugText1.getLineHeight(), 0); // position
	
	debugTextX = new BitmapText(guiFont, false);
	debugTextX.setSize(20);      // font size
	debugTextX.setColor(ColorRGBA.Red);                             // font color
	debugTextX.setText("Debug X!");             // the text
	debugTextX.setLocalTranslation(settings.getWidth()-debugTextX.getLineWidth(), debugTextX.getLineHeight()*2, 0); // position
	
	debugTextY = new BitmapText(guiFont, false);
	debugTextY.setSize(20);      // font size
	debugTextY.setColor(ColorRGBA.Red);                             // font color
	debugTextY.setText("Debug Y!");             // the text
	debugTextY.setLocalTranslation(settings.getWidth()-debugTextY.getLineWidth(), debugTextY.getLineHeight()*3, 0); // position
	
	debugTextZ = new BitmapText(guiFont, false);
	debugTextZ.setSize(20);      // font size
	debugTextZ.setColor(ColorRGBA.Red);                             // font color
	debugTextZ.setText("Debug Z!");             // the text
	debugTextZ.setLocalTranslation(settings.getWidth()-debugTextZ.getLineWidth(), debugTextZ.getLineHeight()*4, 0); // position
	
	
	getGuiNode().attachChild(debugText1);
	getGuiNode().attachChild(debugTextX);
	getGuiNode().attachChild(debugTextY);
	getGuiNode().attachChild(debugTextZ);
    }
    
    
    void setDebugText1(String text){
	debugText1.setText(text);
    }
    
    void setDebugTextX(String text){
	debugTextX.setText("X " + text);
    }
    void setDebugTextY(String text){
	debugTextY.setText("Y " + text);
    }
    void setDebugTextZ(String text){
	debugTextZ.setText("Z " + text);
    }
    
    void resetNodeScales(){
//	for(int i=0;i<clickedGeos.size();i++){
//	}
//	if(clickedGeo != null){
//	    clickedGeo.setLocalScale(1);
//	}else{
//	}
//	if(clickedNode != null){
//	    for(int i=0;i<clickedNode.getSurroundingNodes().size();i++){
//		clickedNode.surroundingNodes.get(i).getBlock().setLocalScale(1);
//	    }
//	}
//	if(clickedNode != null){
//	    for(int i=0;i<clickedNode.getBuildingBlocks().size();i++){
//		clickedNode.getBuildingBlocks().get(i).getBlock().setLocalScale(1);
//	    }
//	}
	for(int i=0;i<renderableCubes.size();i++){
	    renderableCubes.get(i).setLocalScale(1);
	}
    }
    
    CityNode previousClickedNode = null;
    boolean highSurrs = false;
    
    void highlightSurroundingNodes(){
	if(clickedNode != null){
	    if(previousClickedNode != null){
		if(previousClickedNode != clickedNode){
		    resetNodeScales();
		}
	    }
	    previousClickedNode = clickedNode;
	}
	CollisionResults results = new CollisionResults();
		    
	mouseCoords = inputManager.getCursorPosition();
	mouseWorldCoords = cam.getWorldCoordinates(new Vector2f(mouseCoords.getX(), mouseCoords.getY()), 0f).clone();
	dir = cam.getWorldCoordinates(new Vector2f(mouseCoords.getX(), mouseCoords.getY()), 1f).subtractLocal(mouseWorldCoords).normalizeLocal();

	//for(int j=0;j<rootNode.getChildren().size();j++){
	Ray ray = new Ray(mouseWorldCoords, dir);
	// Collect intersections between ray and all nodes in results list.
	rootNode.collideWith(ray, results);
	// (Print the results so we see what is going on:)
	for (int i = 0; i < results.size(); i++) {
	    // (For each “hit”, we know distance, impact point, geometry.)
	    float dist = results.getCollision(i).getDistance();


	    if (results.size() > 0) {
		clickedGeo = results.getCollision(i).getGeometry();
		clickedGeos.add(clickedGeo);
	    }


	    Vector3f contactPoint = results.getCollision(i).getContactPoint();
	    String target = results.getCollision(i).getGeometry().getName();
	    //System.out.println("City.MouseMotion - Selection #" + i + ": " + target + " at " + contactPoint + ", " + dist + " WU away.");
	    //}
	}

	Geometry closest = null;
	if(results.size()>0){
	    clickedGeo = results.getClosestCollision().getGeometry();

	    //closest.getMaterial().setColor("Color", ColorRGBA.Pink);
	    for(int i=0;i<nodes.size();i++){
		//if(nodes.get(i).getPos().distance(clickedGeo.getWorldTranslation())==0){
		if(nodes.get(i).getBlock() != null){
		    if(nodes.get(i).getBlock().getWorldTranslation().getX()==clickedGeo.getWorldTranslation().getX()
			    && nodes.get(i).getBlock().getWorldTranslation().getY()==clickedGeo.getWorldTranslation().getY()
			    && nodes.get(i).getBlock().getWorldTranslation().getZ()==clickedGeo.getWorldTranslation().getZ()){
			clickedNode = nodes.get(i);
		    }
		}
	    }
	    //System.out.println("City.highlightSurroundingNodes - clickedNode type " + clickedNode.getType());
	    setDebugText1(String.valueOf(clickedNode.getType()));
	    //System.out.println("City.highlightSurroundingNodes - clickedNode's brethren # " + clickedNode.getBuildingBlocks().size());
	    if(clickedNode != null
		    || clickedNode.getSurroundingNodes() != null){
		for(int i=0;i<clickedNode.getSurroundingNodes().size();i++){
		    if(clickedNode.surroundingNodes.get(i) != null
			    || clickedNode.surroundingNodes.get(i).getBlock() != null 
			    || clickedNode != null){
			clickedNode.surroundingNodes.get(i).getBlock().setLocalScale(0.5f);
		    }
		    //renderableCubes.add(clickedNode.surroundingNodes.get(i).getBlock());
		}
	    }

	    clickedGeo.setLocalScale(0.5f);
	}
    }
    
    boolean highBros = false;
    void highlightBrethren(){
	if(clickedNode != null){
	    if(previousClickedNode != null){
		if(previousClickedNode != clickedNode){
		    resetNodeScales();
		}
	    }
	    previousClickedNode = clickedNode;
	}
	//		    mouseCoords = new Vector2f(evt.getX(), evt.getY());
//		    mouseWorldCoords = getCamera().getWorldCoordinates(mouseCoords, 0f).clone();
//		    dir = this.getCamera().getWorldCoordinates(new Vector2f(mouseCoords.x, mouseCoords.y), 1f).subtractLocal(mouseWorldCoords).normalizeLocal();
		    
		    CollisionResults results = new CollisionResults();
		    
		    mouseCoords = inputManager.getCursorPosition();
		    mouseWorldCoords = cam.getWorldCoordinates(new Vector2f(mouseCoords.getX(), mouseCoords.getY()), 0f).clone();
		    dir = cam.getWorldCoordinates(new Vector2f(mouseCoords.getX(), mouseCoords.getY()), 1f).subtractLocal(mouseWorldCoords).normalizeLocal();

		    //for(int j=0;j<rootNode.getChildren().size();j++){
		    Ray ray = new Ray(mouseWorldCoords, dir);
		    // Collect intersections between ray and all nodes in results list.
		    rootNode.collideWith(ray, results);
		    // (Print the results so we see what is going on:)
		    for (int i = 0; i < results.size(); i++) {
			// (For each “hit”, we know distance, impact point, geometry.)
			float dist = results.getCollision(i).getDistance();

			
			if (results.size() > 0) {
			    clickedGeo = results.getCollision(i).getGeometry();
			    clickedGeos.add(clickedGeo);
			    //Material pink = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
			    //pink.setColor("Color", ColorRGBA.Pink);
			    //clickedGeo.setMaterial(pink);
			    //clickedGeo.setLocalScale(2);
			    //System.out.println("City.onMouseButtonEvent - cube pos " + clickedGeo.getWorldTranslation());
			    //renderableCubes.clear();
			    //renderableCubes.add(clickedGeo);
			    //System.out.println("City.onMouseButtonEvent - scaled/colored");
			}
			
			
			Vector3f contactPoint = results.getCollision(i).getContactPoint();
			String target = results.getCollision(i).getGeometry().getName();
			//System.out.println("City.MouseMotion - Selection #" + i + ": " + target + " at " + contactPoint + ", " + dist + " WU away.");
			//}
		    }

		    Geometry closest = null;
		    if(results.size()>0){
			clickedGeo = results.getClosestCollision().getGeometry();
		    
			//closest.getMaterial().setColor("Color", ColorRGBA.Pink);
			for(int i=0;i<nodes.size();i++){
			    //if(nodes.get(i).getPos().distance(clickedGeo.getWorldTranslation())==0){
			    if(nodes.get(i).getBlock() != null){
				if(nodes.get(i).getBlock().getWorldTranslation().getX()==clickedGeo.getWorldTranslation().getX()
					&& nodes.get(i).getBlock().getWorldTranslation().getY()==clickedGeo.getWorldTranslation().getY()
					&& nodes.get(i).getBlock().getWorldTranslation().getZ()==clickedGeo.getWorldTranslation().getZ()){
				    clickedNode = nodes.get(i);
				}
			    }
			}
			//System.out.println("City.highlightBrethren - clickedNode type " + clickedNode.underNode.getType());
			//System.out.println("City.highlightBrethren - clickedNode type " + clickedNode.getType());
			//System.out.println("City.highlightBrethren - clickedNode pos " + clickedNode.getPos());
			setDebugText1(String.valueOf(clickedNode.getType()));
			//System.out.println("City.onMouseButtonEvent - clickedNode " + clickedNode.getBlock().getWorldTranslation());
			//System.out.println("City.onMouseButtonEvent - clickedNode's brethren # " + clickedNode.surroundingNodes.size());
			//System.out.println("City.highlightBrethren - clickedNode's brethren # " + clickedNode.getBuildingBlocks().size());
			//clickedNode.checkForBrethren();
			for(int i=0;i<clickedNode.getBuildingBlocks().size();i++){
			//for(int i=0;i<clickedNode.getSurroundingNodes().size();i++){
			    //System.out.println("City.onMouseButtonEvent - clickedNode's brethren " + clickedNode.surroundingNodes.get(i).getBlock());
			    //System.out.println("City.onMouseButtonEvent - clickedNode's brethren " + clickedNode.surroundingNodes.get(i).getBlock());
			    //System.out.println("City.onMouseButtonEvent - clickedNode's brethren " + clickedNode.surroundingNodes.get(i).getBlock().getWorldTranslation());
			    //System.out.println("City.onMouseButtonEvent - clickedNode's brethren type " + clickedNode.getBuildingBlocks().get(i).getType());
			    //clickedNode.surroundingNodes.get(i).getBlock().setLocalScale(0.5f);
			    clickedNode.getBuildingBlocks().get(i).getBlock().setLocalScale(0.5f);
			    //renderableCubes.add(clickedNode.surroundingNodes.get(i).getBlock());
			}
			//for(int i=0;i<clickedNode.getBuildingBlocks().size();i++){
			    //System.out.println("City.onMouseButtonEvent - clickedNode's brethren " + clickedNode.getBuildingBlocks().get(i).getBlock());
			    //clickedNode.getBuildingBlocks().get(i).getBlock().setLocalScale(0.5f);
			//}

			clickedGeo.setLocalScale(0.5f);
		    }

		    //System.out.println("City.onMouseButtonEvent - click RMB");
    }
}