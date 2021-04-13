package ru.sindeev.mygame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;


public class GameThread extends Thread {

    private SurfaceHolder surfaceHolder;

    private volatile boolean running = true; // флаг для остановки потока
    private Paint backgroundPaint = new Paint();

    // Картинки вставлять через это
    private Bitmap floor;
    private Bitmap enemy[] = new Bitmap[9];
    private Bitmap enemyHelper;
    private Bitmap playerHelper;
    private Bitmap playerHelper2;
    private Bitmap player[][] = new Bitmap[8][2];
    private Bitmap joy;
    private Bitmap joy2;
    private Bitmap l_b;
    private Bitmap r_b;

    private int towardPointX;
    private int towardPointY;

    public GameThread(Context context, SurfaceHolder surfaceHolder) {

        // Сюда все картинки
        floor = BitmapFactory.decodeResource(context.getResources(), R.drawable.floor);
        l_b = BitmapFactory.decodeResource(context.getResources(), R.drawable.l);
        r_b = BitmapFactory.decodeResource(context.getResources(), R.drawable.r);
        joy = BitmapFactory.decodeResource(context.getResources(), R.drawable.joystick);
        joy2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.joystick2);
        playerHelper = BitmapFactory.decodeResource(context.getResources(), R.drawable.fur_player);
        enemyHelper = BitmapFactory.decodeResource(context.getResources(), R.drawable.first_enemy);
        playerHelper2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.fur_player_left_side);

        // Заполнение массива с персонажем
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < 4; i++) {
                player[j*4+i][0] = Bitmap.createBitmap(playerHelper, i*playerHelper.getWidth()/4, j*playerHelper.getHeight()/2, playerHelper.getWidth()/4, playerHelper.getHeight()/2);
            }
        }
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < 4; i++) {
                player[j*4+i][1] = Bitmap.createBitmap(playerHelper2, i*playerHelper2.getWidth()/4, j*playerHelper2.getHeight()/2, playerHelper2.getWidth()/4, playerHelper2.getHeight()/2);
            }
        }

        // Заполнение массива с противником
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < 5; i++) {
                if (j == 1 && i == 4){
                    continue;
                }
                enemy[j*5+i] = Bitmap.createBitmap(enemyHelper, i*enemyHelper.getWidth()/5, j*enemyHelper.getHeight()/2, enemyHelper.getWidth()/5, enemyHelper.getHeight()/2);
            }
        }

        this.surfaceHolder = surfaceHolder;
    }

    public void requestStop() {
        running = false;
    }

    // Переменные для получения координат нажатия
    public void setTowardPoint(int x, int y) {
        towardPointX = x;
        towardPointY = y;
    }

    // Тут вся логика
    @Override
    public void run() {
        // Переменные для положения джойстика
        int jX, jY;
        // Переменные для поля
        int rw , rh, rZeroX;
        // Переменные для регулеровок размера всего и вся
        int xRatio, yRatio, scaleP, scaleJ, scaleJ2, scaleJ3;
        // Переменные для подсчета размера поля
        double help, help2;
        // Переменные для подсчета размера и положения джойстиков
        int jc, jc2, jc3;
        int jZeroX, jIndent;
        // Переменные для перемещения персонажа
        int playerPointX, playerPointY, movementX = 0, movementY = 0;
        double movementCo;
        // Переменная для отслеживания кадра персонажа
        int frame = 0, side = 0, enemyFrame = 0;

        // Регулеровка размера
        scaleP = 3;
        scaleJ = 4;
        scaleJ2 = 3;
        scaleJ3 = 2;
        jIndent = 20;
        // Регулеровка соотношения сторон
        xRatio = floor.getWidth() / player[0][0].getWidth();
        yRatio = floor.getHeight() / player[0][0].getHeight();

        backgroundPaint.setFilterBitmap(true);
        backgroundPaint.setDither(true);

        // Пригодиться для считывания положения персонажа, для регулеровки скорости
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
            jc3 = jc / scaleJ3;

            rZeroX = (canvas.getWidth() - rw) / 2;
            jZeroX = rZeroX + jIndent;

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
//                e.printStackTrace();
            }

            // САМА ЛОГИКА ДАЛЬШЕ

            if (canvas != null) {
                try {
                    // Масштабирование
                    floor = Bitmap.createScaledBitmap(floor, rw, rh, true);
                    for (int j = 0; j < 2; j++) {
                        for (int i = 0; i < 8; i++) {
                            player[i][j] = Bitmap.createScaledBitmap(player[i][j], (rw / xRatio) / scaleP, (rh / yRatio) / scaleP, true);
                        }
                    }
                    for (int i = 0; i < 9; i++) {
                        enemy[i] = Bitmap.createScaledBitmap(enemy[i], (rw / xRatio) / scaleP, (rh / yRatio) / scaleP, true);
                    }
                    l_b = Bitmap.createScaledBitmap(l_b, jc3, jc3, true);
                    r_b = Bitmap.createScaledBitmap(r_b, jc3, jc3, true);
                    joy = Bitmap.createScaledBitmap(joy, jc, jc, true);
                    joy2 = Bitmap.createScaledBitmap(joy2, jc2, jc2, true);

                    canvas.drawARGB(255,0,0,0);

                    canvas.drawBitmap(floor, rZeroX, 0, backgroundPaint);
                    canvas.drawBitmap(joy, jZeroX, rh - jc - jIndent, backgroundPaint);

                    canvas.drawBitmap(l_b, rw  + rZeroX - jIndent - l_b.getWidth()*2, rh - l_b.getHeight() - jIndent, backgroundPaint);
                    canvas.drawBitmap(r_b, rw  + rZeroX - jIndent - r_b.getWidth(), rh - jIndent - r_b.getHeight()*2, backgroundPaint);

                    // Проверка, попало ли нажатие на джойстик
                    if (towardPointX > 0 && towardPointX < rZeroX + rw / 2 && towardPointY > 0 && towardPointY < rh) {

                        jX = towardPointX;
                        jY = towardPointY;

                        if (jX < jZeroX){
                            jX = jZeroX;
                        } else if (jX > jZeroX + jc){
                            jX = jZeroX + jc;
                        }
                        if (jY < rh - jc - jIndent){
                            jY = rh - jc - jIndent;
                        } else if (jY > rh - jIndent){
                            jY = rh - jIndent;
                        }

                        canvas.drawBitmap(joy2, jX - jc2 / 2, jY - jc2 / 2, backgroundPaint);
                        movementX = (int)((jX - (jZeroX + jc / 2)) * movementCo);
                        movementY = (int)((jY - (rh - jc / 2 - jIndent)) * movementCo);

                        frame++;
                        frame = frame % 8;

                        playerPointX += movementX;
                        playerPointY += movementY;

                        if (movementX > 0){
                            side = 0;
                        } else if (movementX < 0){
                            side = 1;
                        }

                        if (playerPointX > rw - player[0][0].getWidth()){
                            playerPointX = rw - player[0][0].getWidth();
                        }
                        if (playerPointY > rh - player[0][0].getHeight()){
                            playerPointY = rh - player[0][0].getHeight();
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

                    enemyFrame++;
                    enemyFrame = enemyFrame % 9;

                    canvas.drawBitmap(enemy[enemyFrame],1000,100, backgroundPaint);
                    canvas.drawBitmap(player[frame][side], rZeroX + playerPointX, playerPointY, backgroundPaint);

                } finally {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }

            }
        }
    }
}