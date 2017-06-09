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

    private float globalWeightBuilding = (1.0f / (0.5f + 0.4f + 0.3f + 0.2f + 0.1f)) * 0.5f;
    private float globalWeightProp = (1.0f / (0.5f + 0.4f + 0.3f + 0.2f + 0.1f)) * 0.4f;
    private float globalWeightRoad = (1.0f / (0.5f + 0.4f + 0.3f + 0.2f + 0.1f)) * 0.3f;
    private float globalWeightGrass = (1.0f / (0.5f + 0.4f + 0.3f + 0.2f + 0.1f)) * 0.2f;
    private float globalWeightAir = (1.0f / (0.5f + 0.4f + 0.3f + 0.2f + 0.1f)) * 0.1f;
    private float[] globals = {globalWeightBuilding, globalWeightProp, globalWeightRoad, globalWeightGrass, globalWeightAir};
    private int width, length, height;
    private ArrayList<CityNode> nodes = new ArrayList<CityNode>();
    private ArrayList<CityNode> renderableNodes = new ArrayList<CityNode>();
    private ArrayList<Geometry> nodeCubes = new ArrayList<Geometry>();
    private ArrayList<Geometry> renderableCubes = new ArrayList<Geometry>();
    private boolean closed = false;

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
	viewPort.setBackgroundColor(ColorRGBA.Gray);
	cam.setFrustumFar(100);
	executor.submit(doNodes);
    }

    public City() {
	CityNode node = new CityNode();
	node.setGlobals(globals);

        //**************************
        //**************************
	width = 10;
	length =100;
	height = 3;
        //**************************
        //**************************
    }
    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);

    //@Override
    public void destroy() {
	closed = true;
	executor.shutdownNow();
	super.destroy();
    }

    
    private boolean finished = false;
    
    @Override
    public void simpleUpdate(float tpf) {
	super.simpleUpdate(tpf);

	rootNode.detachAllChildren();
	for (int i = 0; i < renderableCubes.size()-1; i++) {
	    if(renderableNodes.get(i).getType() == CityNode.Type.AIR){
		renderableCubes.remove(i);
		renderableNodes.remove(i);
	    }else{
		    rootNode.attachChild(renderableCubes.get(i));
	    }
	}
	
	setDebugTextX(String.valueOf(x));
	setDebugTextY(String.valueOf(y));
	setDebugTextZ(String.valueOf(z));
	
	if(highBros){
	    highlightBrethren();
	}else if(highSurrs){
	    highlightSurroundingNodes();
	}else{
	    resetNodeScales();
	}
    }
    
    void reSkinNode(Geometry nodeCube){
	CityNode pickedNode = null;
	CityNode.Type type = CityNode.Type.AIR;
	
	for(int i=0;i<nodes.size();i++){
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
    
    private float x = 0, y = 0, z = 0;
    
    private Callable doNodes = new Callable() {
	@Override
	public String call() {
	    System.out.println("City.doNodes - making city");
	    finished = false;

	    
	    while (y < height && !closed) {
		while (x < width && !closed) {
		    while (z < length && !closed) {

			CityNode node = new CityNode(new Vector3f(x, y, z));

			if((x == 0 || x==width-1)
				||(z == 0 || z ==length-1)){
			    node.setCityEdge(true);
			}
			node.setSurroundingNodes(getSurroundingNodes(node));
			nodes.add(node);
			node.setBlock(makeCubeAt(node.getPos(), node.getType()));   
			if(node.getType() != node.getType().AIR){
			    renderableNodes.add(node);
			}
			nodes.trimToSize();
			renderableNodes.trimToSize();
			z++;
			//System.out.println("City.simpleUpdate - pos: " +x+" "+y+" "+z);
			//System.out.println("City.simpleUpdate - z tick");
		    }
		    x++;
		    z = 0;
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
		//try{
		    //Thread.sleep(1000);
		//}catch(Exception e){

		//}
	    }
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

	    if(enableBrethren){
		for(int i=0;i<nodes.size();i++){
		    if(!closed){
			nodes.get(i).checkForBrethren();
		    }
		}
	    }
	    
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
	
	
	return surNodes;
    }

    public void nodeLoop() {
	float x = 0, y = 0, z = 0;
	for (int a = 0; a < height; a++) {
	    for (int b = 0; b < width; b++) {
		for (int c = 0; c < length; c++) {
		    CityNode node = new CityNode(new Vector3f(x, y, z));
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

    private int cubeIndex = 0;
    
    public Geometry makeCubeAt(Vector3f pos, CityNode.Type type) {
	Box boxshape3 = new Box(new Vector3f(0,0,0), 0.5f, 0.5f, 0.5f);
	cubeIndex++;
	Geometry cube_translucent = new Geometry(type + " " + String.valueOf(cubeIndex), boxshape3);
	cube_translucent.move(pos);
	Material mat_tt = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
	mat_tt.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
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
    }

    
    private Geometry clickedGeo = null;
    private ArrayList<Geometry> clickedGeos = new ArrayList<Geometry>();
    private CityNode clickedNode = null;
    
    private Vector2f mouseCoords;
    private Vector3f mouseWorldCoords;
    private Vector3f dir;
    
    @Override
    public void onMouseButtonEvent(MouseButtonEvent evt) {
	//throw new UnsupportedOperationException("Not supported yet.");
	//System.out.println("City.onMouseButtonEvent - click " + evt.getButtonIndex());

	switch (evt.getButtonIndex()) {
	    case 0:
		break;
	    case 1:
		if (evt.isPressed()) {
		    highBros = true;
		}
		if(evt.isReleased())
		{
		    highBros = false;
		}
		break;
	    case 2:
		if (evt.isPressed()) {
		    highSurrs = true;

		}
		if(evt.isReleased())
		{
		    highSurrs = false;
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
			executor.submit(doNodes);
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

    private boolean enableBrethren = false;
    
    @Override
    public void onTouchEvent(TouchEvent evt) {
	//throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private BitmapText debugText1;
    private BitmapText debugTextX, debugTextY, debugTextZ;
    
    public void debugText(){
	debugText1 = new BitmapText(guiFont, false);
	debugText1.setSize(20);
	debugText1.setColor(ColorRGBA.Red);
	debugText1.setText("Debug!");
	debugText1.setLocalTranslation(settings.getWidth()-debugText1.getLineWidth(), debugText1.getLineHeight(), 0);
	
	debugTextX = new BitmapText(guiFont, false);
	debugTextX.setSize(20);
	debugTextX.setColor(ColorRGBA.Red);
	debugTextX.setText("Debug X!");
	debugTextX.setLocalTranslation(settings.getWidth()-debugTextX.getLineWidth(), debugTextX.getLineHeight()*2, 0);
	
	debugTextY = new BitmapText(guiFont, false);
	debugTextY.setSize(20);
	debugTextY.setColor(ColorRGBA.Red);
	debugTextY.setText("Debug Y!");
	debugTextY.setLocalTranslation(settings.getWidth()-debugTextY.getLineWidth(), debugTextY.getLineHeight()*3, 0);
	
	debugTextZ = new BitmapText(guiFont, false);
	debugTextZ.setSize(20);
	debugTextZ.setColor(ColorRGBA.Red);
	debugTextZ.setText("Debug Z!");
	debugTextZ.setLocalTranslation(settings.getWidth()-debugTextZ.getLineWidth(), debugTextZ.getLineHeight()*4, 0);
	
	
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
	for(int i=0;i<renderableCubes.size();i++){
	    renderableCubes.get(i).setLocalScale(1);
	}
    }
    
    private CityNode previousClickedNode = null;
    private boolean highSurrs = false;
    
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

	Ray ray = new Ray(mouseWorldCoords, dir);
	rootNode.collideWith(ray, results);
	for (int i = 0; i < results.size(); i++) {
	    float dist = results.getCollision(i).getDistance();


	    if (results.size() > 0) {
		clickedGeo = results.getCollision(i).getGeometry();
		clickedGeos.add(clickedGeo);
	    }


	    Vector3f contactPoint = results.getCollision(i).getContactPoint();
	    String target = results.getCollision(i).getGeometry().getName();
	    //System.out.println("City.MouseMotion - Selection #" + i + ": " + target + " at " + contactPoint + ", " + dist + " WU away.");
	}

	Geometry closest = null;
	if(results.size()>0){
	    clickedGeo = results.getClosestCollision().getGeometry();

	    for(int i=0;i<nodes.size();i++){
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
		    if(clickedNode.getSurroundingNodes().get(i) != null
			    || clickedNode.getSurroundingNodes().get(i).getBlock() != null 
			    || clickedNode != null){
			clickedNode.getSurroundingNodes().get(i).getBlock().setLocalScale(0.5f);
		    }
		}
	    }

	    clickedGeo.setLocalScale(0.5f);
	}
    }
    
    private boolean highBros = false;
    void highlightBrethren(){
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

		    Ray ray = new Ray(mouseWorldCoords, dir);
		    rootNode.collideWith(ray, results);
		    for (int i = 0; i < results.size(); i++) {
			float dist = results.getCollision(i).getDistance();

			
			if (results.size() > 0) {
			    clickedGeo = results.getCollision(i).getGeometry();
			    clickedGeos.add(clickedGeo);
			    //System.out.println("City.onMouseButtonEvent - cube pos " + clickedGeo.getWorldTranslation());
			}
			
			
			Vector3f contactPoint = results.getCollision(i).getContactPoint();
			String target = results.getCollision(i).getGeometry().getName();
			//System.out.println("City.MouseMotion - Selection #" + i + ": " + target + " at " + contactPoint + ", " + dist + " WU away.");
		    }

		    Geometry closest = null;
		    if(results.size()>0){
			clickedGeo = results.getClosestCollision().getGeometry();
		    
			for(int i=0;i<nodes.size();i++){
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