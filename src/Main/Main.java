package Main;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.collision.CollisionResults;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;

/**
 *
 * @author Xinchro
 */
public class Main extends SimpleApplication implements AnimEventListener{

    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
	//throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
    	//throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public static void main(String[] args) {
	//setup basic game settings
        Main app = new Main();
	AppSettings settings = new AppSettings(true);
	app.setPauseOnLostFocus(false);
	app.setSettings(settings);
	app.settings.setUseJoysticks(true);
	app.setShowSettings(false);
	app.setDisplayFps(true);
	app.setDisplayStatView(true);
	app.settings.setTitle("FUSCHIA - Full Ultra Speed Combat Harmony In Air");
	app.settings.setWidth(800);
	app.settings.setHeight(600);
	app.settings.setFrameRate(60);
        app.start();
    }

    private DirectionalLight sun = new DirectionalLight();
    private DirectionalLight sun2 = new DirectionalLight();
    
    private Material mat, mat1;
    private Input input;
    private Player player;
    private CameraNode camNode;
    
    public CameraNode camNode(){
	return camNode;
    }
    
    private BulletAppState bulletAppState;

    
    @Override
    public void simpleInitApp() {
	//logger.setLevel(Level.OFF);
	
	input = new Input(this);
	input.initKeys();
	java.util.logging.Logger.getLogger("").setLevel(Level.SEVERE);
	
	flyCam.setMoveSpeed(200);
	
	cam.setFrustumNear(1f);
	cam.setFrustumFar(5000f);
	
	//add some lights for the shadows
	sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
	sun.setColor(ColorRGBA.White);
	rootNode.addLight(sun); 
	sun2.setDirection((new Vector3f(-0.5f, 0.5f, -0.5f)).normalizeLocal());
	sun2.setColor(ColorRGBA.White);
	rootNode.addLight(sun2);
	AmbientLight ambLight = new AmbientLight();
	ambLight.setColor(ColorRGBA.White);
	rootNode.addLight(ambLight); 
	rootNode.setShadowMode(RenderQueue.ShadowMode.Cast);
	
	//instanciate the materials
	mat = new Material( assetManager, "Common/MatDefs/Light/Lighting.j3md");

	mat.getAdditionalRenderState().setWireframe(false);
	mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Back);	
	
	mat1 = new Material( assetManager, "Common/MatDefs/Light/Lighting.j3md");
	mat1.setColor("Diffuse", ColorRGBA.Blue);
	
	mat1.getAdditionalRenderState().setWireframe(false);
	mat1.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Back);
	
	//reference cube
	addCube();
	
	player = new Player();
	loadPlayerModel();
	camNode = new CameraNode("Camera Node", this.getCamera());
	camNode.setLocalTranslation(0.0f, 20.0f, -50.0f);
	//make the camera look at the player
	camNode.lookAt(playerNode.getWorldTranslation(), Vector3f.UNIT_Y);
	playerNode.attachChild(camNode);
	
	genNewRandomCity();
	
    }
    
    private CollisionResults results;
    
    public AnimChannel channel;
    public AnimChannel getChannel(){
	return channel;
    }
    public AnimControl control;
    public AnimControl getControl(){
	return control;
    }
    private Node playerNode;
    private CharacterControl character;
    
    public Node playerNode(){
	return playerNode;
    }

    /**
     * Loads an arbitrary model to the player node
     */
    public void loadPlayerModel(){
	
        playerNode = (Node) assetManager.loadModel("Models/Placeholder/Placeholder.j3o");
        Material mat_default = new Material( assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        playerNode.setMaterial(mat_default);
	playerNode.move(0, 10, 0);
        rootNode.attachChild(playerNode);
	
	//for(int i=0;i<playerNode.getChildren().size();i++){
	    //System.out.println(player.getControl(i));
	//}
	
	Node n1 = (Node) playerNode.getChild("Armature");
	Node n2 = (Node) playerNode.getChild("Cube");

	control = n2.getControl(AnimControl.class);
	control.addListener(this);
	channel = control.createChannel();
	channel.setAnim("Still");
	
    }
    
    /**
     * Cube added to have a reference for when flying around
     */
    public void addCube(){
	Box boxshape3 = new Box(Vector3f.ZERO, 1f,1f,1f);
	Geometry cube_translucent = new Geometry("translucent cube", boxshape3);
	Material mat_tt = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
	mat_tt.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
	cube_translucent.setMaterial(mat_tt); 
	rootNode.attachChild(cube_translucent); 
    }
    
    private int citySize;
    
    /**
     * Clears everything and generates a new city
     * 
     * @param size
     * @param width
     * @param seperation
     * @param maxHeight 
     */
    public void generateNew(int size, float width, int seperation, int maxHeight){
	
//	System.out.println("roadsX size: " + roadsX.size());
	for(int i=0;i<roadsX.size();i++){
	    rootNode.detachChild(roadsX.get(i));
//	    System.out.println("roadsX " + (i+1) + "/" + roadsX.size());
	}
//	System.out.println("roadsZ size: " + roadsZ.size());
	for(int i=0;i<roadsZ.size();i++){
	    rootNode.detachChild(roadsZ.get(i));
//	    System.out.println("roadsZ " + (i+1) + "/" + roadsZ.size());
	}
//	System.out.println("buildings size: " + buildings.size());
	for(int i=0;i<buildings.size();i++){
	    rootNode.detachChild(buildings.get(i));
//	    System.out.println("buildings " + (i+1) + "/" + buildings.size());
	}
	
	roadsX.clear();
	//System.out.println("roadsX clear " + roadsX.size());
	roadsZ.clear();
	//System.out.println("roadsZ clear " + roadsZ.size());
	buildings.clear();
	//System.out.println("buildings clear " + buildings.size());
	
	//System.out.println("rootNode clear " + rootNode.getQuantity());
	
	roadWidth = width;
	maxRoadSeperation = seperation;
	citySize = size;
	
	
	generateCity(citySize);
    }
    
    /**
     * Generates a city of the given size.
     * 
     * @param citySize 
     */
    public void generateCity(int citySize){
	genRoads(citySize+1);
	genBuildings(citySize+1);
    }
    
    private ArrayList<Geometry> roadsX = new ArrayList<Geometry>();
    private ArrayList<Geometry> roadsZ = new ArrayList<Geometry>();
    private ArrayList<Geometry> buildings = new ArrayList<Geometry>();
    
    public ArrayList<Geometry> buildings(){
	return buildings;
    }
    
    //----------------------------------------
    //The 3 most important things to play with
    //----------------------------------------
    private float roadWidth = 100f;
    private int maxRoadSeperation = 1000;
    private int maxRandHeight = 300;
    
    private Geometry roadGeom;
    private Box roadPlane;
    
    /**
     * Generates the number of roads in a square grid.
     * 
     * @param citySize 
     */
    public void genRoads(int citySize){
		
	int roadXPos = 0;	
	int roadYPos = 0;
	int roadZPos = 0;
	float roadSeperation;
	
	boolean secondPass = false;
	
	
	
	for(int j=0;j<2;j++){
	    
	    for(int i=0;i<citySize;i++){
		roadSeperation = (float) new Random().nextInt(maxRoadSeperation);
		while(roadSeperation<=roadWidth*2){
		    roadSeperation = (float) new Random().nextInt(maxRoadSeperation);
		}
		roadPlane = new Box(new Vector3f(0, 0, 0), citySize*125f, 0, roadWidth);
		roadGeom = new Geometry("Plane", roadPlane);
		roadGeom.setLocalTranslation(roadXPos, 0, roadZPos);		

		roadGeom.setMaterial(mat1);

		rootNode.attachChild(roadGeom);



		//vertical vs horizontal
		if(secondPass){
		    roadGeom.setLocalRotation(new Quaternion(0,1,0,1));
		    roadXPos+=roadSeperation;
		    roadsX.add(roadGeom);
		    //System.out.println("RoadsX size: " + roadsX.size());
		}else{
		    roadZPos+=roadSeperation;
		    roadsZ.add(roadGeom);
		    //System.out.println("RoadsZ size: " + roadsZ.size());
		}
	    }
	    roadZPos = 0;
	    secondPass = true;
	}
	
    }
    
    public float calcScale(float a1, float a2){
	float scale = 0.0f;
	
	scale = a2 - a1;
	
	return scale;
    }
    
    private Geometry buildingGeom;
    private Box buildingBox;
    
    /**
     * Generates the buildings inbetween the roads.
     * 
     * @param citySize 
     */
    public void genBuildings(int citySize){
	int xPos = 0;	
	int yPos = 0;
	int zPos = 0;
	float ranHeight;
	
	int roadXTick = -1;
	int roadZTick = 0;
    
	
	for(int i=0;i<Math.pow(citySize, 2);i++)
	{
	    buildingGeom = null;
	    ranHeight = (float) new Random().nextInt(maxRandHeight);
	    while(ranHeight<=1){
		ranHeight = (float) new Random().nextInt(maxRandHeight);
	    }
	    roadXTick++;
	    roadZTick = roadZTick;
//	    System.out.println("Road TickX: " + roadXTick);
//	    System.out.println("Road TickZ: " + roadZTick);
	    
	  
	    
	    if(roadXTick >= citySize){
		roadZTick++;
		roadXTick=0;
		buildingBox = null;
		if(roadXTick < roadsX.size()-1 && roadZTick < roadsZ.size()-1){
		buildingBox = new Box(new Vector3f(0, 0, 0), (calcScale(roadsX.get(roadXTick).getWorldTranslation().getX(), roadsX.get(roadXTick+1).getWorldTranslation().getX())/2)-roadWidth,
			ranHeight,
			(calcScale(roadsZ.get(roadZTick).getWorldTranslation().getZ(), roadsZ.get(roadZTick+1).getWorldTranslation().getZ())/2)-roadWidth);
		buildingBox.updateGeometry(
			new Vector3f(
			    buildingBox.getCenter().getX()+(buildingBox.getXExtent())-roadWidth,
			    buildingBox.getCenter().getY()+(buildingBox.getYExtent()),
			    buildingBox.getCenter().getZ()+(buildingBox.getZExtent())-roadWidth),
			buildingBox.getXExtent(),
			buildingBox.getYExtent(),
			buildingBox.getZExtent());
		buildingGeom = new Geometry("Box", buildingBox);
		buildingBox = null;
		
		buildingGeom.setLocalTranslation(roadsX.get(roadXTick).getWorldTranslation().getX()+roadWidth*2, 0, roadsZ.get(roadZTick).getWorldTranslation().getZ()+roadWidth*2);
		}
	    }else{
		buildingBox = null;
		if(roadXTick < roadsX.size()-1 && roadZTick < roadsZ.size()-1){
		buildingBox = new Box(new Vector3f(0, 0, 0), (calcScale(roadsX.get(roadXTick).getWorldTranslation().getX(), roadsX.get(roadXTick+1).getWorldTranslation().getX())/2)-roadWidth,
			ranHeight,
			(calcScale(roadsZ.get(roadZTick).getWorldTranslation().getZ(), roadsZ.get(roadZTick+1).getWorldTranslation().getZ())/2)-roadWidth);
		buildingBox.updateGeometry(
			new Vector3f(
			    buildingBox.getCenter().getX()+(buildingBox.getXExtent())-roadWidth,
			    buildingBox.getCenter().getY()+(buildingBox.getYExtent()),
			    buildingBox.getCenter().getZ()+(buildingBox.getZExtent())-roadWidth),
			buildingBox.getXExtent(),
			buildingBox.getYExtent(),
			buildingBox.getZExtent());
		buildingGeom = new Geometry("Box", buildingBox);
		buildingGeom.setLocalTranslation(roadsX.get(roadXTick).getWorldTranslation().getX()+roadWidth*2, 0, roadsZ.get(roadZTick).getWorldTranslation().getZ()+roadWidth*2);
		}
	    }

	    
	    if(buildingGeom!=null){
		buildingGeom.setMaterial(mat);
		
		rootNode.attachChild(buildingGeom);
		buildings.add(buildingGeom);
	    }

	    
	    if(xPos>=8){
		xPos=0;
		zPos+=2; 
	    }else{
		xPos+=2;
	    }
	    
	}
    }
    
    private int testSize;
    private int testMaxH;
    private float testRoadWidth;
    private int testRoadSep;
    /**
     * Generate a new vity with random parameters
     */
    public void genNewRandomCity(){
	newTick = 0;
	testSize = new Random().nextInt(20)+1;
	testMaxH = new Random().nextInt(50)+30;
	testRoadWidth = new Random().nextInt(20)+10;
	testRoadSep = (int)((new Random().nextInt(20)) + testRoadWidth*2 + 100);
	generateNew(testSize,testRoadWidth,testRoadSep,testMaxH);
	//System.out.println("New tick");
    }

    private int newTick = 0;
    
    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
	input.moveCharacter();
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
