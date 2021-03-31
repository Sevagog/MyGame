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
    private Bitmap player[] = new Bitmap[8];
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

        // Заполнение массива с персонажем
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < 4; i++) {
                player[j*4+i] = Bitmap.createBitmap(playerHelper, i*playerHelper.getWidth()/4, j*playerHelper.getHeight()/2, playerHelper.getWidth()/4, playerHelper.getHeight()/2);
            }
        }

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
        // Переменные для поля
        int rw , rh, rZeroX;
        // Переменные для регулеровок размера всего и вся
        int xRatio, yRatio, scaleP, scaleJ, scaleJ2;
        // Переменные для подсчета размера поля
        double help, help2;
        // Переменные для подсчета размера и положения джойстиков
        int jc, jc2;
        int jZeroX, jIndent;
        // Переменные для перемещения персонажа
        int playerPointX, playerPointY, movementX = 0, movementY = 0;
        double movementCo;
        // Переменная для отслеживания кадра персонажа
        int frame = 0;

        // Регулеровка размера
        scaleP = 3;
        scaleJ = 4;
        scaleJ2 = 3;
        jIndent = 20;
        // Регулеровка соотношения сторон
        xRatio = floor.getWidth() / player[0].getWidth();
        yRatio = floor.getHeight() / player[0].getHeight();

        backgroundPaint.setFilterBitmap(true);
        backgroundPaint.setDither(true);

        // Пригодиться для считывания положения персонажа
        playerPointX = 0;
        playerPointY = 0;
        movementCo = 0.35;

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

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
//                e.printStackTrace();
            }
            if (canvas != null) {
                try {
                    // Масштабирование
                    floor = Bitmap.createScaledBitmap(floor, rw, rh, true);
                    for (int i = 0; i < 8; i++) {
                        player[i] = Bitmap.createScaledBitmap(player[i], (rw / xRatio) / scaleP, (rh / yRatio) / scaleP, true);
                    }
                    joy = Bitmap.createScaledBitmap(joy, jc, jc, true);
                    joy2 = Bitmap.createScaledBitmap(joy2, jc2, jc2, true);

                    canvas.drawARGB(255,0,0,0);

                    canvas.drawBitmap(floor, rZeroX, 0, backgroundPaint);
                    canvas.drawBitmap(joy, jZeroX, rh - jc - jIndent, backgroundPaint);

                    // Проверка, попало ли нажатие на джойстик
                    if (towardPointX > jZeroX && towardPointX < jZeroX + jc && towardPointY > rh - jc - jIndent && towardPointY < rh - jIndent) {
                        canvas.drawBitmap(joy2, towardPointX - jc2 / 2, towardPointY - jc2 / 2, backgroundPaint);

                        movementX = (int)((towardPointX - (jZeroX + jc / 2)) * movementCo);
                        movementY = (int)((towardPointY - (rh - jc / 2 - jIndent)) * movementCo);

                        frame++;
                        frame = frame % 8;

                        playerPointX += movementX;
                        playerPointY += movementY;

                        if (playerPointX > rw - player[0].getWidth()){
                            playerPointX = rw - player[0].getWidth();
                        }
                        if (playerPointY > rh - player[0].getHeight()){
                            playerPointY = rh - player[0].getHeight();
                        }
                        if (playerPointX < 0){
                            playerPointX = 0;
                        }
                        if (playerPointY < 0){
                            playerPointY = 0;
                        }

                    } else {
                        canvas.drawBitmap(joy2, jZeroX + jc / scaleJ2, rh - jc + jc / scaleJ2 - jIndent, backgroundPaint);
                        frame = 0;
                    }

                    canvas.drawBitmap(player[frame], rZeroX + playerPointX, playerPointY, backgroundPaint);

                } finally {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }

            }
        }
    }
}