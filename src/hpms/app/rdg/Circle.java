package hpms.app.rdg;

import javafx.scene.canvas.GraphicsContext;

public final class Circle extends Shape {

   final double _centerX;
   final double _centerY;
   final double _radius;

   public Circle( double centerX, double centerY, double radius ) {
      super( ShapeType.CIRCLE );
      _centerX = centerX;
      _centerY = centerY;
      _radius  = radius;
   }

   @Override
   protected void draw( GraphicsContext ctxt ) {
      ctxt.setStroke( _color );
      final double d = 2.0 * _radius;
      ctxt.strokeOval( _centerX - _radius, _centerY - _radius, d, d );
   }

   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append( "{ " );
      sb.append( super.toString());
      sb.append( ", " );
      sb.append( _centerX );
      sb.append( ", " );
      sb.append( _centerY );
      sb.append( ", " );
      sb.append( _radius );
      sb.append( " }" );
      return sb.toString();
   }
}
