module inm.gui {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.desktop;
    requires org.controlsfx.controls;
    requires java.sql;
    requires org.xerial.sqlitejdbc;


    opens game.gui to javafx.fxml;
    exports game.gui;
    exports game.mecanique;
    opens game.mecanique to javafx.fxml;
    exports game.model;
    opens game.model to javafx.fxml;
}