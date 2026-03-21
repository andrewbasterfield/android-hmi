package com.example.hmi.core.ui.utils

import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver

// Semantic key for testing shape application across components
val ShapeKey = SemanticsPropertyKey<String>("Shape")
var SemanticsPropertyReceiver.componentShape by ShapeKey
