package com.example.hmi.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

object IndustrialShape {
    val Standard = RoundedCornerShape(8.dp)
    val Small = RoundedCornerShape(4.dp)
}

object WidgetShapes {
    fun getShapeForSize(colSpan: Int, rowSpan: Int): RoundedCornerShape {
        return if (colSpan == 1 && rowSpan == 1) {
            IndustrialShape.Small
        } else {
            IndustrialShape.Standard
        }
    }
}
