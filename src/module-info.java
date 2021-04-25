module hpms.app.rdg {

   requires transitive java.prefs;
   requires transitive java.xml;
   requires transitive java.desktop;

   requires transitive javafx.base;
   requires transitive javafx.controls;
   requires transitive javafx.fxml;
   requires transitive javafx.graphics;
   requires transitive javafx.swing;
   requires transitive javafx.web;

   requires transitive jdk.jsobject;
   requires transitive jdk.xml.dom;

   requires transitive jdk.incubator.foreign;

   exports hpms.app.rdg;
   opens   hpms.app.rdg to javafx.graphics, javafx.fxml;
}
