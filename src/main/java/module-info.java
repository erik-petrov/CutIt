module com.example.cutit {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.cutit to javafx.fxml;
    exports com.example.cutit;
}