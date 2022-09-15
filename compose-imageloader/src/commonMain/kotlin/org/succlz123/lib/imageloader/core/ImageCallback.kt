package org.succlz123.lib.imageloader.core

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale

class ImageCallback(
    val placeHolderView: (@Composable () -> Unit)? = null,
    val errorView: (@Composable () -> Unit)? = null,
    val imageView: (@Composable (Painter) -> Unit)
) {

    companion object {

        fun default(
            contentDescription: String? = null,
            modifier: Modifier = Modifier,
            alignment: Alignment = Alignment.Center,
            contentScale: ContentScale = ContentScale.Fit,
            alpha: Float = DefaultAlpha,
            colorFilter: ColorFilter? = null,
            placeHolderView: (@Composable () -> Unit)? = null,
            errorView: (@Composable () -> Unit)? = null,
        ): ImageCallback {
            return ImageCallback(placeHolderView, errorView) {
                Image(
                    painter = it,
                    contentDescription = contentDescription,
                    modifier = modifier,
                    alignment = alignment,
                    contentScale = contentScale,
                    alpha = alpha,
                    colorFilter = colorFilter
                )
            }
        }
    }
}