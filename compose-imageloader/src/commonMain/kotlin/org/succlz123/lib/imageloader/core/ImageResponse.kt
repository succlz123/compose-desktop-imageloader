package org.succlz123.lib.imageloader.core

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter

internal fun ImageBitmap.toBitmapPainter(): BitmapPainter {
    return BitmapPainter(this)
}

class ImageResponse(
    val imagePainter: Painter?, val exception: Exception?, val isLoading: Boolean = false
) {

}