package com.example.hmi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hmi.widgets.ColorUtils

@Composable
fun HexEntryField(
    initialColor: Color,
    onColorChanged: (Color?) -> Unit,
    modifier: Modifier = Modifier
) {
    var hexText by remember { mutableStateOf(ColorUtils.formatHexColor(initialColor)) }
    val focusManager = LocalFocusManager.current
    
    val isValid = remember(hexText) {
        val regex = Regex("^[0-9A-Fa-f]{6}$")
        regex.matches(hexText)
    }

    // Effect to notify listener of color changes
    LaunchedEffect(hexText, isValid) {
        if (isValid) {
            try {
                val parsedInt = android.graphics.Color.parseColor("#$hexText")
                onColorChanged(Color(parsedInt))
            } catch (e: Exception) {
                onColorChanged(null)
            }
        } else {
            onColorChanged(null)
        }
    }

    val previewColor = remember(hexText, isValid) {
        if (isValid) {
            try {
                Color(android.graphics.Color.parseColor("#$hexText"))
            } catch (e: Exception) {
                Color.Transparent
            }
        } else {
            Color.Transparent
        }
    }

    Column(modifier = modifier.padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = hexText,
                onValueChange = {
                    val filtered = it.filter { char -> char.isDigit() || char.lowercaseChar() in 'a'..'f' }
                    hexText = filtered.take(6).uppercase()
                },
                label = { Text("Hex Color (RRGGBB)") },
                prefix = { Text("#") },
                isError = !isValid,
                modifier = Modifier.weight(1f).testTag("HexInputField"),
                supportingText = {
                    if (!isValid) {
                        Text("Enter a valid 6-digit hex code")
                    }
                },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 18.sp
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Ascii,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                )
            )

            // Preview Box
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        previewColor,
                        RoundedCornerShape(8.dp)
                    )
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline,
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (previewColor != Color.Transparent) {
                    Text(
                        text = "ABC",
                        color = ColorUtils.getIndustrialContrastColor(previewColor),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
