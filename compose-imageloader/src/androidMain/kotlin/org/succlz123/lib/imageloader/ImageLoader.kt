package org.succlz123.lib.imageloader

import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.Density
import org.succlz123.lib.imageloader.core.*
import org.succlz123.lib.imageloader.transform.*
import java.io.File

@Composable
actual fun ImageRes(resName: String, imageCallback: ImageCallback) {
}

actual fun ImageAsyncImageFile(
    filePath: String, transformations: List<ITransformation>?, imageCallback: ImageCallback
) {
}

@Composable
actual fun ImageAsyncImageUrl(url: String, transformations: List<ITransformation>?, imageCallback: ImageCallback) {
}


@Composable
actual fun ImageAsyncSvgFile(
    filePath: String, density: Density, imageCallback: ImageCallback
) {
}

@Composable
actual fun ImageAsyncSvgUrl(
    url: String, density: Density, imageCallback: ImageCallback
) {
}

@Composable
actual fun ImageAsyncVectorFile(
    filePath: String, density: Density, imageCallback: ImageCallback
) {
}

@Composable
actual fun ImageAsyncVectorUrl(
    url: String, density: Density, imageCallback: ImageCallback
) {
}

@Composable
fun ImagePlaceHolderDefault() {
    Image(painter = ColorPainter(Color.LightGray), contentDescription = "PlaceHolder")
}