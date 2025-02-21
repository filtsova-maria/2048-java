module com.example.tilesumgame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;


    opens com.example.tilesumgame to javafx.fxml;
    exports com.example.tilesumgame;
}