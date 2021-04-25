package hpms.app.rdg;

import javafx.scene.canvas.GraphicsContext;

public final class Point extends Shape {

   final double _x;
   final double _y;

   public Point( double x, double y ) {
      super( ShapeType.POINT );
      _x = x;
      _y = y;
   }

   @Override
   protected void draw( GraphicsContext ctxt ) {
      ctxt.setFill( _color );
      ctxt.fillOval( _x-1.0, _y-1.0, 2.0, 2.0 );
   }

   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append( "{ " );
      sb.append( super.toString());
      sb.append( ", " );
      sb.append( _x );
      sb.append( ", " );
      sb.append( _y );
      sb.append( " }" );
      return sb.toString();
   }
}
