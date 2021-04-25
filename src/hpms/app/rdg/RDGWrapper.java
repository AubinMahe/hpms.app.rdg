package hpms.app.rdg;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Collection;

import javafx.scene.paint.Color;
import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.FunctionDescriptor;
import jdk.incubator.foreign.LibraryLookup;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

public class RDGWrapper implements AutoCloseable {

   private static final LibraryLookup rdg = LibraryLookup.ofLibrary( "rdg-d" );
   private static final MethodHandle  rdg_new =
      CLinker.getInstance().downcallHandle(
         rdg.lookup( "rdg_new" ).get(),
         MethodType.methodType( byte.class, new Class[] {         // bool rdg_new(
               MemoryAddress.class,                               //    const char *         group,
               short.class,                                       //    unsigned short       port,
               MemoryAddress.class,                               //    rdg_change_callback  listener,
               MemoryAddress.class }),                            //    void *               user_context );
         FunctionDescriptor.of( CLinker.C_CHAR,                   // bool rdg_new(
            CLinker.C_POINTER,                                    //    const char *         group,
            CLinker.C_SHORT,                                      //    unsigned short       port,
            CLinker.C_POINTER,                                    //    rdg_change_callback  listener,
            CLinker.C_POINTER ));                                 //    void *               user_context );
   private static final MethodHandle rdg_add_point =
      CLinker.getInstance().downcallHandle(
         rdg.lookup( "rdg_add_point" ).get(),
         MethodType.methodType( byte.class, new Class[] {         // bool rdg_add_point(
            byte.class, byte.class, byte.class,                   //    byte red, byte green, byte blue,
            double.class, double.class,                           //    double x, double y,
            MemoryAddress.class }),                               //    rkv_id * id )
         FunctionDescriptor.of( CLinker.C_CHAR,                   // bool rdg_add_point(
            CLinker.C_CHAR, CLinker.C_CHAR, CLinker.C_CHAR,       //    byte red, byte green, byte blue,
            CLinker.C_DOUBLE, CLinker.C_DOUBLE,                   //    double x, double y,
            CLinker.C_POINTER ));                                 //    rkv_id * id )
   private static final MethodHandle rdg_add_circle =
      CLinker.getInstance().downcallHandle(
         rdg.lookup( "rdg_add_circle" ).get(),
         MethodType.methodType( byte.class, new Class[] {         // bool rdg_add_circle(
            byte.class, byte.class, byte.class,                   //    byte red, byte green, byte blue,
            double.class, double.class, double.class,             //    double center_x, double center_y, double radius,
            MemoryAddress.class }),                               //    rkv_id * id )
         FunctionDescriptor.of( CLinker.C_CHAR,                   // bool rdg_add_circle(
            CLinker.C_CHAR, CLinker.C_CHAR, CLinker.C_CHAR,       //    byte red, byte green, byte blue,
            CLinker.C_DOUBLE, CLinker.C_DOUBLE, CLinker.C_DOUBLE, //    double center_x, double center_y, double radius,
            CLinker.C_POINTER ));                                 //    rkv_id * id )
   private static final MethodHandle rdg_add_polygon =
      CLinker.getInstance().downcallHandle(
         rdg.lookup( "rdg_add_polygon" ).get(),
         MethodType.methodType( byte.class, new Class[] {         // bool rdg_add_polygon(
            byte.class, byte.class, byte.class,                   //    byte red, byte green, byte blue,
            int.class, MemoryAddress.class,                       //    size_t vertex_count, double * vertices,
            MemoryAddress.class }),                               //    rkv_id * id )
         FunctionDescriptor.of( CLinker.C_CHAR,                   // bool rdg_add_polygon(
            CLinker.C_CHAR, CLinker.C_CHAR, CLinker.C_CHAR,       //    byte red, byte green, byte blue,
            CLinker.C_INT, CLinker.C_POINTER,                     //    size_t vertex_count, double * vertices,
            CLinker.C_POINTER ));                                 //    rkv_id * id )
   private static final MethodHandle rdg_delete =
      CLinker.getInstance().downcallHandle(
         rdg.lookup( "rdg_delete" ).get(),
         MethodType.methodType( void.class, new Class[] {}),
         FunctionDescriptor.ofVoid());

   private static MemorySegment     _javaListenerFunc;
   private static GetShapesWrapper  _rdg_get_shapes;
   private static IRDGCacheListener _listener;
   private static boolean           _programDone = false;

   @SuppressWarnings("unused")
   private static void cacheRefreshed() throws Throwable {
      final Collection<Shape> shapes = _rdg_get_shapes.execute();
      _listener.cacheRefreshed( shapes );
      _programDone = shapes.size() == 3;
   }

   public RDGWrapper( String group, int port, IRDGCacheListener listener ) throws Throwable {
      _listener       = listener;
      _rdg_get_shapes = new GetShapesWrapper( rdg );
      try( var groupCstr = CLinker.toCString( group )) {
         final MethodHandle javaListenerHandle =
            MethodHandles.lookup().findStatic(
               RDGWrapper.class,
               "cacheRefreshed",
               MethodType.methodType( void.class ));
         _javaListenerFunc =
            CLinker.getInstance().upcallStub(
               javaListenerHandle,
               FunctionDescriptor.ofVoid());
         final byte ret = (byte)rdg_new.invokeExact(
            groupCstr.address(), (short)port, _javaListenerFunc.address(), MemoryAddress.NULL );
         if( ret == 0 ) {
            throw new IllegalStateException();
         }
      }
   }

   private static byte color2Byte( double percent ) {
      double value = 255.0 * percent;
      if( value > 127.0 ) {
         value -= 256.0;
      }
      return (byte)value;
   }

   @SuppressWarnings("static-method")
   public boolean addPoint( Point point ) throws Throwable {
      try( var c_id = MemorySegment.allocateNative( 8 )) {
         final byte red   = color2Byte( point._color.getRed());
         final byte green = color2Byte( point._color.getGreen());
         final byte blue  = color2Byte( point._color.getBlue());
         final boolean ok = (byte)rdg_add_point.invokeExact(
            red, green, blue,
            point._x, point._y,
            c_id.address()) == 1;
         if( ok ) {
            point.setId( c_id.asByteBuffer().getLong());
         }
         return ok;
      }
   }

   @SuppressWarnings("static-method")
   public boolean addCircle( Circle circle ) throws Throwable {
      try( var c_id = MemorySegment.allocateNative( 8 )) {
         final byte red   = color2Byte( circle._color.getRed());
         final byte green = color2Byte( circle._color.getGreen());
         final byte blue  = color2Byte( circle._color.getBlue());
         final boolean ok = (byte)rdg_add_circle.invokeExact(
            red, green, blue,
            circle._centerX, circle._centerY, circle._radius,
            c_id.address()) == 1;
         if( ok ) {
             circle.setId( c_id.asByteBuffer().getLong());
         }
         return ok;
      }
   }

   private static MemorySegment createVertices( double[] points ) {
      final var seg = MemorySegment.allocateNative( points.length * Double.BYTES );
      final var bb  = seg.asByteBuffer();
      for( final double p : points ) {
         bb.putDouble( p );
      }
      return seg;
   }

   @SuppressWarnings("static-method")
   public boolean addPolygon( Polygon polygon ) throws Throwable {
      try( var c_id       = MemorySegment.allocateNative( 8 );
           var c_vertices = createVertices( polygon._points ))
      {
         final byte red   = color2Byte( polygon._color.getRed());
         final byte green = color2Byte( polygon._color.getGreen());
         final byte blue  = color2Byte( polygon._color.getBlue());
         final boolean ok = (byte)rdg_add_polygon.invokeExact(
            red, green, blue,
            polygon._x.length, c_vertices.address(),
            c_id.address()) == 1;
         if( ok ) {
             polygon.setId( c_id.asByteBuffer().getLong());
         }
         return ok;
      }
   }

   @Override
   public void close() throws Exception {
      try {
         rdg_delete.invokeExact();
      }
      catch( final Throwable e ) {
         e.printStackTrace();
      }
      _javaListenerFunc.close();
   }

   /**
    * Main de test minimal.
    * @param args aucun
    * @throws Throwable
    */
   public static void main( String[] args ) throws Throwable {
      try( final RDGWrapper wrapper =
         new RDGWrapper( "239.0.0.66", 2416, shapes -> System.err.println( "Cache refreshed: " + shapes )))
      {
         wrapper.addPoint((Point)new Point( 123.45, 98.76 ).setColor( Color.RED ));
         wrapper.addCircle((Circle)new Circle( 200, 200, 150 ).setColor( Color.GREEN ));
         wrapper.addPolygon((Polygon)new Polygon(
            200, 200,
            250, 150,
            300, 150,
            350, 200,
            350, 250,
            300, 300,
            250, 300,
            200, 250 ).setColor( Color.BLUE ));
         while( ! _programDone ) {
            Thread.sleep( 20 ); // ms
         }
      }
      System.err.println( "Done." );
   }
}
