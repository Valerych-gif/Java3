package client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private static final boolean LOGGER_IS_ON = true;
    private static final int QUANTITY_OF_MESSAGE = 100;
    private boolean isDark = false;
    private String darkStyle = getClass().getResource("/stylesDark.css").toExternalForm();
    private String lightStyle = getClass().getResource("/stylesLight.css").toExternalForm();

    @FXML
    Parent mainWindow;

    @FXML
    TextArea textArea;

    @FXML
    TextField messageField, loginField;

    @FXML
    HBox authPanel;

    @FXML
    PasswordField passField;

    @FXML
    ListView<String> clientsList;

    private Network network;

    private boolean authenticated;
    private String nickname;
    private String login;

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
        authPanel.setVisible(!authenticated);
        authPanel.setManaged(!authenticated);
        clientsList.setVisible(authenticated);
        clientsList.setManaged(authenticated);
        if (!authenticated) {
            nickname = "";
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAuthenticated(false);
        clientsList.setOnMouseClicked(this::clientClickHandler);
        createNetwork();
        network.connect();
    }

    public void sendAuth() {
        login = loginField.getText();
        network.sendAuth(login, passField.getText());
        loginField.clear();
        passField.clear();
    }

    public void sendMsg() {
        String msg = messageField.getText();
        if (network.sendMsg(msg)) {
            messageField.clear();
            messageField.requestFocus();
            if (LOGGER_IS_ON) {
                HistoryLogger.logHistory(login, nickname, msg);
            }
        }
    }

    public void showAlert(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
            alert.showAndWait();
        });
    }

    public void createNetwork() {
        network = new Network();
        network.setCallOnException(args -> showAlert(args[0].toString()));

        network.setCallOnCloseConnection(args -> setAuthenticated(false));

        network.setCallOnAuthenticated(args -> {
            setAuthenticated(true);
            nickname = args[0].toString();
            if (LOGGER_IS_ON){
                HistoryLogger.readHistoryLogFile(new File(String.format("Logs/history_%s.log", login)));
                List<String> log = HistoryLogger.getLog();
                for (int i = 0; (i < log.size())&&i<QUANTITY_OF_MESSAGE; i++) {
                    textArea.appendText(log.get(i) + "\n");
                }
            }
        });

        network.setCallOnMsgReceived(args -> {
            String msg = args[0].toString();
            if (msg.startsWith("/")) {
                if (msg.startsWith("/newnick")){
                    String[] tokens = msg.split("\\s");
                    if (tokens.length>1){
                        nickname=tokens[1];
                    }
                }

                if (msg.startsWith("/clients ")) {
                    String[] tokens = msg.split("\\s");
                    Platform.runLater(() -> {
                        clientsList.getItems().clear();
                        for (int i = 1; i < tokens.length; i++) {
                            if (!nickname.equals(tokens[i])) {
                                clientsList.getItems().add(tokens[i]);
                            }
                        }
                    });
                }

            } else {
                textArea.appendText(msg + "\n");
            }
        });
    }

    private void clientClickHandler(MouseEvent event) {
        if (event.getClickCount() == 2) {
            String nickname = clientsList.getSelectionModel().getSelectedItem();
            messageField.setText("/w " + nickname + " ");
            messageField.requestFocus();
            messageField.selectEnd();
        }
    }



    public void close() {
        network.sendMsg("/end");
        Platform.exit();
        System.exit(0);
    }

    public void setTheme(ActionEvent actionEvent) {
        if (!isDark){
            setDarkTheme();
            isDark=true;
        } else {
            setLightTheme();
            isDark=false;
        }
    }

    private void setDarkTheme (){
        mainWindow.getStylesheets().add(darkStyle);
        mainWindow.getStylesheets().remove(lightStyle);
    }

    private void setLightTheme (){
        mainWindow.getStylesheets().add(lightStyle);
        mainWindow.getStylesheets().remove(darkStyle);
    }

    public void showAbout(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About GBChat");
        alert.setHeaderText("GBChat - Simple chat by GeekBrains student");
        alert.setContentText("Author Bulekov Valery\nVersion 0.0.1");
        alert.show();
    }

    public void registration(ActionEvent actionEvent) {

        try {
            RegForm regForm = new RegForm();
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("regForm.fxml"));
            regForm.setNetwork(network);
            loader.setController(regForm);
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Регистрация");
            Button regButton = new Button("Зарегистрироваться");
            regButton.setOnAction(event -> regForm.registration());
            VBox vBox = new VBox(root, regButton);
            stage.setScene(new Scene(vBox, 300, 100));
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
