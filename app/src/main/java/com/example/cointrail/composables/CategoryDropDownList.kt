package com.example.cointrail.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
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
    //onItemSelected: (Category) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<Category?>(null) } // Start with no selection

    Box(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = selectedItem?.name ?: "", // Show selected item name or empty if none
            onValueChange = {},
            readOnly = true,
            placeholder = { Text(stringResource(R.string.category)) }, // Show "Category" when nothing is selected
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Open category dropdown"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.name) },
                    onClick = {
                        selectedItem = item // Update selected item
                        //onItemSelected(item) // Notify parent of selection
                        expanded = false // Close dropdown
                    }
                )
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