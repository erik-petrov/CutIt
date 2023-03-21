module com.example.cutit {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens com.project.cutit to javafx.fxml;
    exports com.project.cutit;
}