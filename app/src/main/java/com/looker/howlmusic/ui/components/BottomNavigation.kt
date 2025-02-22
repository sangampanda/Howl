package com.looker.howlmusic.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.looker.components.ComponentConstants.tweenAnimation
import com.looker.components.compositeOverBackground

@Composable
fun BottomAppBar(
    modifier: Modifier = Modifier,
    navController: NavController,
    items: List<HomeScreens>
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    BottomNavigation(
        modifier = modifier.clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp
    ) {
        items.forEach { screen ->
            BottomNavigationItems(
                icon = screen.icon,
                label = screen.title,
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onSelected = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RowScope.BottomNavigationItems(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    selected: Boolean,
    selectedContentColor: Color = MaterialTheme.colors.primary,
    unselectedContentColor: Color = MaterialTheme.colors.primaryVariant,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onSelected: () -> Unit
) {

    val backgroundColor by animateColorAsState(
        targetValue = if (selected) unselectedContentColor.compositeOverBackground()
        else Color.Transparent,
        animationSpec = tweenAnimation()
    )

    val itemColor by animateColorAsState(
        targetValue = if (selected) selectedContentColor
        else MaterialTheme.colors.onSurface.compositeOverBackground(0.5f),
        animationSpec = tweenAnimation()
    )

    val selectedLabel = if (selected) label
    else ""

    BoxWithConstraints(
        modifier = modifier
            .weight(1f)
            .clip(MaterialTheme.shapes.small)
            .background(backgroundColor)
            .selectable(
                selected = selected,
                onClick = onSelected,
                role = Role.Tab,
                indication = null,
                interactionSource = interactionSource
            )
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        BaselineBottomNavigationItem(
            modifier = Modifier,
            icon = icon,
            label = selectedLabel,
            itemColor = itemColor
        )
    }
}

@Composable
fun BaselineBottomNavigationItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    itemColor: Color
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(imageVector = icon, tint = itemColor, contentDescription = null)
        Text(
            modifier = Modifier.animateContentSize(animationSpec = tweenAnimation()),
            text = label,
            color = itemColor
        )
    }
}