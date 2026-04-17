module pjvsemproj.pjvsemproj {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.logging;

    opens pjvsemproj to javafx.fxml;
    exports pjvsemproj;
}