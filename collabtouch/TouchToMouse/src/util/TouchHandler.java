package util;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.event.InputEvent;

public class TouchHandler implements TouchEventHandler {
    Robot robot;

    public enum ScreenLocation {
        LEFT_4, MIDDLE_4, RIGHT_4, TL, TML, TMR, TR, BL, BML, BMR, BR
    }

    public enum TouchState {
        NONE, SINGLE, RIGHT, DOUBLE, TRIPLE, OTHER, RELEASE
    }

    int screenWidth;
    int screenHeight;

    protected float initialResizeDist;
    protected int initialX, initialY;

    private ScreenLocation sl;
    private TouchState currentTouchState = TouchState.NONE;

    long eventStartTime;

    public TouchHandler(Robot robot, ScreenLocation sl) {
        this.robot = robot;
        this.sl = sl;

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();

        for (int i = 0; i < gs.length; i++) {
            DisplayMode dm = gs[i].getDisplayMode();
            screenWidth = dm.getWidth();
            screenHeight = dm.getHeight();
        }
    }

    @Override
    public void OnTouchPoints(double timestamp, TouchPoint[] points, int n) {
        long currentTime = System.currentTimeMillis();

        float xOffset = 0f;
        float yOffset = 0f;
        float xMultiplier = 1f;
        float yMultiplier = 1f;

        if (sl == ScreenLocation.LEFT_4) {
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

        if (n == 1) {
            int x = (int) (((points[0].tx + xOffset) * screenWidth) * xMultiplier);
            int y = (int) (((points[0].ty + yOffset) * screenHeight) * yMultiplier);

            if (x < 0 || x > screenWidth || y < 0 || y > screenHeight) {
                // DISCARD, NOT FOR MY SCREEN
            } else {
                // System.out.println(x + " : " + y);

                if (points[0].state == 0) {
                    if (isTouchState(TouchState.NONE) || isTouchState(TouchState.SINGLE)) {
                        setTouchState(TouchState.SINGLE);

                        eventStartTime = currentTime;

                        robot.mouseMove(x, y);
                        robot.mousePress(InputEvent.BUTTON1_MASK);
                    }
                } else if (points[0].state == 1) {
                    if (isTouchState(TouchState.SINGLE)) {
                        robot.mouseMove(x, y);
                    }
                } else if (points[0].state == 2) {
                    if (isTouchState(TouchState.SINGLE)) {
                        robot.mouseMove(x, y);
                        robot.mouseRelease(InputEvent.BUTTON1_MASK);
                        setTouchState(TouchState.NONE);
                    } else if (isTouchState(TouchState.RELEASE)) {
                        setTouchState(TouchState.NONE);
                    }
                }
            }
        } else if (n == 2) {
            robot.mouseRelease(InputEvent.BUTTON1_MASK);

            VecF2 v0 = new VecF2((points[0].tx - 0.5f), points[0].ty);
            VecF2 v1 = new VecF2((points[1].tx - 0.5f), points[1].ty);

            int x0 = (int) (((points[0].tx + xOffset) * screenWidth) * xMultiplier);
            int y0 = (int) (((points[0].ty + yOffset) * screenHeight) * yMultiplier);
            int x1 = (int) (((points[1].tx + xOffset) * screenWidth) * xMultiplier);
            int y1 = (int) (((points[1].ty + yOffset) * screenHeight) * yMultiplier);

            if (x0 < 0 || x0 > screenWidth || y0 < 0 || y0 > screenHeight || x1 < 0 || x1 > screenWidth || y1 < 0
                    || y1 > screenHeight) {
                // DISCARD, NOT FOR MY SCREEN
            } else {
                int x = (x0 + x1) / 2;
                int y = (y0 + y1) / 2;

                if (points[0].state == 0 || points[1].state == 0) {
                    if (isTouchState(TouchState.NONE) || isTouchState(TouchState.SINGLE)) {
                        setTouchState(TouchState.DOUBLE);
                        initialResizeDist = VectorFMath.length((v0.sub(v1)));
                        initialX = x;
                        initialY = y;
                    }
                } else if (points[1].state == 1 && points[0].state == 1) {
                    if (isTouchState(TouchState.DOUBLE)) {
                        float amountShorterThanInitial = VectorFMath.length((v0.sub(v1))) - initialResizeDist;

                        int notches = (int) (amountShorterThanInitial * 250);

                        robot.mouseWheel(-notches);

                        initialResizeDist = VectorFMath.length((v0.sub(v1)));
                    }
                } else if (points[0].state == 2 || points[1].state == 2) {
                    if (isTouchState(TouchState.DOUBLE) || isTouchState(TouchState.RELEASE)) {
                        if (points[0].state == 2 && points[1].state == 2) {
                            setTouchState(TouchState.NONE);
                        } else {
                            setTouchState(TouchState.RELEASE);
                        }
                    }
                }
            }
        } else if (n == 3) {
            int x0 = (int) (((points[0].tx + xOffset) * screenWidth) * xMultiplier);
            int y0 = (int) (((points[0].ty + yOffset) * screenHeight) * yMultiplier);
            int x1 = (int) (((points[1].tx + xOffset) * screenWidth) * xMultiplier);
            int y1 = (int) (((points[1].ty + yOffset) * screenHeight) * yMultiplier);
            int x2 = (int) (((points[2].tx + xOffset) * screenWidth) * xMultiplier);
            int y2 = (int) (((points[2].ty + yOffset) * screenHeight) * yMultiplier);

            if (x0 < 0 || x0 > screenWidth || y0 < 0 || y0 > screenHeight || x1 < 0 || x1 > screenWidth || y1 < 0
                    || y1 > screenHeight || x2 < 0 || x2 > screenWidth || y2 < 0 || y2 > screenHeight) {
                // DISCARD, NOT FOR MY SCREEN
            } else {
                if (points[0].state == 0 || points[1].state == 0 || points[2].state == 0) {
                    if (isTouchState(TouchState.NONE) || isTouchState(TouchState.SINGLE)
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
        this.sl = sl;
    }
}
