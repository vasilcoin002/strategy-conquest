module pjvsemproj.pjvsemproj {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires pjvsemproj.pjvsemproj;

    opens pjvsemproj to javafx.fxml;
    exports pjvsemproj;
}