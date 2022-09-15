package org.succlz123.lib.imageloader.transform

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap

class CenterCropTransformation(private val width: Int, private val height: Int) : ITransformation {

    override fun tag(): String {
        return TransformationTag.CenterCropTransformation
    }

    override fun transform(inputImage: ImageBitmap): ImageBitmap {
        val sourceWidth = inputImage.width
        val sourceHeight = inputImage.height
        if (sourceWidth == width && sourceHeight == height) {
            return inputImage
        }
        val xScale = width.toDouble() / sourceWidth
        val yScale = height.toDouble() / sourceHeight
        val (newXScale, newYScale) = if (yScale > xScale) {
            ((1.0 / yScale) * xScale) to 1.0
        } else {
            1.0 to ((1.0 / xScale) * yScale)
        }
        val scaledWidth = newXScale * sourceWidth
        val scaledHeight = newYScale * sourceHeight

        val left = ((sourceWidth - scaledWidth) / 2).toInt()
        val top = ((sourceHeight - scaledHeight) / 2).toInt()
        val width = scaledWidth.toInt()
        val height = scaledHeight.toInt()

        val centeredImage = inputImage.toAwtImage().getSubimage(left, top, width, height)
        return ResizeTransformation(width, height).transform(centeredImage.toComposeImageBitmap())
    }
}