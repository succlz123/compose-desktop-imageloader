package org.succlz123.lib.imageloader.transform

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import java.awt.BasicStroke
import java.awt.RenderingHints
import java.awt.geom.Arc2D
import java.awt.image.BufferedImage

class CircleCropTransformation(
    private val strokeWidth: Float? = null,
    private val strokeColor: Color? = null,
    private val backgroundColor: Color? = null
) : ITransformation {

    override fun tag(): String {
        return TransformationTag.CircleCropTransformation
    }

    override fun transform(inputImage: ImageBitmap): ImageBitmap {
        val tmp = inputImage.toAwtImage()
        val size = if (inputImage.width > inputImage.height) {
            inputImage.width
        } else {
            inputImage.height
        }
        val circleBuffer = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
        val graphics = circleBuffer.createGraphics()
        try {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            graphics.paint = if (backgroundColor != null) {
                java.awt.Color(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.alpha)
            } else {
                java.awt.Color(0, 0, 0, 0)
            }
            graphics.fillRect(0, 0, size, size)
            val arc = Arc2D.Float(0f, 0f, size.toFloat(), size.toFloat(), 0f, -360f, Arc2D.OPEN)
            graphics.clip = arc
            graphics.drawImage(tmp, 0, 0, size, size, null)

            if (strokeWidth != null) {
                graphics.color = if (strokeColor != null) {
                    java.awt.Color(
                        strokeColor.red, strokeColor.green, strokeColor.blue, strokeColor.alpha
                    )
                } else {
                    java.awt.Color(0, 0, 0, 0)
                }
                graphics.stroke = BasicStroke(strokeWidth)
                graphics.draw(arc)
            }
        } finally {
            graphics.dispose()
        }
        return circleBuffer.toComposeImageBitmap()
    }
}