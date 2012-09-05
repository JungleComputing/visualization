package util;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import util.TouchHandler.ScreenLocation;

public class TouchToMouseApp {
    private static Socket touchSocket;
    private static ConnectionHandler touchConnection;
    private static TouchHandler touchHandler;
    static Robot robot;
    static JFrame frame;

    boolean connected = false;

    public static void main(String[] args) {

        frame = new JFrame("Touch to Mouse");
        frame.setPreferredSize(new Dimension(625, 125));

        try {
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        frame.addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowClosing(WindowEvent we) {
                                try {
                                    touchSocket.close();
                                } catch (IOException e) {
                                    // IGNORE
                                }
                                System.exit(0);
                            }
                        });
                    } catch (final Exception e) {
                        e.printStackTrace(System.err);
                        System.exit(1);
                    }
                }
            });

            frame.setLayout(new BorderLayout());

            JPanel topButtonPanel = new JPanel();
            topButtonPanel.setLayout(new GridLayout());

            JButton left_4_button = new JButton("Left 4");
            left_4_button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    touchHandler.setScreen(ScreenLocation.LEFT_4);
                }
            });

            JButton middle_4_button = new JButton("Middle 4");
            middle_4_button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    touchHandler.setScreen(ScreenLocation.MIDDLE_4);
                }
            });

            JButton right_4_button = new JButton("Right 4");
            right_4_button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    touchHandler.setScreen(ScreenLocation.RIGHT_4);
                }
            });

            topButtonPanel.add(left_4_button);

            topButtonPanel.add(middle_4_button);
            topButtonPanel.add(right_4_button);

            frame.add(topButtonPanel, BorderLayout.NORTH);

            JPanel bottomButtonPanel = new JPanel();
            GridLayout bottomLayout = new GridLayout();
            bottomLayout.setRows(2);
            bottomButtonPanel.setLayout(bottomLayout);

            JButton top_left_button = new JButton("Top Left");
            top_left_button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    touchHandler.setScreen(ScreenLocation.TL);
                }
            });

            JButton top_middle_left_button = new JButton("Top Middle Left");
            top_middle_left_button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    touchHandler.setScreen(ScreenLocation.TML);
                }
            });

            JButton top_middle_right_button = new JButton("Top Middle Right");
            top_middle_right_button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    touchHandler.setScreen(ScreenLocation.TMR);
                }
            });

            JButton top_right_button = new JButton("Top Right");
            top_right_button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    touchHandler.setScreen(ScreenLocation.TR);
                }
            });

            JButton bottom_left_button = new JButton("Bottom Left");
            bottom_left_button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    touchHandler.setScreen(ScreenLocation.BL);
                }
            });

            JButton bottom_middle_left_button = new JButton("Bottom Middle Left");
            bottom_middle_left_button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    touchHandler.setScreen(ScreenLocation.BML);
                }
            });

            JButton bottom_middle_right_button = new JButton("Bottom Middle Right");
            bottom_middle_right_button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    touchHandler.setScreen(ScreenLocation.BMR);
                }
            });

            JButton bottom_right_button = new JButton("Bottom Right");
            bottom_right_button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    touchHandler.setScreen(ScreenLocation.BR);
                }
            });

            bottomButtonPanel.add(top_left_button);
            bottomButtonPanel.add(top_middle_left_button);
            bottomButtonPanel.add(top_middle_right_button);
            bottomButtonPanel.add(top_right_button);
            bottomButtonPanel.add(bottom_left_button);
            bottomButtonPanel.add(bottom_middle_left_button);
            bottomButtonPanel.add(bottom_middle_right_button);
            bottomButtonPanel.add(bottom_right_button);

            frame.add(bottomButtonPanel, BorderLayout.SOUTH);

            // Display the window.
            frame.pack();

            // center on screen
            frame.setLocationRelativeTo(null);

            frame.setVisible(true);

            touchSocket = new Socket("145.100.39.13", 12345);
            touchHandler = new TouchHandler(robot, ScreenLocation.MIDDLE_4);
            touchConnection = new ConnectionHandler(touchHandler, touchSocket);

            try {
                robot = new Robot();
            } catch (AWTException e1) {
                JOptionPane.showMessageDialog(frame, "Error: Access to OS events is blocked.");
                System.exit(1);
            }

            new Thread(touchConnection).start();

        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(frame, "Error: Touch event machine IP unknown.");
            System.exit(1);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error: Connection to touch event machine unavailable.");
            System.exit(1);
        }
    }
}
