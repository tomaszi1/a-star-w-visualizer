package edu.agh.idziak.asw.visualizer;

import com.airhacks.afterburner.injection.Injector;
import edu.agh.idziak.asw.visualizer.gui.root.RootPresenter;
import edu.agh.idziak.asw.visualizer.gui.root.RootView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Loader extends Application {

    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("A*W Visualizer 0.1");

        RootView rootView = new RootView();

        Scene scene = new Scene(rootView.getView(), 800, 600);

        RootPresenter presenter = (RootPresenter) rootView.getPresenter();
        presenter.initScene(scene);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        Injector.forgetAll();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
