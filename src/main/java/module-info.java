module com.example.tilesumgame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.desktop;

    opens com.example.tilesumgame to javafx.fxml;
    exports com.example.tilesumgame;
}