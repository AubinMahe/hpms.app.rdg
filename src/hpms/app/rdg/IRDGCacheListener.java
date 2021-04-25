package hpms.app.rdg;

import java.util.Collection;

public interface IRDGCacheListener {

   void cacheRefreshed( Collection<Shape> shapes ) throws Throwable;
}
