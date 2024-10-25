module com.example.tilesumgame {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.tilesumgame to javafx.fxml;
    exports com.example.tilesumgame;
}