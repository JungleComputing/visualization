package util;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private static final int FS_RESOLUTION_WIDTH = 1280 * 2;// 3840;
    private static final int FS_RESOLUTION_HEIGHT = 720 * 2;// 2160;

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
        frame.setPreferredSize(new Dimension(625, 125));

        createInitialPanel();
        createButtonPanel();

        frame.add(initialPanel);
        // frame.add(buttonPanel);

        // Display the window.
        frame.pack();

        // center on screen
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);

        new Thread(new StartStopDeamon(this, 12346)).start();

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
            JOptionPane.showMessageDialog(frame, "IO Exception: " + e.getMessage());
        }
    }

    static void showButtons() {
        frame.setVisible(false);

        frame.remove(initialPanel);

        frame.add(buttonPanel);

        frame.setVisible(true);
    }

    static void createInitialPanel() {
        initialPanel.setLayout(new BorderLayout());

        final JTextField adressField = new JTextField("127.0.0.1"); // "145.100.39.11");
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
                connect(adressField.getText(), Integer.parseInt(portField.getText()));
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

        buttonPanel.add(topButtonPanel, BorderLayout.NORTH);

        JPanel middleButtonPanel = new JPanel();
        GridLayout bottomLayout = new GridLayout();
        bottomLayout.setRows(2);
        middleButtonPanel.setLayout(bottomLayout);

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

        middleButtonPanel.add(top_left_button);
        middleButtonPanel.add(top_middle_left_button);
        middleButtonPanel.add(top_middle_right_button);
        middleButtonPanel.add(top_right_button);
        middleButtonPanel.add(bottom_left_button);
        middleButtonPanel.add(bottom_middle_left_button);
        middleButtonPanel.add(bottom_middle_right_button);
        middleButtonPanel.add(bottom_right_button);

        buttonPanel.add(middleButtonPanel, BorderLayout.CENTER);

        JPanel bottomButtonPanel = new JPanel();
        bottomButtonPanel.setLayout(new GridLayout());

        JButton fr_left_4_button = new JButton("FR LEFT");
        fr_left_4_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                touchHandler.screenHeight = FS_RESOLUTION_HEIGHT;
                touchHandler.screenWidth = FS_RESOLUTION_WIDTH;
                touchHandler.setScreen(ScreenLocation.LEFT_4);
            }
        });
        bottomButtonPanel.add(fr_left_4_button);

        JButton fr_middle_4_button = new JButton("FR MIDDLE");
        fr_middle_4_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                touchHandler.screenHeight = FS_RESOLUTION_HEIGHT;
                touchHandler.screenWidth = FS_RESOLUTION_WIDTH;
                touchHandler.setScreen(ScreenLocation.MIDDLE_4);
            }
        });
        bottomButtonPanel.add(fr_middle_4_button);

        JButton fr_right_4_button = new JButton("FR RIGHT");
        fr_right_4_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                touchHandler.screenHeight = FS_RESOLUTION_HEIGHT;
                touchHandler.screenWidth = FS_RESOLUTION_WIDTH;
                touchHandler.setScreen(ScreenLocation.RIGHT_4);
            }
        });
        bottomButtonPanel.add(fr_right_4_button);

        buttonPanel.add(bottomButtonPanel, BorderLayout.SOUTH);
        buttonPanel.setVisible(true);
    }

    public static void main(String[] args) {
        new TouchToMouseApp();
    }

    public void setListeningToEvents(boolean listenToEvents) {
        if (touchHandler != null) {
            if (listeningToEvents) {
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
