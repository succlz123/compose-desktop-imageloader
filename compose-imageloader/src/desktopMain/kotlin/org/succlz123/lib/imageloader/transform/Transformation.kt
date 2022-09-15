package org.succlz123.lib.imageloader.transform

import androidx.compose.ui.graphics.Color

object TransformationTag {
    const val CenterCropTransformation = "CenterCropTransformation"
    const val CircleCropTransformation = "CircleCropTransformation"
    const val ResizeTransformation = "ResizeTransformation"
}

class Transformation {
    private val iTransformations = mutableListOf<ITransformation>()

    fun toList(): List<ITransformation> {
        return iTransformations
    }

    fun centerCrop(width: Int, height: Int): Transformation {
        if (iTransformations.any { it.tag() == TransformationTag.CenterCropTransformation }) {
            iTransformations.removeIf {
                it.tag() == TransformationTag.CenterCropTransformation
            }
        }
        iTransformations.add(CenterCropTransformation(width, height))
        return this
    }

    fun circleCrop(
        strokeWidth: Float? = null, strokeColor: Color? = null, backgroundColor: Color? = null
    ): Transformation {
        if (iTransformations.any { it.tag() == TransformationTag.CircleCropTransformation }) {
            iTransformations.removeIf {
                it.tag() == TransformationTag.CircleCropTransformation
            }
        }
        iTransformations.add(CircleCropTransformation(strokeWidth, strokeColor, backgroundColor))
        return this
    }

    fun resize(width: Int, height: Int): Transformation {
        if (iTransformations.any { it.tag() == TransformationTag.ResizeTransformation }) {
            iTransformations.removeIf {
                it.tag() == TransformationTag.ResizeTransformation
            }
        }
        iTransformations.add(ResizeTransformation(width, height))
        return this
    }

    fun none(): Transformation {
        return this
    }
}
