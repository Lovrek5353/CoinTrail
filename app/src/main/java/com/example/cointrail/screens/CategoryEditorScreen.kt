import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.cointrail.R
import com.example.cointrail.ui.theme.CoinTrailTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryEditorScreen(
    viewModel: CategoriesViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect ViewModel events for Snackbar and navigation
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is CategoriesViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                CategoriesViewModel.UiEvent.SubmissionSuccess -> {
                    navController.popBackStack()
                }

                else -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.newCategory),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.backIcon),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(id = R.dimen.padding16)),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            item { Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16))) }
            item {
                Text(
                    text = stringResource(id = R.string.categoryName),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding8))
                )
            }
            item {
                OutlinedTextField(
                    value = viewModel.category.name,
                    onValueChange = viewModel::onNameChanged,
                    label = { Text(text = stringResource(id = R.string.categoryName)) },
                    placeholder = { Text(text = stringResource(id = R.string.categoryName)) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Text
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            item { Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16))) }
            item {
                Text(
                    text = stringResource(id = R.string.categoryDescription),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding8))
                )
            }
            item {
                OutlinedTextField(
                    value = viewModel.category.description,
                    onValueChange = viewModel::onDescriptionChanged,
                    label = { Text(text = stringResource(id = R.string.categoryDescription)) },
                    placeholder = { Text(text = stringResource(id = R.string.categoryDescription)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.padding112))
                )
            }
            item { Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding24))) }
            item {
                Button(
                    onClick = { viewModel.onSubmit() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(id = R.string.add))
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun CategoryEditorScreenPreview() {
//    CoinTrailTheme {
//        CategoryEditorScreen(
//            viewModel = CategoriesViewModel(),
//            navController = rememberNavController()
//        )
//    }
//}
