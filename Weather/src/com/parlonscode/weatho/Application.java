package com.parlonscode.weatho;

import java.awt.EventQueue;

import javax.swing.JFrame;

public class Application {

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			MainFrame mainframe = new MainFrame("Weather");
			mainframe.setResizable(false);
			mainframe.setVisible(true);
			mainframe.pack();
			mainframe.setLocationRelativeTo(null);
			mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		});
	}

}
