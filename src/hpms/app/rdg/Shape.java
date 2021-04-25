package hpms.app.rdg;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class Shape {

   protected /* */ long      _id;
   protected /* */ Color     _color;
   protected final ShapeType _type;

   Shape( ShapeType type ) {
      _type = type;
   }

   public final void setId( long id ) {
      _id = id;
   }

   public final Shape setColor( Color color ) {
      _color = color;
      return this;
   }

   protected abstract void draw( GraphicsContext ctxt );

   @Override
   public String toString() {
      return String.format( "%s, %d, #%02X%02X%02X",
         getClass().getSimpleName(),
         _id,
         (int)( _color.getRed  () * 255.0 ),
         (int)( _color.getGreen() * 255.0 ),
         (int)( _color.getBlue () * 255.0 ));
   }
}
