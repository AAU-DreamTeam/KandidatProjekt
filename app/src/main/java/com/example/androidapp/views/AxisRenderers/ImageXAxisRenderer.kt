package com.example.androidapp.views.AxisRenderers

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

class ImageXAxisRenderer(
        viewPortHandler: ViewPortHandler?,
        xAxis: XAxis?,
        transformer: Transformer?,
        imageList: List<Drawable>,
        iconWidth:Int,
        iconHeight:Int

    ) :
        XAxisRenderer(viewPortHandler, xAxis, transformer) {
        var imageList: List<Drawable>
        var iconWidth: Int
        var iconHeight: Int

        init {
            this.imageList = imageList
            this.iconWidth = iconWidth
            this. iconHeight = iconHeight
        }

        override fun drawLabels(c: Canvas?, pos: Float, anchor: MPPointF?) {
            val labelRotationAngleDegrees = mXAxis.labelRotationAngle
            val centeringEnabled = mXAxis.isCenterAxisLabelsEnabled
            val positions = FloatArray(mXAxis.mEntryCount * 2)
            run {
                var i = 0
                while (i < positions.size) {


                    // only fill x values
                    if (centeringEnabled) {
                        positions[i] = mXAxis.mCenteredEntries[i / 2]
                    } else {
                        positions[i] = mXAxis.mEntries[i / 2]
                    }
                    i += 2
                }
            }
            mTrans.pointValuesToPixel(positions)
            var i = 0
            while (i < positions.size) {
                var x = positions[i]
                if (mViewPortHandler.isInBoundsX(x)) {
                    val label = mXAxis.valueFormatter.getAxisLabel(mXAxis.mEntries[i / 2], mXAxis)
                    if (mXAxis.isAvoidFirstLastClippingEnabled) {

                        // avoid clipping of the last
                        if (i / 2 == mXAxis.mEntryCount - 1 && mXAxis.mEntryCount > 1) {
                            val width = Utils.calcTextWidth(mAxisLabelPaint, label).toFloat()
                            if (width > mViewPortHandler.offsetRight() * 2
                                && x + width > mViewPortHandler.chartWidth
                            ) x -= width / 2

                            // avoid clipping of the first
                        } else if (i == 0) {
                            val width = Utils.calcTextWidth(mAxisLabelPaint, label).toFloat()
                            x += width / 2
                        }
                    }
                    drawImage(c, imageList[i/2], x.toInt(), pos.toInt(),iconHeight,iconWidth)
                }
                i += 2
            }
        }

        fun drawImage(
            c: Canvas?,
            drawableImage: Drawable,
            x: Int,
            y: Int,
            width: Int,
            height: Int
        ) {
            Utils.drawImage(c, drawableImage,
                x, y, width, height)
        }
    }