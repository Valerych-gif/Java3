package client;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;


public class RegForm {
    public TextField login;
    public TextField pass;
    public TextField nick;
    private Network network;

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void registration() {
        String l = login.getText().trim();
        String p = pass.getText().trim();
        String n = nick.getText().trim();
        if (l.equals("")||p.equals("")||n.equals("")){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Внимание!");
            alert.setHeaderText("Все поля обязательны");
            alert.setContentText("Заполните, пожалуйста, все поля и повторите попытку");
            alert.show();
            return;
        }
        network.registration(String.format("/reg %s %s %s", l, p, n));

    }

}
