module com.project.cutit {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires org.controlsfx.controls;

    opens com.project.cutit to javafx.fxml;
    exports com.project.cutit;
}