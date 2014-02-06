package Main;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
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
import com.jme3.input.controls.ActionListener;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * test
 * @author normenhansen
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
        Main app = new Main();
	AppSettings settings = new AppSettings(true);
	app.setSettings(settings);
	app.setShowSettings(false);
	app.setDisplayFps(true);
	app.setDisplayStatView(true);
	app.settings.setTitle("FUSCHIA");
	app.settings.setWidth(800);
	app.settings.setHeight(600);
        app.start();
    }

   //private static final Logger logger = Logger.getLogger(Main.class.getName());
    
//    public Main(int cSize, int rWidth, int mSep, int mHeight){
//	simpleInitApp(cSize, rWidth, mSep, mHeight);
//    }
    
    DirectionalLight sun = new DirectionalLight();
    DirectionalLight sun2 = new DirectionalLight();
    
    Material mat, mat1;
    Input input;
    Player player;
    
    @Override
    public void simpleInitApp() {
	//logger.setLevel(Level.OFF);
	//this.
	input = new Input(this);
	input.initKeys();
	java.util.logging.Logger.getLogger("").setLevel(Level.SEVERE);
	
//	TestCityGenGUI gui = new TestCityGenGUI(this);
//	gui.setVisible(true);
	
	flyCam.setMoveSpeed(200);
	
	cam.setFrustumNear(1f);
	cam.setFrustumFar(5000f);
	
//	DirectionalLight sun = new DirectionalLight();
	sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
	sun.setColor(ColorRGBA.White);
	rootNode.addLight(sun); 
//	DirectionalLight sun2 = new DirectionalLight();
	sun2.setDirection((new Vector3f(-0.5f, 0.5f, -0.5f)).normalizeLocal());
	sun2.setColor(ColorRGBA.White);
	rootNode.addLight(sun2);
	AmbientLight ambLight = new AmbientLight();
	//sun2.setDirection((new Vector3f(-0.5f, 0.5f, -0.5f)).normalizeLocal());
	ambLight.setColor(ColorRGBA.White);
	rootNode.addLight(ambLight); 
	rootNode.setShadowMode(RenderQueue.ShadowMode.Cast);
//	DirectionalLight sun3 = new DirectionalLight();
//	sun3.setDirection((new Vector3f(0.5f, 0.5f, 0.5f)).normalizeLocal());
//	sun3.setColor(ColorRGBA.White);
//	rootNode.addLight(sun3); 
	
	mat = new Material( assetManager, "Common/MatDefs/Light/Lighting.j3md");

	mat.getAdditionalRenderState().setWireframe(false);
	mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Back);	
	
	mat1 = new Material( assetManager, "Common/MatDefs/Light/Lighting.j3md");
	mat1.setColor("Diffuse", ColorRGBA.Blue);
	
	mat1.getAdditionalRenderState().setWireframe(false);
	mat1.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Back);
	
	//generateCity(1);
	
	addCube();
	
	player = new Player();
	//player.loadModel();
	
	
    }
    
    public AnimChannel channel;
    public AnimChannel getChannel(){
	return channel;
    }
    public AnimControl control;
    public AnimControl getControl(){
	return control;
    }
    Node playerNode;
    
    public void loadPlayerModel(){
	
	/** Load a model. Uses model and texture from jme3-test-data library! */ 
        playerNode = (Node) assetManager.loadModel("Models/Placeholder/Placeholder.j3o");
        Material mat_default = new Material( assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        playerNode.setMaterial(mat_default);
        rootNode.attachChild(playerNode);
	
	for(int i=0;i<playerNode.getChildren().size();i++){
	    //System.out.println(player.getControl(i));
	}
	
	Node n1 = (Node) playerNode.getChild("Armature");
	Node n2 = (Node) playerNode.getChild("Cube");

	//control = new AnimControl();
	control = n2.getControl(AnimControl.class);
	control.addListener(this);
	channel = control.createChannel();
	channel.setAnim("Still");
	
    }
    
    public void addCube(){
	/** Translucent/transparent cube. Uses Texture from jme3-test-data library! */
	Box boxshape3 = new Box(Vector3f.ZERO, 1f,1f,1f);
	Geometry cube_translucent = new Geometry("translucent cube", boxshape3);
	Material mat_tt = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
	//mat_tt.setTexture("ColorMap", assetManager.loadTexture("Textures/ColoredTex/Monkey.png"));
	mat_tt.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
	cube_translucent.setMaterial(mat_tt); 
	rootNode.attachChild(cube_translucent); 
    }
    
    int citySize;
    
    public void generateNew(int size, float width, int seperation, int maxHeight){
	//rootNode.detachAllChildren();
	
	//rootNode.addLight(sun); 
	//rootNode.addLight(sun2); 
	
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
	
	//roadsX = new ArrayList<Geometry>();
	//roadsZ = new ArrayList<Geometry>();
	//buildings = new ArrayList<Geometry>();
	
	roadWidth = width;
	maxRoadSeperation = seperation;
	//maxRandHeight = maxHeight;
	citySize = size;
	
	
	generateCity(citySize);
    }
    
    public void generateCity(int citySize){
	//this.citySize = citySize;
//	Box roadPlane = new Box(new Vector3f(0, 0, 0), citySize*125f, 0, roadWidth);
//	Geometry roadGeom = new Geometry("Plane", roadPlane);
//	roadGeom.setLocalTranslation(0, 0, 0);		
//
//	roadGeom.setMaterial(mat1);
//
//	rootNode.attachChild(roadGeom);
	genRoads(citySize+1);
	genBuildings(citySize+1);
    }
    
    ArrayList<Geometry> roadsX = new ArrayList<Geometry>();
    ArrayList<Geometry> roadsZ = new ArrayList<Geometry>();
    ArrayList<Geometry> buildings = new ArrayList<Geometry>();;
    
    //----------------------------------------
    //The 3 most important things to play with
    //----------------------------------------
    float roadWidth = 10f;
    int maxRoadSeperation = 100;
    int maxRandHeight = 30;
    
    Geometry roadGeom;
    Box roadPlane;
    
    public void genRoads(int citySize){
		
	int roadXPos = 0;	
	int roadYPos = 0;
	int roadZPos = 0;
	float roadSeperation;
	
	boolean secondPass = false;
	
//	roadsX.clear();
//	roadsZ.clear();
	
	
	for(int j=0;j<2;j++){
	    
	    for(int i=0;i<citySize;i++){
		//if(roadGeom != null){
		//rootNode.detachChild(roadGeom);}
		//roadGeom = null;
		roadSeperation = (float) new Random().nextInt(maxRoadSeperation);
		while(roadSeperation<=roadWidth*2){
		    roadSeperation = (float) new Random().nextInt(maxRoadSeperation);
		}
		//roadPlane = null;
		//roadGeom = null;
		roadPlane = new Box(new Vector3f(0, 0, 0), citySize*125f, 0, roadWidth);
		roadGeom = new Geometry("Plane", roadPlane);
		roadGeom.setLocalTranslation(roadXPos, 0, roadZPos);		

		roadGeom.setMaterial(mat1);

		rootNode.attachChild(roadGeom);



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
    
    Geometry buildingGeom;
    Box buildingBox;
    
    public void genBuildings(int citySize){
	int xPos = 0;	
	int yPos = 0;
	int zPos = 0;
	float ranHeight;
	
	int roadXTick = -1;
	int roadZTick = 0;
//	buildings.clear();
    
	
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
		//Box b = new Box(new Vector3f(0, 0, 0), .5f, ranHeight, .5f);
		//sets box to scale to fit gaps in roads
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

//	    Material mat = new Material( assetManager, "Common/MatDefs/Light/Lighting.j3md");
//
//	    mat.getAdditionalRenderState().setWireframe(false);
//	    mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Back);	    
	    
	    if(buildingGeom!=null){
		buildingGeom.setMaterial(mat);
		rootNode.attachChild(buildingGeom);
		buildings.add(buildingGeom);
		//System.out.println(buildings.size());
	    }

	    
	    if(xPos>=8){
		xPos=0;
		zPos+=2; 
	    }else{
		xPos+=2;
	    }
	    
	}
    }
    
    int testSize;
    int testMaxH;
    float testRoadWidth;
    int testRoadSep;
    public void genNewRandomCity(){
	newTick = 0;
	testSize = new Random().nextInt(20)+1;
	testMaxH = new Random().nextInt(50)+30;
	testRoadWidth = new Random().nextInt(20)+10;
	testRoadSep = (int)((new Random().nextInt(20)) + testRoadWidth*2 + 100);
	//rootNode.detachAllChildren();
	generateNew(testSize,testRoadWidth,testRoadSep,testMaxH);
	//generateNew(1,testRoadWidth,testRoadSep,testMaxH);
	//System.out.println("New tick");
    }

    int newTick = 0;
    
    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
