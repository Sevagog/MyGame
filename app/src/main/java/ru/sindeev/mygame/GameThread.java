package ru.sindeev.mygame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.Random;

public class GameThread extends Thread {

    private SurfaceHolder surfaceHolder;
    private Canvas canvas;

    private volatile boolean running = true; // флаг для остановки потока
    private Paint backgroundPaint = new Paint();
    private boolean AreYouDead = false;
    private int deathAlpha = 0;

    // Для врагов
    private Enemy[] enemies = new Enemy[5];

    // Переменные для поля
    private int rZeroX = 0, rw = 0, rh = 0;
    private int  enemySide = 0, playerPointX = 0, playerPointY = 0;

    // Картинки вставлять через это
    private Bitmap floor;
    private Bitmap rock;
    private Bitmap enemy1[][] = new Bitmap[11][2];
    private Bitmap enemyAttack[][] = new Bitmap[7][2];
    private Bitmap player[][] = new Bitmap[8][2];
    private Bitmap joy;
    private Bitmap joy2;
    private Bitmap l_b;
    private Bitmap l_hit[][] = new Bitmap[4][2];
    private Bitmap r_hit[][] = new Bitmap[4][2];
    private Bitmap r_b;
    private Bitmap helper;

    private int towardPointX;
    private int towardPointY;

    public GameThread(Context context, SurfaceHolder surfaceHolder) {

        floor = BitmapFactory.decodeResource(context.getResources(), R.drawable.floor);
        rock = BitmapFactory.decodeResource(context.getResources(), R.drawable.rock);
        l_b = BitmapFactory.decodeResource(context.getResources(), R.drawable.l);
        r_b = BitmapFactory.decodeResource(context.getResources(), R.drawable.r);
        joy = BitmapFactory.decodeResource(context.getResources(), R.drawable.joystick);
        joy2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.joystick2);
        helper = BitmapFactory.decodeResource(context.getResources(), R.drawable.fur_player);

        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < 4; i++) {
                player[j*4+i][0] = Bitmap.createBitmap(helper, i*helper.getWidth()/4, j*helper.getHeight()/2, helper.getWidth()/4, helper.getHeight()/2);
            }
        }
        helper = BitmapFactory.decodeResource(context.getResources(), R.drawable.fur_player_left_side);
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < 4; i++) {
                player[j*4+i][1] = Bitmap.createBitmap(helper, i*helper.getWidth()/4, j*helper.getHeight()/2, helper.getWidth()/4, helper.getHeight()/2);
            }
        }
        helper = BitmapFactory.decodeResource(context.getResources(), R.drawable.l_hit);
        for (int i = 0; i < 4; i++){
            l_hit[i][0] = Bitmap.createBitmap(helper, i*helper.getWidth()/4, 0, helper.getWidth()/4, helper.getHeight());
        }
        helper = BitmapFactory.decodeResource(context.getResources(), R.drawable.l_hit_left_side);
        for (int i = 0; i < 4; i++){
            l_hit[i][1] = Bitmap.createBitmap(helper, i*helper.getWidth()/4, 0, helper.getWidth()/4, helper.getHeight());
        }
        helper = BitmapFactory.decodeResource(context.getResources(), R.drawable.r_hit);
        for (int i = 0; i < 4; i++){
            r_hit[i][0] = Bitmap.createBitmap(helper, i*helper.getWidth()/4, 0, helper.getWidth()/4, helper.getHeight());
        }
        helper = BitmapFactory.decodeResource(context.getResources(), R.drawable.r_hit_left_side);
        for (int i = 0; i < 4; i++){
            r_hit[i][1] = Bitmap.createBitmap(helper, i*helper.getWidth()/4, 0, helper.getWidth()/4, helper.getHeight());
        }
        helper = BitmapFactory.decodeResource(context.getResources(), R.drawable.first_enemy);
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 4; i++) {
                if (j == 2 && i == 3){
                    continue;
                }
                enemy1[j*4+i][0] = Bitmap.createBitmap(helper, i*helper.getWidth()/4, j*helper.getHeight()/3, helper.getWidth()/4, helper.getHeight()/3);
            }
        }
        helper = BitmapFactory.decodeResource(context.getResources(), R.drawable.first_enemy_left_side);
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 4; i++) {
                if (j == 2 && i == 3){
                    continue;
                }
                enemy1[j*4+i][1] = Bitmap.createBitmap(helper, i*helper.getWidth()/4, j*helper.getHeight()/3, helper.getWidth()/4, helper.getHeight()/3);
            }
        }
        helper = BitmapFactory.decodeResource(context.getResources(), R.drawable.first_enemy_attack);
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < 4; i++) {
                if (j == 1 && i == 3){
                    continue;
                }
                enemyAttack[j*4+i][0] = Bitmap.createBitmap(helper, i*helper.getWidth()/4, j*helper.getHeight()/2, helper.getWidth()/4, helper.getHeight()/2);
            }
        }
        helper = BitmapFactory.decodeResource(context.getResources(), R.drawable.first_enemy_attack_l);
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < 4; i++) {
                if (j == 1 && i == 3){
                    continue;
                }
                enemyAttack[j*4+i][1] = Bitmap.createBitmap(helper, i*helper.getWidth()/4, j*helper.getHeight()/2, helper.getWidth()/4, helper.getHeight()/2);
            }
        }

        this.surfaceHolder = surfaceHolder;
    }

    public void requestStop() {
        running = false;
    }

    public void setTowardPoint(int x, int y) {
        towardPointX = x;
        towardPointY = y;
    }

    public boolean getRunning() {
        return running;
    }

    @Override
    public void run() {
        Random random = new Random();
        // Переменные для положения джойстика
        int jX, jY;
        // Переменные для регулеровок размера всего и вся
        int xRatio, yRatio, scaleP, scaleJ, scaleJ2, scaleJ3, xRatioH, yRatioH;
        double scaleLHit;
        // Переменные для подсчета размера поля
        double help, help2;
        // Переменные для подсчета размера и положения джойстиков
        int jc = 0, jc2 = 0, jc3;
        int jZeroX = 0, jIndent;
        // Переменные для перемещения персонажа
        int movementX = 0, movementY, CenterX, CenterY;
        double movementCo;
        // Переменные для камня...
        int rockX = 0, rockY = 0;
        // Переменные для отслеживания кадра персонажа
        int frame = 0, side = 0, enemyFrame = 0, hitHelper = 0, noHitTimer = 0;
        byte  isHit = 0;
        // Счет
        int SCORE = 0;

        // Регулеровка размера
        scaleP = 1;
        scaleLHit = 2.1;
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

        Paint text = new Paint();

        while (running) {

            canvas = surfaceHolder.lockCanvas();

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

                playerPointX = rZeroX + rw / 2;
                playerPointY = rh / 3;

                rockX = random.nextInt(rw - 100);
                rockY = random.nextInt(rh);

                // Масштабирование
                floor = Bitmap.createScaledBitmap(floor, rw, rh, true);
                rock = Bitmap.createScaledBitmap(rock, rw / 30, rh / 20, true);
                for (int j = 0; j < 2; j++) {
                    for (int i = 0; i < 8; i++) {
                        player[i][j] = Bitmap.createScaledBitmap(player[i][j], (rw / xRatio) / scaleP, (rh / yRatio) / scaleP, true);
                    }
                }
                for (int j = 0; j < 2; j++) {
                    for (int i = 0; i < 11; i++) {
                        enemy1[i][j] = Bitmap.createScaledBitmap(enemy1[i][j], (rw / xRatio) / scaleP, (rh / yRatio) / scaleP, true);
                    }
                }
                for (int j = 0; j < 2; j++) {
                    for (int i = 0; i < 7; i++) {
                        enemyAttack[i][j] = Bitmap.createScaledBitmap(enemyAttack[i][j], (int)((rw / xRatio) / scaleP * 1.5), (rh / yRatio) / scaleP, true);
                    }
                }
                for (int j = 0; j < 2; j++) {
                    for (int i = 0; i < 4; i++) {
                        l_hit[i][j] = Bitmap.createScaledBitmap(l_hit[i][j], (int)((rw / xRatioH) / scaleLHit), (int)((rw / yRatioH) / scaleLHit), true);
                    }
                }
                for (int j = 0; j < 2; j++) {
                    for (int i = 0; i < 4; i++) {
                        r_hit[i][j] = Bitmap.createScaledBitmap(r_hit[i][j], (int)((rw / xRatioH) / scaleLHit), (int)((rw / yRatioH) / scaleLHit), true);
                    }
                }
                l_b = Bitmap.createScaledBitmap(l_b, jc3, jc3, true);
                r_b = Bitmap.createScaledBitmap(r_b, jc3, jc3, true);
                joy = Bitmap.createScaledBitmap(joy, jc, jc, true);
                joy2 = Bitmap.createScaledBitmap(joy2, jc2, jc2, true);

                text.setColor(Color.RED);
                text.setARGB(255, 255, 0, 0);
                text.setTextAlign(Paint.Align.CENTER);
                text.setTextSize((int)(rh / 7));
            }

            // Замедление работы
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {

            }

            if (canvas != null) {
                try {
                    canvas.drawARGB(255, 0, 0, 0);
                    canvas.drawBitmap(floor, rZeroX, 0, backgroundPaint);
                    canvas.drawBitmap(rock, rZeroX + rockX, rockY, backgroundPaint);
                    canvas.drawBitmap(joy, jZeroX, rh - jc - jIndent, backgroundPaint);
                    canvas.drawBitmap(l_b, rw + rZeroX - jIndent - l_b.getWidth() * 2, rh - l_b.getHeight() - jIndent, backgroundPaint);
                    canvas.drawBitmap(r_b, rw + rZeroX - jIndent - r_b.getWidth(), rh - jIndent - r_b.getHeight() * 2, backgroundPaint);

                    for (int i = 0; i < 2; i++){
                        if (enemies[i] == null) {
                            CreateEnemy(1,i);
                        } else if (enemies[i].health <= 0 && enemies[i].deathCount <= 0){
                            CreateEnemy(1,i);
                            SCORE++;
                        }
                    }

                    enemyFrame++;
                    if (enemyFrame == Integer.MAX_VALUE){
                        enemyFrame = 1;
                    }

                    if (noHitTimer == 0 && towardPointX > rw + rZeroX - jIndent - l_b.getWidth() * 2 && towardPointY > rh - l_b.getHeight() - jIndent && towardPointX < rw + rZeroX - jIndent - l_b.getWidth() && towardPointY < rh - jIndent){
                        hitHelper = 0;
                        isHit = 1;
                        noHitTimer = 10;
                    }

                    if (noHitTimer == 0 && towardPointX > rw + rZeroX - jIndent - r_b.getWidth() && towardPointY > rh - r_b.getHeight() * 2 - jIndent && towardPointX < rw + rZeroX - jIndent && towardPointY < rh - jIndent - r_b.getHeight()){
                        hitHelper = 0;
                        isHit = 2;
                        noHitTimer = 10;
                    }

                    if (isHit == 0) {

                        if (noHitTimer > 0) {
                            noHitTimer--;
                        }

                        if (towardPointX > 0 && towardPointX < rZeroX + rw / 2 && towardPointY > 0 && towardPointY < rh) {

                            jX = towardPointX;
                            jY = towardPointY;

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

                            canvas.drawBitmap(joy2, (int)(jX - jc2 / 2), (int)(jY - jc2 / 2), backgroundPaint);
                            movementX = (int) ((jX - (jZeroX + jc / 2)) * movementCo);
                            movementY = (int) ((jY - (rh - jc / 2 - jIndent)) * movementCo);

                            frame++;
                            frame = frame % 8;

                            playerPointX += movementX;
                            playerPointY += movementY;

                            if (movementX > 0) {
                                side = 0;
                            } else if (movementX < 0) {
                                side = 1;
                            }

                            if (playerPointX > rw - player[0][0].getWidth()) {
                                playerPointX = rw - player[0][0].getWidth();
                            } else if (playerPointX < 0) {
                                playerPointX = 0;
                            }
                            if (playerPointY > rh - player[0][0].getHeight()) {
                                playerPointY = rh - player[0][0].getHeight();
                            } else if (playerPointY < 0) {
                                playerPointY = 0;
                            }

                        } else {
                            canvas.drawBitmap(joy2, (int)(jZeroX + jc / scaleJ2), (int)(rh - jc + jc / scaleJ2 - jIndent), backgroundPaint);
                            frame = 0;
                        }

                        canvas.drawBitmap(player[frame][side], rZeroX + playerPointX, playerPointY, backgroundPaint);

                    } else {
                        canvas.drawBitmap(joy2, (int)(jZeroX + jc / scaleJ2), (int)(rh - jc + jc / scaleJ2 - jIndent), backgroundPaint);

                        if (hitHelper < 4){
                            frame = hitHelper;
                        } else {
                            frame = 6 - hitHelper;
                        }

                        if (movementX > 0) {
                            side = 0;
                        } else if (movementX < 0) {
                            side = 1;
                        }

                        if (side == 0) {
                            switch (hitHelper) {
                                case 1:
                                    playerPointX -= rw / 100;
                                    playerPointY += rw / 100;
                                    break;
                                case 2:
                                    playerPointX += rw / 80;
                                    playerPointY -= rw / 50;
                                    break;
                                case 3:
                                    playerPointX += rw / 20;
                                    playerPointY -= rw / 200;
                                    break;
                                case 4:
                                    playerPointX += rw / 80;
                                    playerPointY += rw / 80;
                                    break;
                            }
                        } else {
                            switch (hitHelper) {
                                case 1:
                                    playerPointX += rw / 100;
                                    playerPointY += rw / 100;
                                    break;
                                case 2:
                                    playerPointX -= rw / 80;
                                    playerPointY -= rw / 50;
                                    break;
                                case 3:
                                    playerPointX -= rw / 20;
                                    playerPointY -= rw / 200;
                                    break;
                                case 4:
                                    playerPointX -= rw / 80;
                                    playerPointY += rw / 80;
                                    break;
                            }
                        }

                        if (isHit == 1) {
                            canvas.drawBitmap(l_hit[frame][side], rZeroX + playerPointX, playerPointY, backgroundPaint);
                        } else {
                            canvas.drawBitmap(r_hit[frame][side], rZeroX + playerPointX, playerPointY, backgroundPaint);
                        }

                        if (hitHelper == 6){
                            isHit = 0;
                            towardPointY = 0;
                            towardPointX = 0;
                        }
                        if (playerPointX < 0){
                            playerPointX = 0;
                        } else if (playerPointX > rw){
                            playerPointX = rw - player[0][0].getWidth();
                        }

                        hitHelper++;

                        CenterX = playerPointX + rZeroX + player[0][0].getWidth() / 2;
                        CenterY = playerPointY + player[0][0].getHeight() / 2;
                        for (int i = 0; i < 2; i++) {
                            if (CenterX < enemies[i].positionX + enemy1[0][0].getWidth() && CenterX > enemies[i].positionX && CenterY < enemies[i].positionY + enemy1[0][0].getHeight() && CenterY > enemies[i].positionY){
                                enemies[i].health--;
                                if (enemies[i].health == 0){
                                    enemies[i].deathCount = 1;
                                }
                            }
                        }
                    }

                    for (int i = 0; i < 5; i++) {
                        if (AreYouDead){
                            continue;
                        }
                        if (enemies[i] == null){
                            continue;
                        }
                        if (!enemies[i].isAttack){
                            MoveEnemy(i);
                        }
                        DrawEnemy(enemyFrame, i, isHit);
                    }

                    if (AreYouDead){
                        if (deathAlpha < 255) {
                            canvas.drawARGB(deathAlpha, 0, 0, 0);
                            deathAlpha += 20;
                            towardPointX = 0;
                            towardPointY = 0;
                        } else {
                            canvas.drawARGB(255,0,0,0);
                            canvas.drawText("Ты мертв. Твой счет: " + SCORE, (int)(canvas.getWidth() / 2), (int)(canvas.getHeight() / 2), text);
                            if (towardPointX != 0 && towardPointY != 0){
                                requestStop();
                            }
                        }
                    }
                } finally{
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    private void CreateEnemy(int id, int en){
        int min = rZeroX;
        int max = rw / 3;
        int diff = max - min;
        Random random = new Random();
        enemies[en] = new Enemy();
        switch (id){
            case 1:
                enemies[en].speed = rw / 250;
                enemies[en].attackType = 0;
                enemies[en].health = 1;
                enemies[en].isAttack = false;
                enemies[en].attackFrame = 0;
                enemies[en].deathCount = 0;
                break;
            case 2:
                enemies[en].speed = 0;
                enemies[en].attackType = 1;
                enemies[en].health = 2;
                enemies[en].isAttack = false;
                enemies[en].attackFrame = 0;
                enemies[en].deathCount = 0;
                break;
            case 3:
                enemies[en].speed = 0;
                enemies[en].attackType = 0;
                enemies[en].health = 3;
                enemies[en].isAttack = false;
                enemies[en].attackFrame = 0;
                enemies[en].deathCount = 0;
                break;
        }
        enemies[en].positionX = min + random.nextInt(diff);
        enemies[en].positionY = random.nextInt(rh - enemy1[0][0].getHeight());
    }

    private void DrawEnemy(int enemyFrame, int en, byte isHit){
        int CenterX, CenterY;

        CenterX = playerPointX + rZeroX + player[0][0].getWidth() / 2;
        CenterY = playerPointY + player[0][0].getHeight() / 2;

        if (enemies[en] != null && enemies[en].health > 0){
            if (enemies[en].positionX + enemy1[0][0].getWidth() / 2 < rZeroX + playerPointX + player[0][0].getWidth() / 2){
                enemySide = 0;
            } else {
                enemySide = 1;
            }
        } else if (enemies[en] != null && enemies[en].health <= 0 && enemies[en].deathCount != 0){
            switch (enemies[en].deathCount){
                case 1:
                    enemies[en].deathCount++;
                    backgroundPaint.setAlpha(200);
                    break;
                case 2:
                    enemies[en].deathCount++;
                    backgroundPaint.setAlpha(150);
                    break;
                case 3:
                    enemies[en].deathCount++;
                    backgroundPaint.setAlpha(100);
                    break;
                case 4:
                    enemies[en].deathCount = -3;
                    backgroundPaint.setAlpha(50);
                    break;
            }
            canvas.drawBitmap(enemy1[0][0], enemies[en].positionX, enemies[en].positionY, backgroundPaint);
            backgroundPaint.setAlpha(255);
        }
        if (enemies[en].isAttack && enemies[en].deathCount <= 0){
            if (enemySide == 1){
                canvas.drawBitmap(enemyAttack[enemies[en].attackFrame][enemySide], (int)(enemies[en].positionX - rw / 25), enemies[en].positionY, backgroundPaint);
            } else {
                canvas.drawBitmap(enemyAttack[enemies[en].attackFrame][enemySide], enemies[en].positionX, enemies[en].positionY, backgroundPaint);
            }

            if ((enemies[en].attackFrame == 4 || enemies[en].attackFrame == 5) && isHit == 0 && (CenterX < enemies[en].positionX + enemyAttack[0][0].getWidth() && CenterX > enemies[en].positionX - rw / 25 && CenterY < enemies[en].positionY + enemyAttack[0][0].getHeight() && CenterY > enemies[en].positionY)){
                AreYouDead=true;
            }

            if (enemies[en].attackFrame == 6){
                enemies[en].attackFrame = 0;
                enemies[en].isAttack = false;
            }

            enemies[en].attackFrame++;

        } else if (enemies[en].deathCount <= 0){
            canvas.drawBitmap(enemy1[enemyFrame % 11][enemySide], enemies[en].positionX, enemies[en].positionY, backgroundPaint);
        }
    }

    private void MoveEnemy(int en){
        int roadX, roadY, movePoint;

        if (enemies[en] != null && enemies[en].health > 0){

            roadX = enemies[en].positionX - rZeroX - playerPointX;
            roadY = enemies[en].positionY - playerPointY;

            movePoint = enemies[en].speed;

            if (roadX < 0){
                roadX *= -1;
            }
            if (roadY < 0){
                roadY *= -1;
            }

            while (movePoint > 0){
                if (roadX >= roadY + 10 && roadX >= rw / 20 && roadY >= rh / 20 && enemies[en].positionX > rZeroX + playerPointX && enemies[en].positionY > playerPointY){
                    enemies[en].positionX -= 10;
                    enemies[en].positionY -= 10;
                    movePoint -= 20;
                } else if (roadX >= roadY + 10 && roadX >= rw / 20 && roadY >= rh / 20 && enemies[en].positionX > rZeroX + playerPointX && enemies[en].positionY < playerPointY){
                    enemies[en].positionX -= 10;
                    enemies[en].positionY += 10;
                    movePoint -= 20;
                } else if (roadX >= roadY + 10 && roadX >= rw / 20 && roadY >= rh / 20 && enemies[en].positionX < rZeroX + playerPointX && enemies[en].positionY > playerPointY){
                    enemies[en].positionX += 10;
                    enemies[en].positionY -= 10;
                    movePoint -= 20;
                } else if (roadX >= roadY + 10 && roadX >= rw / 20 && roadY >= rh / 20 && enemies[en].positionX < rZeroX + playerPointX && enemies[en].positionY < playerPointY){
                    enemies[en].positionX += 10;
                    enemies[en].positionY += 10;
                    movePoint -= 20;
                } else if (roadX >= roadY + 10 && roadX >= rw / 20 && roadY < rh / 20 && enemies[en].positionX > rZeroX + playerPointX){
                    enemies[en].positionX -= 10;
                    movePoint -= 10;
                } else if (roadX >= roadY + 10 && roadX >= rw / 20 && roadY < rh / 20 && enemies[en].positionX < rZeroX + playerPointX){
                    enemies[en].positionX += 10;
                    movePoint -= 10;
                } else if (roadX < roadY + 10 && roadY >= rh / 20 && enemies[en].positionY > playerPointY){
                    enemies[en].positionY -= 10;
                    movePoint -= 10;
                } else if (roadX < roadY + 10 && roadY >= rh / 20 && enemies[en].positionY < playerPointY){
                    enemies[en].positionY += 10;
                    movePoint -= 10;
                } else if (roadX < rw / 20 && roadY < rh / 20 && enemies[en].attackType == 0){
                    enemies[en].isAttack = true;
                    movePoint = 0;
                } else {
                    movePoint = 0;
                }
            }
        }
    }
}