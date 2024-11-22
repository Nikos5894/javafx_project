module com.mycompany.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.web;
    requires org.json;


    opens com.example.toursystem to javafx.fxml;
    exports com.example.toursystem;
}