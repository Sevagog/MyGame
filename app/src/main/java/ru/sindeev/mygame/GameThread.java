package ru.sindeev.mygame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;


public class GameThread extends Thread {

    private int xDys, yDis;

    // Что это?
    private SurfaceHolder surfaceHolder;

    // Что это?
    private volatile boolean running = true; // флаг для остановки потока
    private Paint backgroundPaint = new Paint();

    // Картинки вставлять через это?
    private Bitmap floor;
    private Bitmap playerHelper;
    private Bitmap player;


    // Переменные для положения персонажа
    private int playerPointX;
    private int playerPointY;

    // Что это?
    public GameThread(Context context, SurfaceHolder surfaceHolder) {

        // Сюда все картинки?
        floor = BitmapFactory.decodeResource(context.getResources(), R.drawable.floor);
        playerHelper = BitmapFactory.decodeResource(context.getResources(), R.drawable.fur_player);
        player = Bitmap.createBitmap(playerHelper, 0, 0, playerHelper.getWidth()/4, playerHelper.getHeight()/2);

        this.surfaceHolder = surfaceHolder;
    }

    // Что это?
    public void requestStop() {
        running = false;
    }

    // Тут вся логика
    @Override
    public void run() {
        int rw , rh, scale, xRatio, yRatio;

        // Регулеровка размера персонажа
//        scale = 2;

        // Регулеровка соотношения сторон
//        xRatio = floor.getWidth() / player.getWidth();
//        yRatio = floor.getHeight() / player.getHeight();

        backgroundPaint.setFilterBitmap(true);
        backgroundPaint.setDither(true);

        // Пригодиться для считывания положения персонажа
        playerPointX = 250;
        playerPointY = 250;

        while (running) {

            Canvas canvas = surfaceHolder.lockCanvas();

            rh = canvas.getHeight();
            rw = canvas.getHeight() * (floor.getHeight() / floor.getWidth());

            if (canvas != null) {
                try {
                    // Масштабирование
                    floor = Bitmap.createScaledBitmap(floor, rw, rh, true);
//                    player = Bitmap.createScaledBitmap(player, (rw / xRatio) / scale, (rh / yRatio) / scale, true);
                    canvas.drawBitmap(floor, 0, 0, backgroundPaint);
//                    canvas.drawBitmap(player, playerPointX, playerPointY, backgroundPaint);

                } finally {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }

            }
        }
    }
}