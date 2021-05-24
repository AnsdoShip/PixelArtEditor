package com.ansdoship.pixelarteditor.util;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.Stack;

public final class DrawUtils {

    // Scan line seed fill
    public static void fill(Bitmap bitmap, int x, int y, int newColor) {
        Stack<Point> pointStack = new Stack<>();
        Point seed;
        if (bitmap.getPixel(x, y) != newColor) {
            pointStack.push(new Point(x, y));
        }
        while (true) {
            if (!pointStack.isEmpty()) {
                seed = pointStack.pop();
                int oldColor = bitmap.getPixel(seed.x, seed.y);
                int leftX = seed.x;
                int rightX = seed.x;
                int boundaryLeft = -1;
                int boundaryRight = -1;
                if (oldColor != newColor) {
                    while(true) {
                        if (leftX - 1 >= 0) {
                            if(bitmap.getPixel(leftX - 1, seed.y) != oldColor) {
                                boundaryLeft = leftX;
                                break;
                            }
                            leftX--;
                        } else {
                            if(leftX == 0) {
                                boundaryLeft = leftX;
                            }
                            break;
                        }
                    }
                    while(true) {
                        if (rightX + 1 < bitmap.getWidth()) {
                            if (bitmap.getPixel(rightX + 1, seed.y) != oldColor) {
                                boundaryRight = rightX;
                                break;
                            }
                            rightX++;
                        } else {
                            if (rightX + 1 == bitmap.getWidth()) {
                                boundaryRight = rightX;
                            }
                            break;
                        }
                    }
                    if (boundaryLeft != -1 && boundaryRight != -1) {
                        for (int detectX = boundaryLeft; detectX <= boundaryRight; detectX ++) {
                            if (seed.y + 1 < bitmap.getHeight()) {
                                if (bitmap.getPixel(detectX, seed.y + 1) == oldColor) {
                                    if (detectX < boundaryRight) {
                                        if (bitmap.getPixel(detectX + 1, seed.y + 1) != oldColor) {
                                            pointStack.push(new Point(detectX, seed.y + 1));
                                        }
                                    }
                                    else {
                                        pointStack.push(new Point(detectX, seed.y + 1));
                                    }
                                }
                            }
                            if (seed.y - 1 >= 0) {
                                if (bitmap.getPixel(detectX, seed.y - 1) == oldColor) {
                                    if (detectX < boundaryRight) {
                                        if (bitmap.getPixel(detectX + 1, seed.y - 1) != oldColor) {
                                            pointStack.push(new Point(detectX, seed.y - 1));
                                        }
                                    }
                                    else {
                                        pointStack.push(new Point(detectX, seed.y - 1));
                                    }
                                }
                            }
                        }
                    }
                }
                fillLine(bitmap, seed.x, seed.y, newColor);
            }
            else {
                break;
            }
        }
    }

    private static void fillLine(Bitmap bitmap, int x, int y, int newColor) {
        if (x + 1 < bitmap.getWidth()) {
            if (bitmap.getPixel(x + 1, y) == bitmap.getPixel(x, y)) {
                fillLineRight(bitmap, x + 1, y, newColor);
            }
        }
        fillLineLeft(bitmap, x, y, newColor);
    }

    private static void fillLineLeft(Bitmap bitmap, int x, int y, int newColor) {
        int oldColor = bitmap.getPixel(x, y);
        if (oldColor == newColor) {
            return;
        }
        while(true) {
            if(x >= 0) {
                if(bitmap.getPixel(x, y) == oldColor) {
                    bitmap.setPixel(x, y, newColor);
                    x --;
                }
                else {
                    break;
                }
            }
            else {
                break;
            }
        }
    }

    private static void fillLineRight(Bitmap bitmap, int x, int y, int newColor) {
        int oldColor = bitmap.getPixel(x, y);
        if (oldColor == newColor) {
            return;
        }
        while(true) {
            if(x < bitmap.getWidth()) {
                if(bitmap.getPixel(x, y) == oldColor) {
                    bitmap.setPixel(x, y, newColor);
                    x ++;
                }
                else {
                    break;
                }
            }
            else {
                break;
            }
        }
    }

}
