package CityGen;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Level;

/**
 *
 * @author Xinchro
 */
public class City extends SimpleApplication{

    float globalWeightRoad = 0.0f;
    float globalWeightBuilding = 0.0f;
    float globalWeightProp = 0.0f;
    float globalWeightGrass = 0.0f;
    float globalWeightAir = 0.0f; 
    //float globals[] = new float[5];
    float[] globals = {globalWeightRoad, globalWeightBuilding, globalWeightProp, globalWeightGrass, globalWeightAir};
    
    int width, length, height;
    
    ArrayList<Node> nodes = new ArrayList<Node>();
    ArrayList<Geometry> nodeCubes = new ArrayList<Geometry>();
    

    public static void main(String[] args) {
        City app = new City();
	AppSettings settings = new AppSettings(true);
	app.setPauseOnLostFocus(false);
	app.setSettings(settings);
	app.settings.setUseJoysticks(true);
	
	app.setShowSettings(false);
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
	java.util.logging.Logger.getLogger("").setLevel(Level.SEVERE);
	flyCam.setMoveSpeed(100);
	cam.setLocation(new Vector3f(width+20.0f, height+10.0f, length+20.0f));
	cam.lookAt(new Vector3f(width/2, 0, length/2)
		, Vector3f.UNIT_Y);
	//throw new UnsupportedOperationException("Not supported yet.");
	/** Translucent/transparent cube. Uses Texture from jme3-test-data library! */
//	Box boxshape3 = new Box(Vector3f.ZERO, 1f,1f,1f);
//	Geometry cube_translucent = new Geometry("translucent cube", boxshape3);
//	Material mat_tt = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//	//mat_tt.setTexture("ColorMap", assetManager.loadTexture("Textures/ColoredTex/Monkey.png"));
//	mat_tt.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
//	mat_tt.setColor("Color", new ColorRGBA(1f,1f,1f, 1f));
//	cube_translucent.setMaterial(mat_tt); 
//	rootNode.attachChild(cube_translucent); 
	viewPort.setBackgroundColor(ColorRGBA.Gray);
	executor.submit(doNodes);
	//nodeLoop();
    }
    
    public City(){
	Node node = new Node();
	node.setGlobals(globals);
	
	
	width = 50;
	length = width;
	height = 10;
	
	
    }
    
    ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    @Override
    public void destroy() {
        super.destroy();
        executor.shutdown();
    }
    
    float x = 0
	    , y = 0
	    , z = 0;
    
     @Override
    public void simpleUpdate(float tpf) {
	super.simpleUpdate(tpf);
	
	//executor.submit(doNodes);
	rootNode.detachAllChildren();
	for(int i=0;i<nodeCubes.size();i++){
	    rootNode.attachChild(nodeCubes.get(i));
	}
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
     
     private Callable doNodes = new Callable(){
	 //return this;
	 @Override
	 public String call(){
	     //enqueue(
		     //new Callable(){
			 //public String call(){
			     while(y<height){
				if(x<width){
				    if(z<length){
					Node node = new Node(new Vector3f(x, y, z));
					node.setUnderNode(getSurroundingNode(
						new Vector3f(node.getPos().getX(), node.getPos().getY()-1, node.getPos().getZ())));
					makeCubeAt(node.getPos(), node.getType());
					nodes.add(node);
					nodes.trimToSize();
					z++;
					//System.out.println("City.simpleUpdate - pos: " +x+" "+y+" "+z);
					//System.out.println("City.simpleUpdate - z tick");
				    }else{
					z = 0;
					x++;
				    }
				}else{
				    x = 0;
				    y++;
				}
			    }
			     //return "a";
		     //}});
	     
	     return "Finished";
	 }
    };
    
    public void nodeLoop(){
	float x = 0
	    , y = 0
	    , z = 0;
	for(int a=0;a<height;a++){
	    for(int b=0;b<width;b++){
		for(int c=0;c<length;c++){
		    Node node = new Node(new Vector3f(x, y, z));
		    node.setUnderNode(getSurroundingNode(
			    new Vector3f(node.getPos().getX(), node.getPos().getY()-1, node.getPos().getZ())));
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
    
    Node getSurroundingNode(Vector3f posToGet){
	//System.out.println("City.getSurroundingNode");
	Node node = null;
	
	for(int i=0; i<nodes.size(); i++){
	    //System.out.print("City.getSurroundingNode - positions:");
	    //System.out.print("City.getSurroundingNode - " + nodes.get(i).getPos());
	    //System.out.println("City.getSurroundingNode - " + posToGet);
	    if(nodes.get(i).getPos().distance(posToGet) == 0){
		node = nodes.get(i);
		//System.out.println("City.getSurroundingNode - node match");
		return node;
	    }
	}
	return node;
    }
    
    public void makeCubeAt(Vector3f pos, Node.Type type){
	Box boxshape3 = new Box(pos, 0.5f,0.5f,0.5f);
	Geometry cube_translucent = new Geometry("translucent cube", boxshape3);
	Material mat_tt = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
	//mat_tt.setTexture("ColorMap", assetManager.loadTexture("Textures/ColoredTex/Monkey.png"));
	mat_tt.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
	switch(type){
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
	}
	cube_translucent.setMaterial(mat_tt); 
	if(type != Node.Type.AIR){
	    //rootNode.attachChild(cube_translucent);
	    nodeCubes.add(cube_translucent);
	}
    }

}
