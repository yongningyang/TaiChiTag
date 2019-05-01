package me.yongning.taichitag;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author yongningyang@gmail.com
 * @date 2019-05-01
 * @Description
 */
public class TaiChiTag extends View {
    private int index = 0;
    private int mClickDownLastX;
    private int mClickDownLastY;
    private String[] names = new String[]{"陈奕迅", "庄心妍", "汪苏泷", "许嵩", "半阳摩登兄弟", "李玉刚", "胡夏", "吴亦凡", "贺一航", "梁博", "白小白", "鹿晗", "Taylor Swift (泰勒·斯威夫特)", "于文文", "李宇春", "曲肖冰", "张靓颖", "王力宏", "张学友", "BEYOND", "胡66", "陈粒", "杨宗纬", "张杰", "华晨宇", "陈奕迅", "庄心妍", "汪苏泷", "许嵩", "半阳摩登兄弟", "李玉刚", "胡夏", "吴亦凡", "贺一航", "梁博", "白小白", "鹿晗", "Taylor Swift (泰勒·斯威夫特)", "于文文", "李宇春", "曲肖冰", "张靓颖", "王力宏", "张学友", "BEYOND", "胡66", "陈粒", "杨宗纬", "张杰", "华晨宇"};
    private int[] colors = new int[]{Color.YELLOW, Color.BLUE, Color.RED, Color.GREEN};
    private List<RectF> already = new ArrayList<>(names.length);
    private int maxTextSize = 20;
    private int minTextSize = 10;
    private int rings;
    private TextPaint textPaint;

    public TaiChiTag(Context context) {
        super(context);
    }

    public TaiChiTag(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TaiChiTag(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        maxTextSize = width / 20;
        minTextSize = width / 25;
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(colors[0]);
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.CENTER);
        index = 0;


        rings = width / 2 / maxTextSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        while (index < names.length) {
            textPaint.setColor(colors[new Random().nextInt(4)]);
            textPaint.setTextSize(new Random().nextInt(maxTextSize - minTextSize) + minTextSize);
            RectF rect = getNextRect(canvas);
            if (rect != null) {
                already.add(rect);
                if (rect.width() == textPaint.getTextSize()) {
                    canvas.rotate(-90, rect.centerX(), rect.centerY());
                    canvas.drawText(names[index], rect.centerX(), rect.centerY() + getBaseline(textPaint), textPaint);
                    canvas.rotate(90, rect.centerX(), rect.centerY());
                } else {
                    canvas.drawText(names[index], rect.centerX(), rect.centerY() + getBaseline(textPaint), textPaint);

                }

//                canvas.drawRect(rect, textPaint);
            }

            index++;
        }


    }

    public static float getBaseline(Paint p) {
        Paint.FontMetrics fontMetrics = p.getFontMetrics();
        return (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent;
    }

    private RectF getNextRect(Canvas canvas) {
        RectF rectF = new RectF();
        float height = textPaint.getTextSize();
        float width = textPaint.measureText(names[index]);
        if (new Random().nextBoolean()) {
            height = textPaint.measureText(names[index]);
            width = textPaint.getTextSize();

        }

        int radius = 0;
        int angle = 0;
        while (radius < rings) {
            while (angle < 361) {

                rectF.left = (float) (canvas.getWidth() / 2 - width / 2 + maxTextSize * radius * Math.cos(angle * Math.PI / 180.0));
                rectF.top = (float) (canvas.getHeight() / 2 - height / 2 + maxTextSize * radius * Math.sin(angle * Math.PI / 180.0));
                rectF.right = rectF.left + width;
                rectF.bottom = rectF.top + height;


                if (already.isEmpty()) {
                    return rectF;
                }

                boolean cross = false;
                for (int i = 0; i < already.size(); i++) {
                    RectF temp = new RectF(already.get(i));
                    if (temp.contains(rectF) || rectF.contains(temp) || temp.intersect(rectF) || !inOvalBounds(rectF)) {
                        cross = true;
                        break;
                    }
                }

                if (cross) {
                    angle += 30;
                } else {
                    return rectF;
                }
            }

            radius += 1;
            angle = 0;
        }


        return null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // For multiple touch
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:

                handleTouchDown(event);
                return true;
            case MotionEvent.ACTION_POINTER_DOWN:

                handleTouchDown(event);

                break;
            case MotionEvent.ACTION_MOVE:


                break;
            case MotionEvent.ACTION_POINTER_UP:

                handleTouchUp(event);

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:

                handleTouchUp(event);
                mClickDownLastX = -1;
                mClickDownLastY = -1;

                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        index = 0;
    }

    private void handleTouchUp(MotionEvent event) {
        for (int i = 0; i < already.size(); i++) {
            if (already.get(i).contains(mClickDownLastX, mClickDownLastY)) {
                Toast.makeText(getContext(), names[i], Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean inOvalBounds(RectF rectF) {
        int radius = getWidth() / 2;
        if (radius > Math.sqrt(Math.pow(rectF.left - radius, 2) + Math.pow(rectF.top - radius, 2))) {
            return true;
        }
        if (radius > Math.sqrt(Math.pow(rectF.left - radius, 2) + Math.pow(rectF.bottom - radius, 2))) {
            return true;
        }
        if (radius > Math.sqrt(Math.pow(rectF.right - radius, 2) + Math.pow(rectF.top - radius, 2))) {
            return true;
        }
        if (radius > Math.sqrt(Math.pow(rectF.right - radius, 2) + Math.pow(rectF.bottom - radius, 2))) {
            return true;
        }

        return false;
    }

    private void handleTouchDown(MotionEvent event) {
        final int actionIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        final int downX = (int) event.getX(actionIndex);
        final int downY = (int) event.getY(actionIndex);
        mClickDownLastX = downX;
        mClickDownLastY = downY;
    }
}
