/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package net.proest.lp2go3.UI;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.text.DecimalFormat;


public class PidSeekBar extends SeekBar implements SeekBar.OnSeekBarChangeListener, SeekBar.OnTouchListener, View.OnClickListener {

    private PidTextView mTxtSeekBarProgress;
    private int mDenominator;
    private int mStep;
    private String mDecimalFormatString;
    private ImageView mLock;
    private boolean mLockOpen;
    private int mProgress;
    private boolean mAllowUpdateFromFC = true;
    private String mName;

    public PidSeekBar(Context context) {
        super(context);
    }

    public PidSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PidSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public String getName() {
        return mName;
    }

    public void setAllowUpdateFromFC(boolean allowUpdate) {
        this.mAllowUpdateFromFC = allowUpdate;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser && mLockOpen) {
            if (progress < mProgress) {
                this.setProgress(mProgress - mStep);
            } else if (progress > mProgress) {
                this.setProgress(mProgress + mStep);
            }
        }
        int p = seekBar.getProgress();
        mProgress = p;

        if (p % mStep != 0) {
            p = p - p % mStep;
        }

        float v = (float) p / mDenominator;
        mTxtSeekBarProgress.setText(getDecimalString(v));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void init(PidTextView textView, ImageView lock, int denominator, int max, int step, String dfs, String name) {
        this.mDenominator = denominator;
        this.mStep = step;
        this.mTxtSeekBarProgress = textView;
        this.mTxtSeekBarProgress.setPidSeekBar(this);
        this.mDecimalFormatString = dfs;
        this.mName = name;

        this.setProgress(0);
        this.setMax(max);
        this.setOnSeekBarChangeListener(this);

        this.mLock = lock;

        mLock.setOnClickListener(this);

        mLockOpen = false;

        onProgressChanged(this, 0, false);
        //this.setProgress(0d);

        this.setOnTouchListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mLock)) {
            mLockOpen = !mLockOpen;
            if (mLockOpen) {
                mLock.setColorFilter(Color.argb(0xff, 0x00, 0x80, 0x00));
            } else {
                mLock.setColorFilter(Color.argb(0xff, 0xd4, 0x00, 0x00));
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return !mLockOpen;
    }

    String getDecimalString(float v) {
        DecimalFormat df = new DecimalFormat(mDecimalFormatString);
        return df.format(v);
    }

    /*
    * Set Progress from PollThread, overriding the lock.
    *
    * @returns true, if view and saved value are equal
     */
    public void setProgress(float p) {
        int ip = Math.round(p * this.mDenominator);
        if (mAllowUpdateFromFC) {
            this.setProgress(ip);
            mAllowUpdateFromFC = false;
        }
        if (ip == this.getProgress()) {
            mTxtSeekBarProgress.setTextColor(Color.argb(0xff, 0x33, 0x33, 0x33));
        } else {
            mTxtSeekBarProgress.setTextColor(Color.argb(0xff, 0xff, 0x00, 0x00));
        }
    }

    void setProgressOverride(float p) {
        int ip = Math.round(p * this.mDenominator);
        this.setProgress(ip);
    }
}
