package pl.edu.agh.idziak.asw.visualizer;

import com.airhacks.afterburner.injection.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.idziak.asw.visualizer.gui.root.RootPresenter;
import pl.edu.agh.idziak.asw.visualizer.gui.root.RootView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static java.lang.String.format;

public class Loader extends Application {
    private static final Logger LOG = LoggerFactory.getLogger(Loader.class);
    private static final String ENV_VAR_MAXIMIZE = "maximize";

    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("A*W Visualizer 0.1");

        RootView rootView = new RootView();

        Scene scene = new Scene(rootView.getView(), 800, 600);

        RootPresenter presenter = (RootPresenter) rootView.getPresenter();
        presenter.initScene(scene);

        primaryStage.setScene(scene);
        optionallyMaximizeWindow(primaryStage);
        primaryStage.show();
    }

    private static void optionallyMaximizeWindow(Stage primaryStage) {
        if ("true".equalsIgnoreCase(System.getProperty(ENV_VAR_MAXIMIZE, System.getenv(ENV_VAR_MAXIMIZE)))) {
            LOG.info("Window maximized");
            primaryStage.setMaximized(true);
            return;
        }
        LOG.info("Window not maximized. Use '{}=true' env variable to maximize on startup.", ENV_VAR_MAXIMIZE);
    }

    @Override
    public void stop() throws Exception {
        Injector.forgetAll();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
