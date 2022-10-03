![banner][file:banner]

[file:banner]: screenshot/banner.png

## Introduction
A simple, crude image loader that supports memory and disk LRU caching. Only supported Compose Desktop, because there
are some more powerful image loader lib for Android platform.

You can see the official doc for more inspiration.
https://github.com/JetBrains/compose-jb/tree/master/tutorials/Image_And_Icons_Manipulations

## Screenshot

![1][file:1]

[file:1]: screenshot/imageloader-1.png

## Usage

```kotlin
ImageAsyncImageUrl(imageUrl,
    imageCallback = ImageCallback {
        Image(modifier = Modifier.size(640.dp), painter = it, contentDescription = "123")
    })
ImageRes("compose-logo.png",
    imageCallback = ImageCallback {
        Image(modifier = Modifier.size(64.dp), painter = it, contentDescription = "res")
    })
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
```

## Download

```
implementation("io.github.succlz123:compose-imageloader-desktop:0.0.2")
```

## Thanks

- https://github.com/K1rakishou/Caching-Image-Loader
- https://github.com/MayakaApps/KotlinizedLruCache
- https://github.com/JakeWharton/DiskLruCache

