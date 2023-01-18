package com.example.delivr

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.core.net.toUri
import com.squareup.picasso.Picasso
import java.math.BigDecimal
import java.math.RoundingMode

data class ImageResource(@DrawableRes val drawableRes: Int? = null, val urlRes: Uri? = null)
fun String.toImageResource() = ImageResource(urlRes = this.toUri())

fun ImageView.setImageResource(imageResource: ImageResource) {
    when {
        imageResource.urlRes != null -> {
            Picasso.get().load(imageResource.urlRes).into(this)
        }
        imageResource.drawableRes != null -> {
            Picasso.get().load(imageResource.drawableRes).into(this)
        }
        else -> {
            throw Exception("Image resource should contain a reference to image")
        }
    }
}

fun ViewGroup.inflater(): LayoutInflater = LayoutInflater.from(context)

fun ViewGroup.inflate(@LayoutRes layout: Int): View {
    return inflater().inflate(layout, this, false)
}

fun Int.asWeightString(): String {
    if (this < 1000)
        return "${this}г"
    return "${this}кг"
}

fun BigDecimal.asPriceString(): String {
    val v = this.setScale(2, RoundingMode.CEILING)
    var vs = v.toString()
    if (vs.endsWith(".00"))
        vs = vs.substring(0..vs.length-4)
    return "$vs ₽"
}
