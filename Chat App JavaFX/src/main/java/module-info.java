module com.chat {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens com.chat.client to javafx.fxml;
    opens com.chat.server to javafx.fxml;

    exports com.chat.client;
    exports com.chat.server;
}
