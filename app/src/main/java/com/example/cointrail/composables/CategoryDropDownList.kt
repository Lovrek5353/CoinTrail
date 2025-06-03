package com.example.cointrail.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cointrail.R
import com.example.cointrail.data.Category
import com.example.cointrail.data.dummyCategories
import com.example.cointrail.ui.theme.CoinTrailTheme

@Composable
fun CategoryDropDownList(
    items: List<Category>,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<Category?>(null) }


    Box(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = selectedItem?.name ?: "",
            onValueChange = {},
            readOnly = true,
            placeholder = { Text(stringResource(R.string.category)) },
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = stringResource(R.string.categoryDropdown)
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        if (expanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .blur(radius = dimensionResource(R.dimen.padding16))
                    .clickable { expanded = false }
            ) {
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .offset(y = dimensionResource(R.dimen.offset60))
                ) {
                    items.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item.name) },
                            onClick = {
                                selectedItem = item
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}




@Preview
@Composable
fun CategoryDropDownListPreview(){
    CoinTrailTheme {
        CategoryDropDownList(dummyCategories)
    }
}