package az.kodcraft.core.presentation.composable.appBar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import az.kodcraft.core.R
import az.kodcraft.core.utils.noRippleClickable


@Composable
fun TopAppBar(
    showMenuIcon: Boolean = false,
    onMenuClick: () -> Unit = {},
    showBackIcon: Boolean = false,
    iconsColor: Color = Color.White,
    onBackClick: () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showMenuIcon)
            Icon(
                modifier = Modifier.noRippleClickable { onMenuClick() },
                painter = painterResource(id = R.drawable.ic_menu),
                tint = iconsColor,
                contentDescription = null
            )
        else if (showBackIcon)
            Icon(
                modifier = Modifier.padding().noRippleClickable { onBackClick() },
                painter = painterResource(id = R.drawable.ic_back),
                tint = iconsColor,
                contentDescription = null
            )
        content()
    }

}
