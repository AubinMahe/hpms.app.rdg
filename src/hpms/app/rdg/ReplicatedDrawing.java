package hpms.app.rdg;

import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ReplicatedDrawing extends Application {

   private static final String APP_NAME        = "Replicated drawing";
   private static final int    DEFAULT_PORT    = 2416;
   private static final String DEFAULT_ADDRESS = "239.0.0.66";

   private Stage _mainWindow;

   private void quit() {
      final double x = _mainWindow.getX();
      final double y = _mainWindow.getY();
      final double w = _mainWindow.getWidth();
      final double h = _mainWindow.getHeight();
      final Preferences prefs = Preferences.userNodeForPackage( getClass());
      prefs.putDouble( "x", x );
      prefs.putDouble( "y", y );
      prefs.putDouble( "w", w );
      prefs.putDouble( "h", h );
      System.exit( 0 );
   }

   @Override
   public void start( final Stage mainStage ) throws Exception {
      final FXMLLoader loader = new FXMLLoader( ReplicatedDrawingCtrl.class.getResource( "ReplicatedDrawing.fxml" ));
      final Parent     view   = loader.load();
      final Scene      scene  = new Scene( view );
      _mainWindow = mainStage;
      _mainWindow.setTitle( APP_NAME );
      _mainWindow.setOnHiding      ( e -> quit());
      _mainWindow.setOnCloseRequest( e -> quit());
      _mainWindow.setScene( scene );
//      _mainWindow.getIcons().add( new Image( getClass().getResource( "icon_256x256.png" ).toExternalForm()));
      final Preferences prefs = Preferences.userNodeForPackage( getClass());
      final double x = prefs.getDouble( "x", Double.NaN );
      final double y = prefs.getDouble( "y", Double.NaN );
      final double w = prefs.getDouble( "w", Double.NaN );
      final double h = prefs.getDouble( "h", Double.NaN );
      if( ! Double.isNaN( x )) {
         _mainWindow.setX( x );
         _mainWindow.setY( y );
         _mainWindow.setWidth( w );
         _mainWindow.setHeight( h );
      }
      final ReplicatedDrawingCtrl ctrl = loader.getController();
      try {
         ctrl.init( DEFAULT_ADDRESS, DEFAULT_PORT );
         _mainWindow.show();
      }
      catch( final Throwable t ) {
         t.printStackTrace();
         System.exit( 1 );
      }
   }

   public static void main( String[] args ) {
      Application.launch( args );
   }
}
