package client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private TextArea textArea;

    @FXML
    private TextField msgField, loginField;

    @FXML
    private HBox msgPanel, authPanel;

    @FXML
    private PasswordField passField;

    @FXML
    private ListView<String> clientsList;

    private Network network;

    private boolean authenticated;
    private String nickname;
    private String login;

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
        authPanel.setVisible(!authenticated);
        authPanel.setManaged(!authenticated);
        msgPanel.setVisible(authenticated);
        msgPanel.setManaged(authenticated);
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

    @FXML
    private void sendAuth() {
        String login = loginField.getText();
        String password = passField.getText();
        if (login.equals("") || password.equals("")) return;
        network.sendAuth(loginField.getText(), passField.getText());
        this.login = loginField.getText();
        loginField.clear();
        passField.clear();
    }

    @FXML
    private void sendMsg() {
        if (network.sendMsg(msgField.getText())) {
            msgField.clear();
            msgField.requestFocus();
        }
    }

    public void sendExit() {
        network.sendMsg("/end");
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
            String line;
            List<String> list = new ArrayList<>();
            textArea.clear();
            try (RandomAccessFile raf = new RandomAccessFile("history_" + login + ".txt", "r")) {
                while (true) {
                    line = raf.readLine();
                    if (line == null) break;
                    if (list.size() >= 100) {
                        list.remove(0);
                    }
                    String utf8 = new String(line.getBytes("ISO-8859-1"), "UTF-8");
                    list.add(utf8);
                }
                for (int i = 0; i < list.size(); i++) {
                    textArea.appendText(list.get(i) + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        network.setCallOnMsgReceived(args -> {
            String msg = args[0].toString();
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
            } else if (msg.startsWith("/changenick ")) {
                nickname = msg.split("\\s")[1];
            } else {
                textArea.appendText(msg + "\n");
                try (RandomAccessFile raf = new RandomAccessFile("history_" + login + ".txt", "rw")) {
                    raf.seek(raf.length());
                    raf.writeUTF(msg + "\n");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void clientClickHandler(MouseEvent event) {
        if (event.getClickCount() == 2) {
            String nickname = clientsList.getSelectionModel().getSelectedItem();
            msgField.setText("/w " + nickname + " ");
            msgField.requestFocus();
            msgField.selectEnd();
        }
    }
}
