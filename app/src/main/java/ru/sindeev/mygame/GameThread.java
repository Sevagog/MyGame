package ru.sindeev.mygame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
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
    private Bitmap l_hit[][] = new Bitmap[4][2];
    private Bitmap l_hit_helper;
    private Bitmap l_hit_helper2;
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
        l_hit_helper = BitmapFactory.decodeResource(context.getResources(), R.drawable.l_hit);
        l_hit_helper2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.l_hit_left_side);
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

        // Удар левой ногой
        for (int i = 0; i < 4; i++){
            l_hit[i][0] = Bitmap.createBitmap(l_hit_helper, i*l_hit_helper.getWidth()/4, 0, l_hit_helper.getWidth()/4, l_hit_helper.getHeight());
        }
        for (int i = 0; i < 4; i++){
            l_hit[i][1] = Bitmap.createBitmap(l_hit_helper2, i*l_hit_helper2.getWidth()/4, 0, l_hit_helper2.getWidth()/4, l_hit_helper2.getHeight());
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
        int rw = 0, rh = 0, rZeroX = 0;
        // Переменные для регулеровок размера всего и вся
        int xRatio, yRatio, scaleP, scaleJ, scaleJ2, scaleJ3, xRatioH, yRatioH;
        // Переменные для подсчета размера поля
        double help, help2;
        // Переменные для подсчета размера и положения джойстиков
        int jc = 0, jc2 = 0, jc3 = 0;
        int jZeroX = 0, jIndent;
        // Переменные для перемещения персонажа
        int playerPointX, playerPointY, movementX = 0, movementY = 0;
        double movementCo;
        // Переменные для отслеживания кадра персонажа
        int frame = 0, side = 0, enemyFrame = 0, isHit = 0, hitHelper = 0, noHitTimer = 0;
        // Переменные для отслеживания позиции врага
        int enemyX = 1000, enemyY = 100;

        // Регулеровка размера
        scaleP = 1;
        scaleJ = 4;
        scaleJ2 = 3;
        scaleJ3 = 2;
        jIndent = 50;
        // Регулеровка соотношения сторон
        xRatio = floor.getWidth() / player[0][0].getWidth();
        yRatio = floor.getHeight() / player[0][0].getHeight();

        xRatioH = floor.getWidth() / l_hit[0][0].getWidth();
        yRatioH = floor.getWidth() / l_hit[0][0].getHeight();

        // Создание "кисточки"
        backgroundPaint.setFilterBitmap(true);
        backgroundPaint.setDither(true);

        // Пригодиться для считывания положения персонажа, для регулеровки скорости
        playerPointX = 0;
        playerPointY = 0;
        movementCo = 0.15;

        while (running) {

            Canvas canvas = surfaceHolder.lockCanvas();

            // Делаем все 1 раз, чтоб каждый раз не вычеслять
            if (rh == 0) {
                // Настройка экрана
                rh = canvas.getHeight();
                help = (float) (floor.getWidth()) / (float) (floor.getHeight());
                help2 = canvas.getHeight() * help;
                rw = (int) help2;

                // Размеры джойстиков
                jc = rh / scaleJ;
                jc2 = jc / scaleJ2;
                jc3 = jc / scaleJ3;

                // Создание черных полос по краям
                rZeroX = (canvas.getWidth() - rw) / 2;
                jZeroX = rZeroX + jIndent;
            }

            // Замедление работы
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {

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
                    for (int j = 0; j < 2; j++) {
                        for (int i = 0; i < 4; i++) {
                            l_hit[i][j] = Bitmap.createScaledBitmap(l_hit[i][j], (rw / xRatioH) / scaleP, (rw / yRatioH) / scaleP, true);
                        }
                    }
                    l_b = Bitmap.createScaledBitmap(l_b, jc3, jc3, true);
                    r_b = Bitmap.createScaledBitmap(r_b, jc3, jc3, true);
                    joy = Bitmap.createScaledBitmap(joy, jc, jc, true);
                    joy2 = Bitmap.createScaledBitmap(joy2, jc2, jc2, true);

                    // Рисуем то, что не собираемся перерисовывать (фон, кнопки)
                    canvas.drawARGB(255, 0, 0, 0);
                    canvas.drawBitmap(floor, rZeroX, 0, backgroundPaint);
                    canvas.drawBitmap(joy, jZeroX, rh - jc - jIndent, backgroundPaint);
                    canvas.drawBitmap(l_b, rw + rZeroX - jIndent - l_b.getWidth() * 2, rh - l_b.getHeight() - jIndent, backgroundPaint);
                    canvas.drawBitmap(r_b, rw + rZeroX - jIndent - r_b.getWidth(), rh - jIndent - r_b.getHeight() * 2, backgroundPaint);

                    // Увеличиваем кадр противника
                    enemyFrame++;
                    if (enemyFrame == Integer.MAX_VALUE){
                        enemyFrame = 1;
                    }

                    // Проверяем, попало ли нажатие на кнопку удара, идет ли таймер
                    if (noHitTimer == 0 && towardPointX > rw + rZeroX - jIndent - l_b.getWidth() * 2 && towardPointY > rh - l_b.getHeight() - jIndent && towardPointX < rw + rZeroX - jIndent - l_b.getWidth() && towardPointY < rh - jIndent){
                        hitHelper = 0;
                        isHit = 1;
                        noHitTimer = 10;
                    }

                    // Если мы не бьем
                    if (isHit == 0) {

                        // Уменьшаем таймер удара, если он не равен 0
                        if (noHitTimer > 0) {
                            noHitTimer--;
                        }

                        // Проверка, попало ли нажатие на джойстик
                        if (towardPointX > 0 && towardPointX < rZeroX + rw / 2 && towardPointY > 0 && towardPointY < rh) {

                            // ТУТ НУЖНО КАК-ТО УБРАТЬ УГОЛ У ДЖОЙСТИКА

                            // Это переменные для положения джойстика
                            jX = towardPointX;
                            jY = towardPointY;

                            // Смотрим, не выходят ли за пределы
                            if (jX < jZeroX) {
                                jX = jZeroX;
                            } else if (jX > jZeroX + jc) {
                                jX = jZeroX + jc;
                            }
                            if (jY < rh - jc - jIndent) {
                                jY = rh - jc - jIndent;
                            } else if (jY > rh - jIndent) {
                                jY = rh - jIndent;
                            }

                            // Рисуем джойстик, настраиваем переменные для смещения персонажа
                            canvas.drawBitmap(joy2, jX - jc2 / 2, jY - jc2 / 2, backgroundPaint);
                            movementX = (int) ((jX - (jZeroX + jc / 2)) * movementCo);
                            movementY = (int) ((jY - (rh - jc / 2 - jIndent)) * movementCo);

                            // Кадр персонажа
                            frame++;
                            frame = frame % 8;

                            // Двигаем персонажа
                            playerPointX += movementX;
                            playerPointY += movementY;

                            // Смотрим, в какую сторону идет персонаж
                            if (movementX > 0) {
                                side = 0;
                            } else if (movementX < 0) {
                                side = 1;
                            }

                            // Если персонаж ушел за границу - возвращаем
                            if (playerPointX > rw - player[0][0].getWidth()) {
                                playerPointX = rw - player[0][0].getWidth();
                            }
                            if (playerPointY > rh - player[0][0].getHeight()) {
                                playerPointY = rh - player[0][0].getHeight();
                            }
                            if (playerPointX < 0) {
                                playerPointX = 0;
                            }
                            if (playerPointY < 0) {
                                playerPointY = 0;
                            }

                        } else {
                            // Если не трогали джойстик - рисуем его в центре, обнуляем кадр движения
                            canvas.drawBitmap(joy2, jZeroX + jc / scaleJ2, rh - jc + jc / scaleJ2 - jIndent, backgroundPaint);
                            frame = 0;
                        }

                        // Рисуем персонажа
                        canvas.drawBitmap(player[frame][side], rZeroX + playerPointX, playerPointY, backgroundPaint);

                        // А если мы бьем
                    } else if (isHit == 1){
                        // Рисуем джойстик в центре
                        canvas.drawBitmap(joy2, jZeroX + jc / scaleJ2, rh - jc + jc / scaleJ2 - jIndent, backgroundPaint);

                        // Сначала идем в 1 сторону, потом в другую по кадрам. Я не знаю, как это можно реализовать лучше
                        if (hitHelper < 4){
                            frame = hitHelper;
                        } else {
                            frame = 6 - hitHelper;
                        }

                        // Почему-то без этого side обнуляется, это нужно, чтоб бить в нужную сторону
                        if (movementX > 0) {
                            side = 0;
                        } else if (movementX < 0) {
                            side = 1;
                        }

                        // Во время удара наш персонаж летит, тут его траектория
                        if (side == 0) {
                            switch (hitHelper) {
                                case 1:
                                    playerPointX -= 10;
                                    playerPointY += 10;
                                    break;
                                case 2:
                                    playerPointX += 15;
                                    playerPointY -= 20;
                                    break;
                                case 3:
                                    playerPointX += 50;
                                    playerPointY -= 5;
                                    break;
                                case 4:
                                    playerPointX += 15;
                                    playerPointY += 15;
                                    break;
                            }
                        } else {
                            switch (hitHelper) {
                                case 1:
                                    playerPointX += 10;
                                    playerPointY += 10;
                                    break;
                                case 2:
                                    playerPointX -= 15;
                                    playerPointY -= 20;
                                    break;
                                case 3:
                                    playerPointX -= 50;
                                    playerPointY -= 5;
                                    break;
                                case 4:
                                    playerPointX -= 15;
                                    playerPointY += 15;
                                    break;
                            }
                        }

                        // Рисуем персонажа
                        canvas.drawBitmap(l_hit[frame][side], rZeroX + playerPointX, playerPointY, backgroundPaint);

                        // Выходим из удара
                        if (hitHelper == 6){
                            isHit = 0;
                            towardPointY = 0;
                            towardPointX = 0;
                        }

                        hitHelper++;
                    }

                    // Рисуем врага, тут что-то надо поменять, чтоб враг не всегда был сверху нашего персонажа
                    canvas.drawBitmap(enemy[enemyFrame % 9], enemyX, enemyY, backgroundPaint);

                } finally{
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}