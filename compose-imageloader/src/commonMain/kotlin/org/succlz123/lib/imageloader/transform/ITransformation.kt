package org.succlz123.lib.imageloader.transform

import androidx.compose.ui.graphics.ImageBitmap

interface ITransformation {

    fun tag(): String

    fun transform(inputImage: ImageBitmap): ImageBitmap
}