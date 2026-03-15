module pjvsemproj.pjvsemproj {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens pjvsemproj to javafx.fxml;
    exports pjvsemproj;
}