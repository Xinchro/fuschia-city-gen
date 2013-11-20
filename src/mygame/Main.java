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
import java.util.Random;

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
        app.start();
    }

   
    @Override
    public void simpleInitApp() {
	flyCam.setMoveSpeed(50);
	
	cam.setFrustumNear(1f);
	cam.setFrustumFar(5000f);
	
	DirectionalLight sun = new DirectionalLight();
	sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
	sun.setColor(ColorRGBA.White);
	rootNode.addLight(sun); 
	
	generateCity(25);
    }
    
    public void generateCity(int citySize){
	genRoads(citySize);
	genBuildings(citySize);
    }
    
    public void genBuildings(int citySize){
	int xPos = 0;	
	int yPos = 0;
	int zPos = 0;
	float ranHeight;
	
	int maxRand = 10;
		
	for(int i=0;i<citySize;i++)
	{
	    ranHeight = (float) new Random().nextInt(maxRand);
	    while(ranHeight<=1){
		ranHeight = (float) new Random().nextInt(maxRand);
	    }
	    
	    Box b = new Box(new Vector3f(xPos, 0, zPos), .5f, ranHeight, .5f);
	    //move box to same "ground" level
	    b.updateGeometry(new Vector3f(b.getCenter().getX(), b.getCenter().getY()+(b.getYExtent()), b.getCenter().getZ()), b.getXExtent(), b.getYExtent(), b.getZExtent());
	    Geometry geom = new Geometry("Box", b);

	    Material mat = new Material( assetManager, "Common/MatDefs/Light/Lighting.j3md");
	    //mat.setColor("Color", ColorRGBA.White);
	    this.renderer.setBackgroundColor(ColorRGBA.Blue);

	    mat.getAdditionalRenderState().setWireframe(false);
	    mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Back);	    
	    
	    geom.setMaterial(mat);

	    rootNode.attachChild(geom);
	    if(xPos>=8){
		xPos=0;
		zPos+=2; 
	    }else{
		xPos+=2;
	    }
	}
    }
    
    public void genRoads(int citySize){
	
	int maxRand = 10;
	
	int roadXPos = 0;	
	int roadYPos = 0;
	int roadZPos = 0;
	float roadWidth = .5f;
	float roadSeperation;
	
	boolean secondPass = false;
	
	for(int j=0;j<2;j++){
	    for(int i=0;i<citySize;i++){
		
		roadSeperation = (float) new Random().nextInt(maxRand);
		while(roadSeperation<=roadWidth*2){
		    roadSeperation = (float) new Random().nextInt(maxRand);
		}

		Box plane = new Box(new Vector3f(roadXPos, 0, roadZPos), 125f, 0, roadWidth);
		Geometry geom = new Geometry("Plane", plane);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md"); 
		mat.setColor("Color", ColorRGBA.Blue);

		geom.setMaterial(mat);

		rootNode.attachChild(geom);

		if(secondPass){
		    geom.setLocalRotation(new Quaternion(0,1,0,1));
		    roadZPos+=roadSeperation;
		}else{
		    roadZPos+=roadSeperation;
		}
	    }
	    roadZPos = 0;
	    secondPass = true;
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
