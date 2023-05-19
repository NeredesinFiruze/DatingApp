package com.example.datingapp.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.unit.sp
import com.example.datingapp.presentation.on_boarding.OnBoardingViewModel
import com.example.datingapp.ui.theme.GrayNormal
import java.util.*


@Composable
fun DateTextField(viewModel: OnBoardingViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var date by remember { viewModel.birthdate }

        DateTextField(
            value = date,
            onValueChange = {
                date = it
                viewModel.saveBirthdate(it.text)
            },
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DateTextField(
    value: TextFieldValue,
    modifier: Modifier = Modifier,
    onValueChange: (TextFieldValue) -> Unit,
) {
    var isBackspacePressed by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = { textFieldValue ->
            // ex: "01-1M-yyyy" -> "011
            val date = textFieldValue.text.takeDigitString()

            if (date.length < 9) {
                val selection = if (!isBackspacePressed) {
                    when (date.length) {
                        3, 5 -> textFieldValue.selection.start + 1
                        else -> textFieldValue.selection.start
                    }
                } else textFieldValue.selection.start
                onValueChange(
                    textFieldValue.copy(
                        text = TextFieldDateFormatter.format(textFieldValue),
                        selection = TextRange(selection)
                    )
                )
            }
        },
        visualTransformation = DateFormatVisualTransformation(LocalTextStyle.current),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        modifier = modifier
            .onKeyEvent {
                if (it.key == Key.Backspace) {
                    isBackspacePressed = true
                    return@onKeyEvent true
                } else {
                    isBackspacePressed = false
                }
                false
            }
        ,
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.textFieldColors(
            cursorColor = GrayNormal,
            focusedLabelColor = Color.Transparent,
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        textStyle = TextStyle(
            textAlign = Center
        )
    )
}
object TextFieldDateFormatter{
    private const val ddMMyyyy = "DDMMYYYY"

    fun format(
        fieldValue: TextFieldValue,
        minYear: Int = 1960,
        maxYear: Int = 2023 - 18
    ): String{
        val builder = StringBuilder()
        val s = fieldValue.text.replace(
            oldValue =  listOf(" ", ".", ",", "/","-", "D", "M", "Y"),
            newValue = ""
        )

        if (s.length != 8){
            for (i in 0 until 8){
                builder.append(
                    try {
                        s[i]
                    }
                    catch (e: Exception){
                        ddMMyyyy[i]
                    }
                )
            }
        }else builder.append(s)

        repeat(3){
            when(it){
               0->{
                   val day = try {
                       "${builder[0]}${builder[1]}".toInt()
                   }catch (e: Exception){
                       null
                   }
                   if (day != null){
                       val dayMax = day.coerceIn(1,31).toString()
                       builder.replace(0,2,if (dayMax.length == 1)"0$dayMax" else dayMax)
                   }
               }
               1->{
                   val month = try {
                       "${builder[2]}${builder[3]}".toInt()
                   } catch (e: Exception) {
                       null
                   }

                   if (month != null) {
                       val monthMax = month
                           .coerceIn(
                               minimumValue = 1,
                               maximumValue = 12,
                           )
                           .toString()

                       builder.replace(
                           2,
                           4,
                           if (monthMax.length == 1) "0$monthMax" else monthMax
                       )
                   }
               }
               2->{
                   val year = try {
                       "${builder[4]}${builder[5]}${builder[6]}${builder[7]}".toInt()
                   } catch (e: Exception) {
                       null
                   }

                   if (year != null) {
                       val yearMaxMin = year.coerceIn(
                           minimumValue = minYear,
                           maximumValue = maxYear
                       ).toString()

                       builder.replace(4, 6, yearMaxMin.substring(0, 2))
                       builder.replace(6, 8, yearMaxMin.substring(2, 4))
                   }
               }
            }
        }
        return builder.toString()
            .addStringBefore("/", 2)  // dd-MMyyyy
            .addStringBefore("/", 5)   // dd-MM-yyyy
    }
}

fun String.takeDigitString(): String {
    var builder = ""
    forEach { if (it.isDigit()) builder += it }
    return builder
}

fun String.replace(
    oldValue: List<String>,
    newValue: String,
    ignoreCase: Boolean = false,
): String {
    var result = this

    oldValue.forEach { s ->
        if (this.contains(s)) {
            result = result.replace(s, newValue, ignoreCase)
        }
    }

    return result
}

fun String.addStringBefore(s: String, index: Int): String {
    val result = StringBuilder()
    forEachIndexed { i, c ->
        if (i == index) {
            result.append(s)
            result.append(c)
        } else result.append(c)
    }

    return result.toString()
}

class DateFormatVisualTransformation(private val textStyle: TextStyle) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            text = buildAnnotatedString {
                for (word in text) {
                    withStyle(
                        textStyle.copy(
                            color = if (word.isDigit() || word == '/') textStyle.color
                            else Color.Gray,
                            fontSize = 42.sp
                        ).toSpanStyle()
                    ) {
                        append(word)
                    }
                }
            },
            offsetMapping = OffsetMapping.Identity
        )
    }

}