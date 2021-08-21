package com.task.newapp.utils.dotindicator

import android.view.View

fun View.setPaddingHorizontal(padding: Int) {
  setPadding(padding, paddingTop, padding, paddingBottom)
}

fun View.setPaddingVertical(padding: Int) {
  setPadding(paddingLeft, padding, paddingRight, padding)
}