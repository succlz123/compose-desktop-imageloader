package org.succlz123.lib.imageloader

import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.loadSvgPainter
import androidx.compose.ui.res.loadXmlImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import org.succlz123.lib.imageloader.core.*
import org.succlz123.lib.imageloader.transform.ITransformation
import org.xml.sax.InputSource
import java.io.File
import java.net.URL

@Composable
actual fun ImageRes(resName: String, imageCallback: ImageCallback) {
    ImageComposeLoad(get = { ImageResponse(painterResource(resName), null) }, imageCallback)
}

@Composable
fun ImageAsyncImageFile(
    filePath: String, imageCallback: ImageCallback
) {
    ImageAsyncImageFile(filePath, null, imageCallback)
}

@Composable
actual fun ImageAsyncImageFile(
    filePath: String, transformations: List<ITransformation>?, imageCallback: ImageCallback
) {
    ImageSuspendLoad(key = filePath + transformations?.transformationKey(), get = {
        ImageLoader.instance().newRequest().load(File(filePath)).transformations(transformations)
            .saveStrategy(SaveStrategy.Original).get()
    }, imageCallback)
}

@Composable
fun ImageAsyncImageUrl(url: String, imageCallback: ImageCallback) {
    ImageAsyncImageUrl(url, null, imageCallback)
}

@Composable
actual fun ImageAsyncImageUrl(url: String, transformations: List<ITransformation>?, imageCallback: ImageCallback) {
    ImageSuspendLoad(key = url + transformations?.transformationKey(), get = {
        ImageLoader.instance().newRequest().load(url).transformations(transformations)
            .saveStrategy(SaveStrategy.Original)
            .get()
    }, imageCallback)
}

@Composable
actual fun ImageAsyncSvgFile(
    filePath: String, density: Density, imageCallback: ImageCallback
) {
    ImageSuspendLoad(key = filePath, get = {
        try {
            ImageResponse(File(filePath).inputStream().buffered().use { loadSvgPainter(it, density) }, null)
        } catch (e: Exception) {
            ImageResponse(null, e)
        }
    }, imageCallback)
}

@Composable
actual fun ImageAsyncSvgUrl(
    url: String, density: Density, imageCallback: ImageCallback
) {
    ImageSuspendLoad(key = url, get = {
        try {
            ImageResponse(URL(url).openStream().buffered().use { loadSvgPainter(it, density) }, null)
        } catch (e: Exception) {
            ImageResponse(null, e)
        }
    }, imageCallback)
}

@Composable
actual fun ImageAsyncVectorFile(
    filePath: String, density: Density, imageCallback: ImageCallback
) {
    ImageComposeLoad(get = {
        var exception: Exception? = null
        val imageVector = try {
            File(filePath).inputStream().buffered().use { loadXmlImageVector(InputSource(it), density) }
        } catch (e: Exception) {
            exception = e
            null
        }
        if (imageVector == null) {
            ImageResponse(null, exception)
        } else {
            ImageResponse(rememberVectorPainter(imageVector), null)
        }
    }, imageCallback)
}

@Composable
actual fun ImageAsyncVectorUrl(
    url: String, density: Density, imageCallback: ImageCallback
) {
    ImageComposeLoad(get = {
        var exception: Exception? = null
        val imageVector = try {
            URL(url).openStream().buffered().use { loadXmlImageVector(InputSource(it), density) }
        } catch (e: Exception) {
            exception = e
            null
        }
        if (imageVector == null) {
            ImageResponse(null, exception)
        } else {
            ImageResponse(rememberVectorPainter(imageVector), null)
        }
    }, imageCallback)
}

@Composable
fun ImagePlaceHolderDefault() {
    Image(painter = ColorPainter(Color.LightGray), contentDescription = "PlaceHolder")
}

@Composable
private fun ImageSuspendLoad(key: String, get: suspend () -> ImageResponse, imageCallback: ImageCallback) {
    var imageResponse by remember { mutableStateOf(ImageInLoading) }
    LaunchedEffect(key) {
        imageResponse = ImageInLoading
        imageResponse = get()
    }
    if (imageResponse.exception != null) {
        imageCallback.errorView?.invoke()
    } else {
        val painter = imageResponse.imagePainter
        if (painter != null) {
            imageCallback.imageView.invoke(painter)
        } else {
            if (imageResponse.isLoading) {
                imageCallback.placeHolderView?.invoke()
            } else {
                imageCallback.errorView?.invoke()
            }
        }
    }
}

@Composable
private fun ImageComposeLoad(get: @Composable () -> ImageResponse, imageCallback: ImageCallback) {
    val imageResponse = get.invoke()
    if (imageResponse.exception != null) {
        imageCallback.errorView?.invoke()
    } else if (imageResponse.imagePainter == null) {
        imageCallback.errorView?.invoke()
    } else {
        imageCallback.imageView.invoke(imageResponse.imagePainter)
    }
}
