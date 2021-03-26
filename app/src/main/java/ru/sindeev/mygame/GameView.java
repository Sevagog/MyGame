package ru.sindeev.mygame;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    // Что это?
    private GameThread gameThread;

    // Что это?
    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    // Что это?
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        gameThread = new GameThread(getContext(), getHolder());
        gameThread.start();
    }

    // Что это?
    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    // Что это?
    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        gameThread.requestStop();
        boolean retry = true;
        while (retry) {
            try {
                gameThread.join();
                retry = false;
            } catch (InterruptedException e) {

            }
        }
    }

    // Что это?
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN) {
            gameThread.setTowardPoint((int) event.getX(), (int) event.getY());
        } else if (event.getAction() == MotionEvent.ACTION_UP){
            gameThread.setTowardPoint(0, 0);
        }

        // Решение проблемы
        return true;
    }


}
