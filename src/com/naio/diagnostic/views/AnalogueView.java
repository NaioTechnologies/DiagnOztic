package com.naio.diagnostic.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class AnalogueView extends View {

	
    float x,y;
    double r,t;
    int cx, cy,w,h;
    int RADIUS = 100;//width = height >   =width/4
    Paint black = new Paint();
    Paint grey = new Paint();
    Paint white = new Paint();

    private int toDo;
	private OnMoveListener moveListener;
	Handler mHandler = new Handler();

    public AnalogueView(Context context, AttributeSet attrs) {
        super(context, attrs);
        black.setColor(Color.BLACK);
        grey.setColor(Color.GRAY);
        white.setColor(Color.WHITE);
        black.setFlags(Paint.ANTI_ALIAS_FLAG);
        white.setFlags(Paint.ANTI_ALIAS_FLAG);
        grey.setFlags(Paint.ANTI_ALIAS_FLAG);
    }
    
    public void setRADIUS(int rADIUS) {
		RADIUS = rADIUS;
	}
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.w = w;
        this.h = h;
        cx = w/2;
        cy = h/2;
        x = cx;y=cy;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawMyStuff(canvas);
        switch (toDo) {
        case 1:
            center();
            break;
        default:
            break;
        }

    }

    private void drawMyStuff(final Canvas canvas) {
        canvas.drawCircle(cx, cy, w/2, black);
        canvas.drawCircle(cx, cy, w/2-5, grey);
        canvas.drawCircle(cx, cy, w/2-10, black);
        canvas.drawCircle(x, y, RADIUS+2, white);
        canvas.drawCircle(x, y, RADIUS, grey);
    }

//  n2p : normal to polar coordinates conversion
//  p2n : polar to normal coordinates conversion
//  R : distance to polar center 
//  T : polar angle
    double n2pR(double x, double y){
        return distance(x,y,cx,cy);
    }

    double n2pT(double x, double y){
        return Math.atan2((y-cy),(x-cx));
    }

    double p2nX(double r, double t){
        return r*Math.cos(t) + cx;
    }



    double p2nY(double r, double t){
        return r*Math.sin(t) + cy;
    }

    double n2pR(){
        return distance(x,y,cx,cy);
    }

    double n2pT(){
        return Math.atan2((y-cy),(x-cx));
    }

    double p2nX(){
        return r*Math.cos(t) + cx;
    }

    double p2nY(){
        return r*Math.sin(t) + cy;
    }

    double distance(double x1 , double y1, double x2, double y2 ){
        return Math.sqrt(Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2));
    }
    Runnable mAction = new Runnable() {
        @Override public void run() {
            moveListener.sendMotorsCommand();
            mHandler.postDelayed(this, 20);
        }
    };
    @Override
    public boolean onTouchEvent(MotionEvent event) {
   
        switch(event.getAction()){
        case MotionEvent.ACTION_DOWN :
        	mHandler.postDelayed(mAction, 20);
        case MotionEvent.ACTION_MOVE :
            updatePosition(event);
            break;    
        case MotionEvent.ACTION_UP :
        	mHandler.removeCallbacks(mAction);
            toDo = 1; 
            center(); 
            break;
        default :
        	break;
        }
        return true;
    }

    private void center(){
        if(r>15){
            r-=15;
        }else{
            toDo=0;
            r=0;
        }
        x = (float) p2nX();
        y= (float) p2nY();
        invalidate();
    }

    void updatePosition(MotionEvent e){
        r= Math.min(w/2-RADIUS, n2pR(e.getX(),e.getY()));
        t = n2pT(e.getX(),e.getY());
        x = (float) p2nX();
        y =(float) p2nY();
        float xb=(x/2)-RADIUS;
        float yb=(y/2)-RADIUS;
        float factor = 126/(RADIUS/2) ;
        xb *= factor;
        yb *= -1* factor;
        
        
        if(moveListener != null)
            if(r == w/2-RADIUS)
                moveListener.onMaxMoveInDirection((int)xb,(int)yb);
            else if(r >= w/4-RADIUS/2)
                moveListener.onHalfMoveInDirection((int)xb,(int)yb);

        invalidate();
    }
    
    public void setOnMoveListener(OnMoveListener listener){
        moveListener =listener;
    }

    public interface OnMoveListener{
        public void onHalfMoveInDirection(int x, int y);
        public void onMaxMoveInDirection(int x, int y);
        public void sendMotorsCommand();
    }
}