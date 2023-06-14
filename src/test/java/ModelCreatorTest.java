import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.ray3k.stripe.ViewportWidget;
import mesh.data.gdx.MeshInfo;
import mesh.data.gdx.VertexMap;
import mesh.data.halfedge.HEdge;
import mesh.data.halfedge.HVert;
import mesh.data.halfedge.HMesh;
import mesh.math.Mathf;
import mesh.shapes.*;

import java.util.Arrays;


public class ModelCreatorTest extends ApplicationAdapter {
    ModelBatch batch;
    Array<ModelInstance> instances = new Array<ModelInstance>();
    PerspectiveCamera camera;
    CameraInputController camController;

    Stage stage;

    ViewportWidget viewportWidget;
    HMesh heMesh;
    VisWindow window;
    ModelInstance torusInstance;
    ScreenViewport viewport;
    Environment environment;
    ShapeRenderer sr;
    SpriteBatch spriteBatch;
    BitmapFont font;
    VertexMap vertexMap;

    @Override
    public void create() {
        VisUI.load();
        stage = new Stage(new ScreenViewport());


       batch = new ModelBatch();
         camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            camera.position.set(10f, 10f, 10f);
            camera.lookAt(0,0,0);
            camera.near = .1f;
            camera.far = 300f;
            camera.update();

            viewport = new ScreenViewport(camera);
            viewportWidget = new ViewportWidget(viewport);
            window = new VisWindow("Viewport");

            window.add(viewportWidget).grow();

            stage.addActor(window);

            camController = new CameraInputController(camera);
            Gdx.input.setInputProcessor(camController);
            Material mat = new Material();
            mat.set(ColorAttribute.createDiffuse(Color.TEAL));

            TorusCreator torusCreator = new TorusCreator(.5f,0.1f,47,12,mat);
            torusCreator.setShapeType(GL20.GL_TRIANGLES);
            Model torus = torusCreator.create();

        torusInstance = new ModelInstance(torus);
        torusInstance.transform.translate(0,2,0);

            CubeCreator cubeCreator = new CubeCreator(.5f,mat);
            cubeCreator.setShapeType(GL20.GL_LINES);
            Model cube = cubeCreator.create();
            ModelInstance cubeInstance = new ModelInstance(cube);

            WedgeCreator wedgeCreator = new WedgeCreator(.5f);
            wedgeCreator.setShapeType(GL20.GL_LINES);
            wedgeCreator.setMaterial(mat);
            Model wedge = wedgeCreator.create();
            ModelInstance wedgeInstance = new ModelInstance(wedge);
            wedgeInstance.transform.translate(0,-2,0);

            UVSphereCreator uvSphereCreator = new UVSphereCreator(30,30,1);
            uvSphereCreator.setShapeType(GL20.GL_LINES).setMaterial(mat);
            Model uvSphere = uvSphereCreator.create();
            ModelInstance uvSphereInstance = new ModelInstance(uvSphere);
            uvSphereInstance.transform.translate(0,4,0);

            SquarePyramidCreator squarePyramidCreator = new SquarePyramidCreator(1,1);
            squarePyramidCreator.setShapeType(GL20.GL_LINES).setMaterial(mat);
            Model squarePyramid = squarePyramidCreator.create();
            ModelInstance squarePyramidInstance = new ModelInstance(squarePyramid);
            squarePyramidInstance.transform.translate(0,-4,0);

            CircleCreator circleCreator = new CircleCreator(30,1,0,mat);
            circleCreator.setShapeType(GL20.GL_LINES);
            Model circle = circleCreator.create();
            ModelInstance circleInstance = new ModelInstance(circle);
            circleInstance.transform.translate(0,6,0);

            ArcCreator arcCreator = new ArcCreator(0, Mathf.PI/2,1,90);
            arcCreator.setShapeType(GL20.GL_LINES).setMaterial(mat);
            Model arc = arcCreator.create();
            ModelInstance arcInstance = new ModelInstance(arc);
            arcInstance.transform.translate(0,-6,0);

            BoxCreator boxCreator = new BoxCreator(1,0.5f,2);
            boxCreator.setShapeType(GL20.GL_LINES).setMaterial(mat);
            Model box = boxCreator.create();
            ModelInstance boxInstance = new ModelInstance(box);
            boxInstance.transform.translate(2,0,0);

            CapsuleCreator capsuleCreator = new CapsuleCreator(1,1,1,1,1,32,32,10,32);
            capsuleCreator.setShapeType(GL20.GL_LINES).setMaterial(mat);
            Model capsule = capsuleCreator.create();
            ModelInstance capsuleInstance = new ModelInstance(capsule);
            capsuleInstance.transform.translate(-2,0,0);

//            ConeCreator coneCreator = new ConeCreator(32,1,0,1,2);
//            coneCreator.setShapeType(GL20.GL_LINES).setMaterial(mat);
//            Model cone = coneCreator.create();
//            ModelInstance coneInstance = new ModelInstance(cone);
//            coneInstance.transform.translate(0,0,4);
//
//
//
//
//            instances.add(coneInstance);
//            instances.add(capsuleInstance);
//            instances.add(boxInstance);
//            instances.add(arcInstance);
//            instances.add(circleInstance);
//            instances.add(squarePyramidInstance);
//            instances.add(wedgeInstance);
//            instances.add(cubeInstance);
//            instances.add(torusInstance);
//            instances.add(uvSphereInstance);


        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        ModelBuilder modelBuilder = new ModelBuilder();
        Model sphere = modelBuilder.createCapsule(1f,3f,10, GL20.GL_TRIANGLES,
                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        heMesh = mesh.data.halfedge.HalfEdge.create(sphere.meshes.first());

        HEdge edge = heMesh.edge;

        System.out.println(heMesh.data);

        ModelInstance sphereInstance = new ModelInstance(sphere);
        sphereInstance.transform.translate(0,0,0);
        instances.add(sphereInstance);

        sr = new ShapeRenderer();
        sr.setAutoShapeType(true);
        sr.setProjectionMatrix(camera.combined);

        spriteBatch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("roboto-medium-small.fnt"));

        FreeTypeFontGenerator ldr = new FreeTypeFontGenerator(Gdx.files.internal("Minecraftia-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 8;
        parameter.color = Color.WHITE;
        font = ldr.generateFont(parameter);
        ldr.dispose();

        Gdx.gl.glEnable(GL20.GL_BLEND);

        vertexMap = new VertexMap(heMesh.data.info);

//        for(HEdge e : heMesh.data.edges){
//            MeshOps.edgeFlip(heMesh,e);
//        }

        for (MeshInfo.HalfEdge e:heMesh.data.info.he_edges){
            System.out.println(e.pair);
        }












//        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render() {
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT | Gdx.gl.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1f);
        camController.update();


        for(ModelInstance instance : instances)
            instance.transform.rotate(0,1,0,.1f);

        batch.begin(camera);
//        batch.render(instances,environment);
        batch.end();
        sr.setProjectionMatrix(camera.combined);
        sr.begin();
        sr.set(ShapeRenderer.ShapeType.Line);
        sr.setColor(1,0,0,0.5f);

        for (HEdge edge:heMesh.data.edges) {


            if(edge.orig==null||edge.next.orig==null)
                continue;
            sr.line(edge.orig.data.v[0],edge.orig.data.v[1],edge.orig.data.v[2],
                    edge.next.orig.data.v[0],edge.next.orig.data.v[1],edge.next.orig.data.v[2]);
        }
        sr.end();
        Vector3 proj = new Vector3();
        for(HVert v:heMesh.data.verts){
            proj=camera.project(new Vector3(v.data.v[0],v.data.v[1],v.data.v[2]));


            int indx = Arrays.stream(heMesh.data.verts).filter(v1 -> v1.getID()==v.getID()).findFirst().get().getID();
            String index = String.valueOf(indx);


            spriteBatch.begin();
            font.draw(spriteBatch,index,proj.x,proj.y);
            spriteBatch.end();
        }



    }
}
