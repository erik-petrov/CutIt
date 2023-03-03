module com.example.cutit {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens com.example.cutit to javafx.fxml;
    exports com.example.cutit;
}