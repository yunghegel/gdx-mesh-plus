import com.badlogic.gdx.Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class TestsLauncher {
    public static void main(String[] args) {

        createApplication().setLogLevel(Application.LOG_DEBUG);
    }


    private static Lwjgl3Application createApplication() {

        Lwjgl3ApplicationConfiguration configuration = getDefaultConfiguration();


        return new Lwjgl3Application(new ModelCreatorTest(), configuration);
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL30, 3, 0);
        configuration.setWindowedMode(800, 600);
        configuration.setBackBufferConfig(1, 1, 1, 1, 8, 8, 4);


        return configuration;
    }
}

