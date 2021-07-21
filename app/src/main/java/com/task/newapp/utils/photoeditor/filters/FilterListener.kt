package com.task.newapp.utils.photoeditor.filters

import com.task.newapp.utils.photoeditor.edit.PhotoFilter


interface FilterListener {
    fun onFilterSelected(photoFilter: PhotoFilter?)
}