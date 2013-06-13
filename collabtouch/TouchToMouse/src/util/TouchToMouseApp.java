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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import util.TouchHandler.ScreenLocation;

public class TouchToMouseApp {
    private static final int FS_RESOLUTION_WIDTH = 3840;
    private static final int FS_RESOLUTION_HEIGHT = 2160;

    private static Socket touchSocket;
    private static ConnectionHandler touchConnection;
    private static TouchHandler touchHandler;
    static Robot robot;
    static JFrame frame;

    final static JPanel initialPanel = new JPanel();
    final static JPanel buttonPanel = new JPanel();

    private boolean listeningToEvents;

    /**
     * Basic constructor for TouchToMouseApp
     */
    public TouchToMouseApp() {
        frame = new JFrame("CollabTouch");
        frame.setPreferredSize(new Dimension(625, 150));

        createInitialPanel();
        createButtonPanel();

        frame.getContentPane().add(initialPanel);
        // frame.add(buttonPanel);

        // Display the window.
        frame.pack();

        // center on screen
        frame.setLocationRelativeTo(null);

        final StartStopDeamon deamon = new StartStopDeamon(this, 12346);
        new Thread(deamon).start();

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                deamon.setRunning(false);
                System.exit(0);
            }
        });

        frame.setVisible(true);

        try {
            robot = new Robot();
        } catch (AWTException e1) {
            e1.printStackTrace();
        }
    }

    static void connect(String address, int port) {
        try {
            touchSocket = new Socket(address, port);
            touchHandler = new TouchHandler(robot, ScreenLocation.MIDDLE_4);
            touchConnection = new ConnectionHandler(touchHandler, touchSocket);
            new Thread(touchConnection).start();

            showButtons();
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(frame, "Unknown host: " + address);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame,
                    "IO Exception: " + e.getMessage());
        }
        // showButtons();
    }

    static void showButtons() {
        frame.setVisible(false);

        frame.remove(initialPanel);

        frame.getContentPane().add(buttonPanel);

        frame.setVisible(true);
    }

    static void createInitialPanel() {
        initialPanel.setLayout(new BorderLayout());

        final JTextField adressField = new JTextField("145.100.39.11");
        final JTextField portField = new JTextField("12345");

        JPanel top = new JPanel(new GridLayout());
        top.add(new JLabel("Address:"));
        top.add(adressField);
        initialPanel.add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout());
        center.add(new JLabel("Port:"));
        center.add(portField);
        initialPanel.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout());
        JButton connect_button = new JButton("Connect");
        connect_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connect(adressField.getText(),
                        Integer.parseInt(portField.getText()));
            }
        });
        bottom.add(connect_button);
        initialPanel.add(bottom, BorderLayout.SOUTH);
        initialPanel.setVisible(true);
    }

    static void createButtonPanel() {
        buttonPanel.setLayout(new BorderLayout());

        JPanel topButtonPanel = new JPanel();
        topButtonPanel.setLayout(new GridLayout());

        JButton left_4_button = new JButton("LEFT 1920x1080");
        left_4_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                touchHandler.setScreen(ScreenLocation.LEFT_4);
            }
        });

        JButton middle_4_button = new JButton("MID 1920x1080");
        middle_4_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                touchHandler.setScreen(ScreenLocation.MIDDLE_4);
            }
        });

        JButton right_4_button = new JButton("RIGHT 1920x1080");
        right_4_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                touchHandler.setScreen(ScreenLocation.RIGHT_4);
            }
        });

        topButtonPanel.add(left_4_button);

        topButtonPanel.add(middle_4_button);
        topButtonPanel.add(right_4_button);

        buttonPanel.add(topButtonPanel, BorderLayout.NORTH);

        JPanel middleButtonPanel = new JPanel();

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
        middleButtonPanel.setLayout(new GridLayout(2, 4));

        middleButtonPanel.add(top_left_button, 0);
        middleButtonPanel.add(top_middle_left_button, 1);
        middleButtonPanel.add(top_middle_right_button, 2);
        middleButtonPanel.add(top_right_button, 3);
        middleButtonPanel.add(bottom_left_button, 4);
        middleButtonPanel.add(bottom_middle_left_button, 5);
        middleButtonPanel.add(bottom_middle_right_button, 6);
        middleButtonPanel.add(bottom_right_button, 7);

        buttonPanel.add(middleButtonPanel, BorderLayout.CENTER);

        JPanel bottomButtonPanel = new JPanel(new GridLayout(2, 1));
        JPanel bottomButtonPanel1 = new JPanel(new GridLayout(1, 1));
        JPanel bottomButtonPanel2 = new JPanel(new GridLayout(1, 3));

        JButton fr_entire_screen = new JButton("7680x2160");
        fr_entire_screen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                touchHandler.screenHeight = FS_RESOLUTION_HEIGHT;
                touchHandler.screenWidth = FS_RESOLUTION_WIDTH * 2;
                touchHandler.setScreen(ScreenLocation.ALL);
            }
        });
        bottomButtonPanel1.add(fr_entire_screen);

        JButton fr_left_4_button = new JButton("LEFT 3840x2160");
        fr_left_4_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                touchHandler.screenHeight = FS_RESOLUTION_HEIGHT;
                touchHandler.screenWidth = FS_RESOLUTION_WIDTH;
                touchHandler.setScreen(ScreenLocation.LEFT_4);
            }
        });
        bottomButtonPanel2.add(fr_left_4_button);

        JButton fr_middle_4_button = new JButton("MID 3840x2160");
        fr_middle_4_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                touchHandler.screenHeight = FS_RESOLUTION_HEIGHT;
                touchHandler.screenWidth = FS_RESOLUTION_WIDTH;
                touchHandler.setScreen(ScreenLocation.MIDDLE_4);
            }
        });
        bottomButtonPanel2.add(fr_middle_4_button);

        JButton fr_right_4_button = new JButton("RIGHT 3840x2160");
        fr_right_4_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                touchHandler.screenHeight = FS_RESOLUTION_HEIGHT;
                touchHandler.screenWidth = FS_RESOLUTION_WIDTH;
                touchHandler.setScreen(ScreenLocation.RIGHT_4);
            }
        });
        bottomButtonPanel2.add(fr_right_4_button);

        bottomButtonPanel.add(bottomButtonPanel1, 0);
        bottomButtonPanel.add(bottomButtonPanel2, 1);

        buttonPanel.add(bottomButtonPanel, BorderLayout.SOUTH);
        buttonPanel.setVisible(true);
    }

    public static void main(String[] args) {
        new TouchToMouseApp();
    }

    public void setListeningToEvents(boolean listenToEvents) {
        if (touchHandler != null) {
            if (listenToEvents) {
                touchHandler.setListen(true);
            } else {
                touchHandler.setListen(false);
            }
            this.listeningToEvents = listenToEvents;
        }
    }

    public boolean isListeningToEvents() {
        return listeningToEvents;
    }
}
