package ru.sindeev.mygame;

import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private Context context;

    private GameThread gameThread;

    public GameView(Context context) {
        super(context);
        this.context = context;
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        gameThread = new GameThread(getContext(), getHolder());
        gameThread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!gameThread.getRunning()){
            gameThread.interrupt();
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN) {
            gameThread.setTowardPoint((int) event.getX(), (int) event.getY());
        } else if (event.getAction() == MotionEvent.ACTION_UP){
            gameThread.setTowardPoint(0, 0);
        }
        return true;
    }
}
