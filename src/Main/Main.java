package Main;

import GUI.LoginGUI;
import GUI.RegisterGUI;
import Handler.Server;

import java.io.IOException;

/**
 * @author by Pham Nguyen My Diem
 * @version 1.0
 * @date 5/16/2021 2:27 PM
 */
public class Main {
    public static void main(String[] args) throws IOException {
        LoginGUI r = new LoginGUI();
        Server.init();
    }
}
