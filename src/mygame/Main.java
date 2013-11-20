package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
	AppSettings settings = new AppSettings(true);
	app.setSettings(settings);
	app.setShowSettings(false);
	app.setDisplayFps(true);
	app.setDisplayStatView(true);
	app.settings.setTitle("FUSCHIA");
	app.settings.setWidth(1280);
	app.settings.setHeight(720);
        app.start();
    }

   //private static final Logger logger = Logger.getLogger(Main.class.getName());
    
    @Override
    public void simpleInitApp() {
	//logger.setLevel(Level.OFF);
	//this.
	java.util.logging.Logger.getLogger("").setLevel(Level.SEVERE);
	
	flyCam.setMoveSpeed(50);
	
	cam.setFrustumNear(1f);
	cam.setFrustumFar(5000f);
	
	DirectionalLight sun = new DirectionalLight();
	sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
	sun.setColor(ColorRGBA.White);
	rootNode.addLight(sun); 
	DirectionalLight sun2 = new DirectionalLight();
	sun2.setDirection((new Vector3f(-0.5f, 0.5f, -0.5f)).normalizeLocal());
	sun2.setColor(ColorRGBA.White);
	rootNode.addLight(sun2); 
	
	generateCity(25);
    }
    
    public void generateCity(int citySize){
	genRoads(citySize);
	genBuildings(citySize);
    }
    
    ArrayList<Geometry> roadsX = new ArrayList<Geometry>();
    ArrayList<Geometry> roadsZ = new ArrayList<Geometry>();
    ArrayList<Geometry> buildings = new ArrayList<Geometry>();
    
    float roadWidth = .5f;
    
    public void genRoads(int citySize){
	
	int maxRand = 10;
	
	int roadXPos = 0;	
	int roadYPos = 0;
	int roadZPos = 0;
	float roadSeperation;
	
	boolean secondPass = false;
	
	for(int j=0;j<2;j++){
	    Geometry geom = null;
	    for(int i=0;i<citySize;i++){
		
		roadSeperation = (float) new Random().nextInt(maxRand);
		while(roadSeperation<=roadWidth*2){
		    roadSeperation = (float) new Random().nextInt(maxRand);
		}

		Box plane = new Box(new Vector3f(0, 0, 0), 125f, 0, roadWidth);
		geom = new Geometry("Plane", plane);
		geom.setLocalTranslation(roadXPos, 0, roadZPos);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md"); 
		mat.setColor("Color", ColorRGBA.Blue);

		geom.setMaterial(mat);

		rootNode.attachChild(geom);

		if(secondPass){
		    geom.setLocalRotation(new Quaternion(0,1,0,1));
		    roadXPos+=roadSeperation;
		    roadsX.add(geom);
		    System.out.println("RoadsX size: " + roadsX.size());
		}else{
		    roadZPos+=roadSeperation;
		    roadsZ.add(geom);
		    System.out.println("RoadsZ size: " + roadsZ.size());
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
    
    public void genBuildings(int citySize){
	int xPos = 0;	
	int yPos = 0;
	int zPos = 0;
	float ranHeight;
	
	int maxRand = 10;
	int roadXTick = -1;
	int roadZTick = 0;
	
	for(int i=0;i<Math.pow(citySize, 2);i++)
	{
	    ranHeight = (float) new Random().nextInt(maxRand);
	    while(ranHeight<=1){
		ranHeight = (float) new Random().nextInt(maxRand);
	    }
	    System.out.println("Road TickX: " + roadXTick++);
	    System.out.println("Road TickZ: " + roadZTick);
	    
	    Geometry geom = null;
	    
	    if(roadXTick >= citySize){
		roadZTick++;
		roadXTick=0;
		//Box b = new Box(new Vector3f(0, 0, 0), .5f, ranHeight, .5f);
		//sets box to scale to fit gaps in roads
		Box b = null;
		if(roadXTick < roadsX.size()-1 && roadZTick < roadsZ.size()-1){
		b = new Box(new Vector3f(0, 0, 0), (calcScale(roadsX.get(roadXTick).getWorldTranslation().getX(), roadsX.get(roadXTick+1).getWorldTranslation().getX())/2)-roadWidth,
			ranHeight,
			(calcScale(roadsZ.get(roadZTick).getWorldTranslation().getZ(), roadsZ.get(roadZTick+1).getWorldTranslation().getZ())/2)-roadWidth);
		b.updateGeometry(
			new Vector3f(
			    b.getCenter().getX()+(b.getXExtent())-roadWidth,
			    b.getCenter().getY()+(b.getYExtent()),
			    b.getCenter().getZ()+(b.getZExtent())-roadWidth),
			b.getXExtent(),
			b.getYExtent(),
			b.getZExtent());
		geom = new Geometry("Box", b);
		geom.setLocalTranslation(roadsX.get(roadXTick).getWorldTranslation().getX()+roadWidth*2, 0, roadsZ.get(roadZTick).getWorldTranslation().getZ()+roadWidth*2);
		}
	    }else{
		Box b = null;
		if(roadXTick < roadsX.size()-1 && roadZTick < roadsZ.size()-1){
		b = new Box(new Vector3f(0, 0, 0), (calcScale(roadsX.get(roadXTick).getWorldTranslation().getX(), roadsX.get(roadXTick+1).getWorldTranslation().getX())/2)-roadWidth,
			ranHeight,
			(calcScale(roadsZ.get(roadZTick).getWorldTranslation().getZ(), roadsZ.get(roadZTick+1).getWorldTranslation().getZ())/2)-roadWidth);
		b.updateGeometry(
			new Vector3f(
			    b.getCenter().getX()+(b.getXExtent())-roadWidth,
			    b.getCenter().getY()+(b.getYExtent()),
			    b.getCenter().getZ()+(b.getZExtent())-roadWidth),
			b.getXExtent(),
			b.getYExtent(),
			b.getZExtent());
		geom = new Geometry("Box", b);
		geom.setLocalTranslation(roadsX.get(roadXTick).getWorldTranslation().getX()+roadWidth*2, 0, roadsZ.get(roadZTick).getWorldTranslation().getZ()+roadWidth*2);
		}
	    }

	    Material mat = new Material( assetManager, "Common/MatDefs/Light/Lighting.j3md");
	    //mat.setColor("Color", ColorRGBA.White);
	    this.renderer.setBackgroundColor(ColorRGBA.Blue);

	    mat.getAdditionalRenderState().setWireframe(false);
	    mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Back);	    
	    
	    if(geom!=null){
		geom.setMaterial(mat);
		rootNode.attachChild(geom);
		buildings.add(geom);
	    }

	    
	    if(xPos>=8){
		xPos=0;
		zPos+=2; 
	    }else{
		xPos+=2;
	    }
	    
	}
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
