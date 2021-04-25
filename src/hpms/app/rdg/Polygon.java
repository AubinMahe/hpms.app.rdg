package hpms.app.rdg;

import javafx.scene.canvas.GraphicsContext;

public final class Polygon extends Shape {

   final double[] _points;
   final double[] _x;
   final double[] _y;

   public Polygon( double ... points ) {
      super( ShapeType.POLYGON );
      _points = points;
      _x = new double[_points.length/2];
      _y = new double[_points.length/2];
      for( int i = 0; i < _x.length; ++i ) {
         _x[i] = _points[2*i+0];
         _y[i] = _points[2*i+1];
      }
   }

   @Override
   protected void draw( GraphicsContext ctxt ) {
      ctxt.setStroke( _color );
      ctxt.strokePolygon( _x, _y, _x.length );
   }

   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append( "{ " );
      sb.append( super.toString());
      for( int i = 0; i < _x.length; ++i ) {
         sb.append( ", { " );
         sb.append( _x[i] );
         sb.append( ", " );
         sb.append( _y[i] );
         sb.append( " }" );
      }
      sb.append( '}' );
      return sb.toString();
   }
}
