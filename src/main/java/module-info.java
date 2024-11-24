module com.example.toursystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.sql;
    requires org.json;
    requires javafx.web;
    requires jdk.jsobject;

    opens com.example.toursystem to javafx.fxml;
    exports com.example.toursystem;
}
