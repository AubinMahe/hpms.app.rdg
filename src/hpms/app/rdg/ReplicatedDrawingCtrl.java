package hpms.app.rdg;

import java.util.Collection;
import java.util.Random;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.paint.Color;

public class ReplicatedDrawingCtrl implements IRDGCacheListener {

   @FXML private ComboBox<String> _shapeSelector;
   @FXML private ColorPicker      _colorPicker;
   @FXML private Canvas           _canvas;

   private final Random     _random = new Random( System.currentTimeMillis());
   private /* */ RDGWrapper _rdg;

   void init( String address, int port ) throws Throwable {
      _rdg = new RDGWrapper( address, port, this );
      _canvas.setOnMouseClicked( e -> addShape( e.getX(), e.getY()));
   }

   private void addShape( double x, double y ) {
      try {
         final String shapeName = _shapeSelector.getSelectionModel().getSelectedItem();
         final Color color = _colorPicker.getValue();
         if( shapeName != null ) {
            switch( shapeName ) {
            case "Point":
               _rdg.addPoint((Point)new Point( x, y ).setColor( color ));
               break;
            case "Cercle":
               _rdg.addCircle((Circle)new Circle( x, y, 20.0 + 100.0 * _random.nextDouble()).setColor( color ));
               break;
            case "Polygone":
               final double radius = 20.0 + 100.0 * _random.nextDouble();
               int vertex_count = _random.nextInt( 20 );
               if( vertex_count < 3 ) {
                  vertex_count = 3;
               }
               final double[] points     = new double[ 2 * vertex_count ];
               final double   deltaAngle = 4.0 * Math.PI / points.length;
               for( int i = 0; i < vertex_count; ++i ) {
                  final double angle = i * deltaAngle;
                  points[2*i+0] = x + radius * Math.cos( angle );
                  points[2*i+1] = y + radius * Math.sin( angle );
               }
               _rdg.addPolygon((Polygon)new Polygon( points ).setColor( color ));
               break;
            }
         }
      }
      catch( final Throwable t ) {
         t.printStackTrace();
      }
   }

   @Override
   public void cacheRefreshed( Collection<Shape> shapes ) throws Throwable {
      for( final Shape shape : shapes ) {
         shape.draw( _canvas.getGraphicsContext2D());
      }
   }
}
