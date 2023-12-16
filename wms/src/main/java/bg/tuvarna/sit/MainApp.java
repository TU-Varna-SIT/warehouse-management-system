package bg.tuvarna.sit;

import bg.tuvarna.sit.wms.dao.UserDao;
import bg.tuvarna.sit.wms.exceptions.RegistrationException;
import bg.tuvarna.sit.wms.service.PasswordHashingService;
import bg.tuvarna.sit.wms.service.UserService;
import bg.tuvarna.sit.wms.util.JpaUtil;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main application class for the JavaFX application.
 * This class is responsible for loading and displaying the primary stage and its contents.
 */
public class MainApp extends Application {

  private static final Logger LOGGER = LogManager.getLogger(MainApp.class);

  /**
   * Starts the JavaFX application by setting the primary stage.
   *
   * @param stage The primary stage for this application, onto which the application scene can be set.
   * @throws IOException If the FXML file for the home view cannot be loaded.
   */
  @Override
  public void start(Stage stage) throws IOException {
    Parent root = FXMLLoader.load(getClass().getResource("/views/home.fxml"));
    stage.setTitle("Home");
    stage.setScene(new Scene(root));
    stage.show();
  }

  /**
   * The main entry point for all JavaFX applications.
   * The launch method is called to start the JavaFX application.
   *
   * @param args The command line arguments passed to the application. Not used in this application.
   */
  public static void main(String[] args) {
    launch(args);
  }

  /**
   * Initializes the application before the JavaFX application thread is started.
   * This method is called after the JavaFX system is initialized and before the
   * application start method is called.
   *
   * @throws Exception if an error occurs during initialization.
   */
  @Override
  public void init() throws Exception {
    super.init();
    initializeApplication();
  }

  /**
   * Performs application-wide initialization tasks.
   * Specifically, it initializes administrators in the system.
   */
  private void initializeApplication() {
    try {
      new UserService(new UserDao(JpaUtil.getEntityManagerFactory()), new PasswordHashingService())
              .initializeAdministrators();
    } catch (RegistrationException | InvalidKeySpecException | NoSuchAlgorithmException e) {
      LOGGER.error("Error during application initialization: ", e);
    }
  }
}

