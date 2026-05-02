module pjvsemproj.pjvsemproj {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.logging;
    requires com.google.gson;

    opens pjvsemproj to javafx.fxml;
    exports pjvsemproj;
    exports pjvsemproj.dto;
}