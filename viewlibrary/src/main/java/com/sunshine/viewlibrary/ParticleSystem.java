package com.sunshine.viewlibrary;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleSystem {
    private List<Particle> particles = new ArrayList<>();
    private int border_width = 20;
    private Paint paint = new Paint();
    private Random random;
    int mMaxHeight, mMinHeight, mWidth;

    public ParticleSystem(int particlesCount, int maxHeight, int minHeight, int width) {
        mMaxHeight = maxHeight;
        mMinHeight = minHeight;
        mWidth = width;
        LinearGradient lg = new LinearGradient(0, 0, 20, 200, Color.parseColor("#D98719"), Color.rgb(226, 17, 12), Shader.TileMode.REPEAT);  //
//        paint.setColor();
        paint.setShader(lg);
        random = new Random();
        for (int i = 0; i < particlesCount; i++) {
            Particle particle = new Particle(random.nextInt(width), mMaxHeight);
            particle.xv = 0.5f - 1 * random.nextFloat();
            particles.add(particle);
        }
    }

    public void onDraw(Canvas canvas) {
        for (int i = 0; i < particles.size(); i++) {
            Particle particle = particles.get(i);
            paint.setAlpha(particle.alpha);
            canvas.drawRect(particle.getX(), particle.getY(), particle.getX() + border_width, particle.getY() + border_width, paint);
        }
    }

    /**
     * 粒子集合更新方法
     *
     * @param wind wind是风，0时代表无风，粒子不偏移
     */
    public void update(float wind) {
        for (int i = 0; i < particles.size(); i++) {
            Particle particle = particles.get(i);
            if (particle.xv > 0) {
                //当初始加速度为正，就一直为正
                particle.xv += 0.01f + wind;
            } else {
                //当初始加速度为负，就一直为正
                particle.xv += -0.01f + wind;
            }
            particle.yv = particle.yv + 0.1f;
            particle.setX(particle.getX() + particle.xv);
            particle.setY(particle.getY() - particle.yv, mMaxHeight, mMinHeight);
        }
        List<Particle> list = new ArrayList<>();
        list.addAll(particles);
        for (Particle particle : list) {
            if (particle.getY() < mMinHeight) {
                particles.remove(particle);
            }
        }
        for (int i = 0; i < 5; i++) {
            Particle particle = new Particle(random.nextInt(mWidth), mMaxHeight);

            particle.xv = 0.5f - 1 * random.nextFloat();
            particles.add(particle);
        }
    }

}