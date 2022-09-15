import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import org.succlz123.lib.imageloader.ImageAsyncImageUrl
import org.succlz123.lib.imageloader.ImageAsyncSvgUrl
import org.succlz123.lib.imageloader.ImageAsyncVectorUrl
import org.succlz123.lib.imageloader.ImageRes
import org.succlz123.lib.imageloader.core.ImageCallback
import org.succlz123.lib.imageloader.core.ImageLoader
import org.succlz123.lib.imageloader.core.ImageLoader.Companion.USER_DIR
import org.succlz123.lib.imageloader.transform.Transformation
import kotlin.random.Random

val imageList = arrayListOf(
    "https://w.wallhaven.cc/full/wq/wallhaven-wq2787.jpg",
    "https://w.wallhaven.cc/full/9m/wallhaven-9mjoy1.png",
    "https://w.wallhaven.cc/full/6o/wallhaven-6ozkzl.jpg",
    "https://w.wallhaven.cc/full/z8/wallhaven-z8dg9y.png",
    "https://w.wallhaven.cc/full/j3/wallhaven-j3m8y5.png",
    "https://w.wallhaven.cc/full/y8/wallhaven-y8622k.jpg",
    "https://w.wallhaven.cc/full/57/wallhaven-572k81.png",
    "https://w.wallhaven.cc/full/72/wallhaven-72ywpv.jpg",
    "https://w.wallhaven.cc/full/v9/wallhaven-v9wo18.png",
    "https://w.wallhaven.cc/full/8o/wallhaven-8o23gk.jpg",
    "https://w.wallhaven.cc/full/pk/wallhaven-pkq1q9.png"
)

@Composable
fun App() {
    var imageUrl by remember { mutableStateOf(imageList.first()) }
    var lastIndex by remember { mutableStateOf(0) }
    var transformation by remember { mutableStateOf(Transformation().toList()) }

    LaunchedEffect(Unit) {
        // configure the image loader
        ImageLoader.configuration(1024 * 1024 * 100L, 1024 * 1024 * 50L, USER_DIR)
    }

    MaterialTheme(colors = lightColors().copy(primary = Color(0xFFF5730A))) {
        Column(
            modifier = Modifier.fillMaxWidth().background(Color.White).padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                ImageRes(
                    "compose-logo.png",
                    imageCallback = ImageCallback.default(modifier = Modifier.size(64.dp), contentDescription = "res")
                )
                ImageAsyncSvgUrl("https://raw.githubusercontent.com/JetBrains/compose-jb/master/artwork/compose-logo.svg",
                    density = LocalDensity.current,
                    imageCallback = ImageCallback {
                        Image(modifier = Modifier.size(64.dp), painter = it, contentDescription = "svg")
                    })
                ImageAsyncVectorUrl("https://raw.githubusercontent.com/JetBrains/compose-jb/master/artwork/compose-logo.xml",
                    density = LocalDensity.current,
                    imageCallback = ImageCallback {
                        Image(modifier = Modifier.size(64.dp), painter = it, contentDescription = "vector")
                    })
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                ImageAsyncImageUrl(imageUrl,
                    transformations = transformation,
                    imageCallback = ImageCallback(placeHolderView = {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(64.dp), strokeWidth = 6.dp)
                        }
                    }) {
                        Image(modifier = Modifier.size(480.dp), painter = it, contentDescription = "123")
                    })
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(modifier = Modifier, onClick = {
                var random = Random.nextInt(0, imageList.size)
                while (lastIndex == random) {
                    random = Random.nextInt(0, imageList.size)
                }
                lastIndex = random
                imageUrl = imageList[random]
            }) {
                Text(text = "Change")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(modifier = Modifier, onClick = {
                    transformation = Transformation().circleCrop(
                        strokeWidth = 60f, strokeColor = Color.Red, backgroundColor = Color.Gray
                    ).toList()
                }) {
                    Text(text = "Circle")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(modifier = Modifier, onClick = {
                    transformation = Transformation().none().toList()
                }) {
                    Text(text = "Normal")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(modifier = Modifier, onClick = {
                    transformation = Transformation().resize(120, 120).toList()
                }) {
                    Text(text = "Resize 120x120")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(modifier = Modifier, onClick = {
                    transformation = Transformation().centerCrop(240, 240).toList()
                }) {
                    Text(text = "CenterCrop 240x240")
                }
            }
        }
    }
}
