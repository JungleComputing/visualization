package util;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.ArrayList;

public class TouchHandler implements TouchEventHandler {
    Robot robot;

    public enum ScreenLocation {
        LEFT_4, MIDDLE_4, RIGHT_4, TL, TML, TMR, TR, BL, BML, BMR, BR, ALL
    }

    public enum TouchState {
        NONE, SINGLE, RIGHT, DOUBLE, TRIPLE, OTHER, RELEASE
    }

    int screenWidth;
    int screenHeight;

    protected float initialResizeDist;
    protected int initialX, initialY;

    private TouchState currentTouchState = TouchState.NONE;

    long eventStartTime;

    float xOffset = 0f;
    float yOffset = 0f;
    float xMultiplier = 1f;
    float yMultiplier = 1f;

    private boolean listening = true;

    public TouchHandler(Robot robot, ScreenLocation sl) {
        this.robot = robot;
        setScreen(sl);

        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();

        for (int i = 0; i < gs.length; i++) {
            DisplayMode dm = gs[i].getDisplayMode();
            screenWidth = dm.getWidth();
            screenHeight = dm.getHeight();

            System.out.println("W: " + screenWidth);
            System.out.println("H: " + screenHeight);
        }
    }

    @Override
    public void OnTouchPoints(double timestamp, TouchPoint[] points, int n) {
        if (listening) {
            long currentTime = System.currentTimeMillis();

            ArrayList<TouchPoint> newPoints = new ArrayList<TouchPoint>();

            for (int i = 0; i < n; i++) {
                int x = (int) (((points[i].tx + xOffset) * screenWidth) * xMultiplier);
                int y = (int) (((points[i].ty + yOffset) * screenHeight) * yMultiplier);

                if (x >= 0 && x < screenWidth && y >= 0 && y < screenHeight) {
                    newPoints.add(points[i]);
                }
            }

            if (newPoints.size() == 1) {
                TouchPoint t0 = newPoints.get(0);

                int x = (int) (((t0.tx + xOffset) * screenWidth) * xMultiplier);
                int y = (int) (((t0.ty + yOffset) * screenHeight) * yMultiplier);

                if (t0.state == 0) {
                    if (isTouchState(TouchState.NONE)
                            || isTouchState(TouchState.SINGLE)) {
                        setTouchState(TouchState.SINGLE);

                        eventStartTime = currentTime;

                        robot.mouseMove(x, y);
                        robot.mousePress(InputEvent.BUTTON1_MASK);
                    }
                } else if (t0.state == 1) {
                    if (isTouchState(TouchState.SINGLE)) {
                        robot.mouseMove(x, y);
                    }
                } else if (t0.state == 2) {
                    if (isTouchState(TouchState.SINGLE)) {
                        robot.mouseMove(x, y);
                        robot.mouseRelease(InputEvent.BUTTON1_MASK);
                        setTouchState(TouchState.NONE);
                    } else if (isTouchState(TouchState.RELEASE)) {
                        setTouchState(TouchState.NONE);
                    }
                }
            } else if (newPoints.size() == 2) {
                robot.mouseRelease(InputEvent.BUTTON1_MASK);

                TouchPoint t0 = newPoints.get(0);
                TouchPoint t1 = newPoints.get(1);

                int x0 = (int) (((t0.tx + xOffset) * screenWidth) * xMultiplier);
                int y0 = (int) (((t0.ty + yOffset) * screenHeight) * yMultiplier);
                int x1 = (int) (((t1.tx + xOffset) * screenWidth) * xMultiplier);
                int y1 = (int) (((t1.ty + yOffset) * screenHeight) * yMultiplier);

                VecF2 v0 = new VecF2((t0.tx - 0.5f), t0.ty);
                VecF2 v1 = new VecF2((t1.tx - 0.5f), t1.ty);

                int x = (x0 + x1) / 2;
                int y = (y0 + y1) / 2;

                if (t0.state == 0 || t1.state == 0) {
                    if (isTouchState(TouchState.NONE)
                            || isTouchState(TouchState.SINGLE)) {
                        setTouchState(TouchState.DOUBLE);
                        initialResizeDist = VectorFMath.length((v0.sub(v1)));
                        initialX = x;
                        initialY = y;
                    }
                } else if (t1.state == 1 && t0.state == 1) {
                    if (isTouchState(TouchState.DOUBLE)) {
                        float amountShorterThanInitial = VectorFMath.length((v0
                                .sub(v1))) - initialResizeDist;

                        int notches = (int) (amountShorterThanInitial * 250);

                        robot.mouseWheel(-notches);

                        initialResizeDist = VectorFMath.length((v0.sub(v1)));
                    }
                } else if (t0.state == 2 || t1.state == 2) {
                    if (isTouchState(TouchState.DOUBLE)
                            || isTouchState(TouchState.RELEASE)) {
                        if (t0.state == 2 && t1.state == 2) {
                            setTouchState(TouchState.NONE);
                        } else {
                            setTouchState(TouchState.RELEASE);
                        }
                    }
                }
            } else if (newPoints.size() == 3) {
                TouchPoint t0 = newPoints.get(0);
                TouchPoint t1 = newPoints.get(1);
                TouchPoint t2 = newPoints.get(2);

                if (t0.state == 0 || t1.state == 0 || t2.state == 0) {
                    if (isTouchState(TouchState.NONE)
                            || isTouchState(TouchState.SINGLE)
                            || isTouchState(TouchState.DOUBLE)) {
                        setTouchState(TouchState.TRIPLE);
                        robot.mousePress(InputEvent.BUTTON3_MASK);
                        robot.mouseRelease(InputEvent.BUTTON3_MASK);
                    }
                } else {
                    setTouchState(TouchState.RELEASE);
                }
            }
        }
    }

    private synchronized void setTouchState(TouchState newState) {
        currentTouchState = newState;
    }

    private synchronized boolean isTouchState(TouchState state) {
        if (currentTouchState == state) {
            return true;
        }
        return false;
    }

    public void setScreen(ScreenLocation sl) {
        if (sl == ScreenLocation.ALL) {
            xOffset = 0f;
            yOffset = 0f;
            xMultiplier = 1f;
            yMultiplier = 1f;
        } else if (sl == ScreenLocation.LEFT_4) {
            xOffset = 0f;
            yOffset = 0f;
            xMultiplier = 2f;
            yMultiplier = 1f;
        } else if (sl == ScreenLocation.MIDDLE_4) {
            xOffset = -.25f;
            yOffset = 0f;
            xMultiplier = 2f;
            yMultiplier = 1f;
        } else if (sl == ScreenLocation.RIGHT_4) {
            xOffset = -.5f;
            yOffset = 0f;
            xMultiplier = 2f;
            yMultiplier = 1f;
        } else if (sl == ScreenLocation.TL) {
            xOffset = 0f;
            yOffset = 0f;
            xMultiplier = 4f;
            yMultiplier = 2f;
        } else if (sl == ScreenLocation.TML) {
            xOffset = -.25f;
            yOffset = 0f;
            xMultiplier = 4f;
            yMultiplier = 2f;
        } else if (sl == ScreenLocation.TMR) {
            xOffset = -.5f;
            yOffset = 0f;
            xMultiplier = 4f;
            yMultiplier = 2f;
        } else if (sl == ScreenLocation.TR) {
            xOffset = -.75f;
            yOffset = 0f;
            xMultiplier = 4f;
            yMultiplier = 2f;
        } else if (sl == ScreenLocation.BL) {
            xOffset = 0f;
            yOffset = -.5f;
            xMultiplier = 4f;
            yMultiplier = 2f;
        } else if (sl == ScreenLocation.BML) {
            xOffset = -.25f;
            yOffset = -.5f;
            xMultiplier = 4f;
            yMultiplier = 2f;
        } else if (sl == ScreenLocation.BMR) {
            xOffset = -.5f;
            yOffset = -.5f;
            xMultiplier = 4f;
            yMultiplier = 2f;
        } else if (sl == ScreenLocation.BR) {
            xOffset = -.75f;
            yOffset = -.5f;
            xMultiplier = 4f;
            yMultiplier = 2f;
        }

    }

    /**
     * @param listening
     */
    public void setListen(boolean listening) {
        if (listening) {
            System.out.println("starting touch!");
        } else {
            System.out.println("stopping touch!");
        }

        this.listening = listening;
    }
}
