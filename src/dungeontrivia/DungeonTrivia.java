/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeontrivia;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.*;

/**
 *
 * @author luisfelipealvarez
 */
public class DungeonTrivia extends JFrame implements ActionListener {

    public static Connection con;
    public static Statement st;
    public static ResultSet rs;

  

    public static boolean offline;
    public static Game n;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        n = new Game("Dungeon Trivia", 1080, 800);
        n.start();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {

    }

}
