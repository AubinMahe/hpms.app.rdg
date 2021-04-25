package hpms.app.rdg;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.LinkedList;

import javafx.scene.paint.Color;
import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.FunctionDescriptor;
import jdk.incubator.foreign.LibraryLookup;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

public class GetShapesWrapper implements AutoCloseable {

   private static final Collection<Shape> SHAPES = new LinkedList<>();

   private final MethodHandle  _rdg_get_shapes;
   private final MemorySegment _pointIteratorFunc;
   private final MemorySegment _circleIteratorFunc;
   private final MemorySegment _polygonIteratorFunc;

   public GetShapesWrapper( LibraryLookup rdg ) throws NoSuchMethodException, IllegalAccessException {
      _rdg_get_shapes =
         CLinker.getInstance().downcallHandle(
            rdg.lookup( "rdg_get_shapes" ).get(),
            MethodType.methodType( byte.class, new Class[] { // bool rdg_get_shapes(
               MemoryAddress.class,                          //    rdg_point_iterator   iterator,
               MemoryAddress.class,                          //    rdg_circle_iterator  iterator,
               MemoryAddress.class,                          //    rdg_polygon_iterator iterator,
               MemoryAddress.class }),                       //    void *               user_context )
            FunctionDescriptor.of( CLinker.C_CHAR,           // bool rdg_get_shapes(
               CLinker.C_POINTER,                            //    rdg_point_iterator   iterator,
               CLinker.C_POINTER,                            //    rdg_circle_iterator  iterator,
               CLinker.C_POINTER,                            //    rdg_polygon_iterator iterator,
               CLinker.C_POINTER ));                         //    void *               user_context )
      final MethodHandle pointIteratorHandle =
         MethodHandles.lookup().findStatic(
            GetShapesWrapper.class,
            "pointIterator",
            MethodType.methodType( byte.class, new Class[] {         // bool rdg_point_iterator(
               byte.class, byte.class, byte.class,                   //    byte red, byte green, byte blue,
               double.class, double.class,                           //    double x, double y,
               MemoryAddress.class }));                              //    void * user_context )
      final MethodHandle circleIteratorHandle =
         MethodHandles.lookup().findStatic(
            GetShapesWrapper.class,
            "circleIterator",
            MethodType.methodType( byte.class, new Class[] {         // bool rdg_circle_iterator(
               byte.class, byte.class, byte.class,                   //    byte red, byte green, byte blue,
               double.class, double.class, double.class,             //    double x, double y, double r,
               MemoryAddress.class }));                              //    void * user_context )
      final MethodHandle polygonIteratorHandle =
         MethodHandles.lookup().findStatic(
            GetShapesWrapper.class,
            "polygonIterator",
            MethodType.methodType( byte.class, new Class[] {         // bool rdg_polygon_iterator(
               byte.class, byte.class, byte.class,                   //    byte red, byte green, byte blue,
               int.class, MemoryAddress.class,                       //    int vertex_count, MemoryAddress verticesAddress
               MemoryAddress.class }));                              //    void * user_context )
      _pointIteratorFunc =
         CLinker.getInstance().upcallStub( pointIteratorHandle,
            FunctionDescriptor.of( CLinker.C_CHAR,                   // bool rdg_iterator(
               CLinker.C_CHAR, CLinker.C_CHAR, CLinker.C_CHAR,       //    byte red, byte green, byte blue,
               CLinker.C_DOUBLE, CLinker.C_DOUBLE,                   //    double x, double y,
               CLinker.C_POINTER ));                                 //    void * user_context ));
      _circleIteratorFunc =
         CLinker.getInstance().upcallStub( circleIteratorHandle,
            FunctionDescriptor.of( CLinker.C_CHAR,                   // bool rdg_iterator(
               CLinker.C_CHAR, CLinker.C_CHAR, CLinker.C_CHAR,       //    byte red, byte green, byte blue,
               CLinker.C_DOUBLE, CLinker.C_DOUBLE, CLinker.C_DOUBLE, //    double x, double y, double r
               CLinker.C_POINTER ));                                 //    void * user_context ));
      _polygonIteratorFunc =
         CLinker.getInstance().upcallStub( polygonIteratorHandle,
            FunctionDescriptor.of( CLinker.C_CHAR,                   // bool rdg_iterator(
               CLinker.C_CHAR, CLinker.C_CHAR, CLinker.C_CHAR,       //    byte red, byte green, byte blue,
               CLinker.C_INT, CLinker.C_POINTER,                     //    size_t vertex_count, double * vertices
               CLinker.C_POINTER ));                                 //    void * user_context ));
   }

   @SuppressWarnings("unused")
   public static byte pointIterator( byte red, byte green, byte blue, double x, double y, MemoryAddress user_context ) {
      final int r = ( red   < 0 ) ? 256 + red   : red;
      final int g = ( green < 0 ) ? 256 + green : green;
      final int b = ( blue  < 0 ) ? 256 + blue  : blue;
      final Point point = new Point( x, y );
      point.setColor( Color.rgb( r, g, b ));
      SHAPES.add( point );
      return 1;
   }

   @SuppressWarnings("unused")
   private static byte circleIterator( byte red, byte green, byte blue, double center_x, double center_y, double radius, MemoryAddress user_context ) {
      final int r = ( red   < 0 ) ? 256 + red   : red;
      final int g = ( green < 0 ) ? 256 + green : green;
      final int b = ( blue  < 0 ) ? 256 + blue  : blue;
      final Circle circle = new Circle( center_x, center_y, radius );
      circle.setColor( Color.rgb( r, g, b ));
      SHAPES.add( circle );
      return 1;
   }

   @SuppressWarnings("unused")
   private static byte polygonIterator( byte red, byte green, byte blue, int vertex_count, MemoryAddress verticesAddress, MemoryAddress user_context ) {
      final int r = ( red   < 0 ) ? 256 + red   : red;
      final int g = ( green < 0 ) ? 256 + green : green;
      final int b = ( blue  < 0 ) ? 256 + blue  : blue;
      final int length = 2 * vertex_count;
      try( final var segment = verticesAddress.asSegmentRestricted( length * Double.BYTES )) {
         final ByteBuffer bb = segment.asByteBuffer();
         final double[] vertices = new double[length];
         for( int i = 0; i < length; ++i ) {
            vertices[i] = bb.getDouble();
         }
         final Polygon polygon = new Polygon( vertices );
         polygon.setColor( Color.rgb( r, g, b ));
         SHAPES.add( polygon );
      }
      return 1;
   }

   public Collection<Shape> execute() throws Throwable {
      SHAPES.clear();
      final byte ret = (byte)_rdg_get_shapes.invokeExact(
         _pointIteratorFunc  .address(),
         _circleIteratorFunc .address(),
         _polygonIteratorFunc.address(),
         MemoryAddress.NULL );
      if( ret == 1 ) {
         return SHAPES;
      }
      return null;
   }

   @Override
   public void close() throws Exception {
      _pointIteratorFunc  .close();
      _circleIteratorFunc .close();
      _polygonIteratorFunc.close();
   }
}
