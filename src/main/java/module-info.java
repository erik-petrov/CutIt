module com.project.cutit {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires org.controlsfx.controls;
    requires ffmpeg;
    requires org.apache.commons.lang3;

    opens com.project.cutit to javafx.fxml;
    exports com.project.cutit;
    exports com.project.cutit.controllers;
    opens com.project.cutit.controllers to javafx.fxml;
}