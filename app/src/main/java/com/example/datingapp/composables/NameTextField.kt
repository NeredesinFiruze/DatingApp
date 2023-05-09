package com.example.datingapp.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.datingapp.presentation.on_boarding.OnBoardingViewModel
import com.example.datingapp.ui.theme.GrayNormal

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NameTextField(
    viewModel: OnBoardingViewModel = hiltViewModel(),
    onSearch: ()-> Unit
) {
    var query by remember { mutableStateOf("") }
    TextField(
        value = query,
        onValueChange = { newValue ->
            if (newValue.length < 40 && newValue.all { it.isLetter() }){
                query = newValue
                if (newValue.length > 2) viewModel.saveName(newValue)
                else viewModel.saveName("")
            }
        },
        modifier = Modifier
            .fillMaxWidth(),
        singleLine = true,
        placeholder = {
            Text(
                text = "Name",
                color = Color(0xFFF17474),
                fontSize = 22.sp
            )
        },
        colors = ExposedDropdownMenuDefaults.textFieldColors(
            textColor = GrayNormal,
            backgroundColor = Color.Transparent,
            disabledTextColor = Color.LightGray,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ), keyboardOptions = KeyboardOptions(
            autoCorrect = true,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onSearch() }
        ),
    )
}