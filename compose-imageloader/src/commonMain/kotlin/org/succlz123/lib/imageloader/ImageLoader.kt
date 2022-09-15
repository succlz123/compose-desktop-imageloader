package org.succlz123.lib.imageloader

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Density
import org.succlz123.lib.imageloader.core.ImageCallback
import org.succlz123.lib.imageloader.transform.ITransformation

@Composable
expect fun ImageRes(resName: String, imageCallback: ImageCallback)

@Composable
expect fun ImageAsyncImageFile(filePath: String, transformations: List<ITransformation>?, imageCallback: ImageCallback)

@Composable
expect fun ImageAsyncImageUrl(url: String, transformations: List<ITransformation>?, imageCallback: ImageCallback)

@Composable
expect fun ImageAsyncSvgFile(filePath: String, density: Density, imageCallback: ImageCallback)

@Composable
expect fun ImageAsyncSvgUrl(url: String, density: Density, imageCallback: ImageCallback)

@Composable
expect fun ImageAsyncVectorFile(filePath: String, density: Density, imageCallback: ImageCallback)

@Composable
expect fun ImageAsyncVectorUrl(url: String, density: Density, imageCallback: ImageCallback)