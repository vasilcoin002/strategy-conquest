module semproject.semproject {
    requires javafx.controls;
    requires javafx.fxml;


    opens semproject.semproject to javafx.fxml;
    exports semproject.semproject;
}