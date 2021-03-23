package ru.sindeev.mygame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;


public class GameThread extends Thread {

    // Что это?
    private SurfaceHolder surfaceHolder;

    // Что это?
    private volatile boolean running = true; // флаг для остановки потока
    private Paint backgroundPaint = new Paint();

    // Картинки вставлять через это?
    private Bitmap floor;
    private Bitmap playerHelper;
    private Bitmap player;
    private Bitmap joy;
    private Bitmap joy2;

    private int towardPointX;
    private int towardPointY;

    // Что это?
    public GameThread(Context context, SurfaceHolder surfaceHolder) {

        // Сюда все картинки?
        floor = BitmapFactory.decodeResource(context.getResources(), R.drawable.floor);
        joy = BitmapFactory.decodeResource(context.getResources(), R.drawable.joystick);
        joy2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.joystick2);
        playerHelper = BitmapFactory.decodeResource(context.getResources(), R.drawable.fur_player);
        player = Bitmap.createBitmap(playerHelper, 0, 0, playerHelper.getWidth()/4, playerHelper.getHeight()/2);

        this.surfaceHolder = surfaceHolder;
    }

    // Что это?
    public void requestStop() {
        running = false;
    }

    public void setTowardPoint(int x, int y) {
        towardPointX = x;
        towardPointY = y;
    }

    // Тут вся логика
    @Override
    public void run() {
        int rw , rh, scaleP, scaleJ, scaleJ2, xRatio, yRatio, rZeroX, jZeroX, playerPointX, playerPointY, jc, jc2, jIndent;
        double help, help2;

        // Регулеровка размера
        scaleP = 3;
        scaleJ = 5;
        scaleJ2 = 3;
        jIndent = 20;

        // Регулеровка соотношения сторон
        xRatio = floor.getWidth() / player.getWidth();
        yRatio = floor.getHeight() / player.getHeight();

        backgroundPaint.setFilterBitmap(true);
        backgroundPaint.setDither(true);

        // Пригодиться для считывания положения персонажа
        playerPointX = 250;
        playerPointY = 250;

        while (running) {

            Canvas canvas = surfaceHolder.lockCanvas();

            rh = canvas.getHeight();
            help = (float)(floor.getWidth()) / (float)(floor.getHeight());
            help2 = canvas.getHeight() * help;
            rw = (int)help2;

            jc = rh / scaleJ;
            jc2 = jc / scaleJ2;

            rZeroX = (canvas.getWidth() - rw) / 2;
            jZeroX = rZeroX + jIndent;

            if (canvas != null) {
                try {
                    // Масштабирование
                    floor = Bitmap.createScaledBitmap(floor, rw, rh, true);
                    player = Bitmap.createScaledBitmap(player, (rw / xRatio) / scaleP, (rh / yRatio) / scaleP, true);
                    joy = Bitmap.createScaledBitmap(joy, jc, jc, true);
                    joy2 = Bitmap.createScaledBitmap(joy2, jc2, jc2, true);

                    canvas.drawARGB(255,0,0,0);
                    canvas.drawBitmap(floor, rZeroX, 0, backgroundPaint);
                    canvas.drawBitmap(joy, jZeroX, rh - jc - jIndent, backgroundPaint);

                    if (towardPointX > jZeroX && towardPointX < jZeroX + jc && towardPointY > rh - jc - jIndent && towardPointY < rh - jIndent) {
                        canvas.drawBitmap(joy2, towardPointX, towardPointY, backgroundPaint);
                    } else {
                        canvas.drawBitmap(joy2, jZeroX + jc / scaleJ2, rh - jc + jc / scaleJ2 - jIndent, backgroundPaint);
                    }

                    canvas.drawBitmap(player, rZeroX + playerPointX, playerPointY, backgroundPaint);

                } finally {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }

            }
        }
    }
}