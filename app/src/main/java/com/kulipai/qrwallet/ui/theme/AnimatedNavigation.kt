package com.kulipai.qrwallet.ui.theme

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.spec.DestinationStyle

/**
 * Material Design 3 Navigation Animations
 *
 * This implementation follows Material Design 3 motion principles:
 * - Uses the standard easing curve (cubic-bezier(0.2, 0.0, 0, 1.0))
 * - Implements Shared Axis transitions for spatial relationships
 * - Includes Fade Through pattern for unrelated content transitions
 */

// M3 standard easing curve
private val StandardEasing = CubicBezierEasing(0.2f, 0.0f, 0f, 1.0f)
private val EmphasizedEasing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)

// M3 standard durations
private const val DURATION_SHORT = 200
private const val DURATION_MEDIUM = 300
private const val DURATION_LONG = 400

/**
 * Shared Axis X - Forward navigation pattern
 * Used for navigating forward through app hierarchy
 */
object SharedAxisXForward : DestinationStyle.Animated() {
    override val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
        {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(DURATION_MEDIUM, easing = StandardEasing)
            ) + fadeIn(
                animationSpec = tween(DURATION_SHORT, delayMillis = 50, easing = StandardEasing)
            )
        }

    override val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
        {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(DURATION_MEDIUM, easing = StandardEasing)
            ) + fadeOut(
                animationSpec = tween(DURATION_SHORT, easing = StandardEasing)
            )
        }

    override val popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
        {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(DURATION_MEDIUM, easing = StandardEasing)
            ) + fadeIn(
                animationSpec = tween(DURATION_SHORT, delayMillis = 50, easing = StandardEasing)
            )
        }

    override val popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
        {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(DURATION_MEDIUM, easing = StandardEasing)
            ) + fadeOut(
                animationSpec = tween(DURATION_SHORT, easing = StandardEasing)
            )
        }
}

/**
 * Shared Axis Y - Vertical navigation pattern
 * Used for bottom sheet, modal, or vertical navigation
 */
object SharedAxisYForward : DestinationStyle.Animated() {
    override val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
        {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Up,
                animationSpec = tween(DURATION_MEDIUM, easing = EmphasizedEasing)
            ) + fadeIn(
                animationSpec = tween(DURATION_SHORT, delayMillis = 50, easing = StandardEasing)
            )
        }

    override val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
        {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Up,
                animationSpec = tween(DURATION_MEDIUM, easing = EmphasizedEasing)
            ) + fadeOut(
                animationSpec = tween(DURATION_SHORT, easing = StandardEasing)
            )
        }

    override val popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
        {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Down,
                animationSpec = tween(DURATION_MEDIUM, easing = EmphasizedEasing)
            ) + fadeIn(
                animationSpec = tween(DURATION_SHORT, delayMillis = 50, easing = StandardEasing)
            )
        }

    override val popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
        {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Down,
                animationSpec = tween(DURATION_MEDIUM, easing = EmphasizedEasing)
            ) + fadeOut(
                animationSpec = tween(DURATION_SHORT, easing = StandardEasing)
            )
        }
}

/**
 * Fade Through - For content that doesn't have spatial relationship
 * Used for tabs, bottom navigation, or unrelated content transitions
 */
object FadeThrough : DestinationStyle.Animated() {
    override val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
        {
            fadeIn(
                animationSpec = tween(DURATION_SHORT, delayMillis = 90, easing = StandardEasing)
            ) + scaleIn(
                initialScale = 0.92f,
                animationSpec = tween(DURATION_MEDIUM, easing = StandardEasing)
            )
        }

    override val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
        {
            fadeOut(
                animationSpec = tween(90, easing = StandardEasing)
            )
        }

    override val popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
        {
            fadeIn(
                animationSpec = tween(DURATION_SHORT, delayMillis = 90, easing = StandardEasing)
            ) + scaleIn(
                initialScale = 0.92f,
                animationSpec = tween(DURATION_MEDIUM, easing = StandardEasing)
            )
        }

    override val popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
        {
            fadeOut(
                animationSpec = tween(90, easing = StandardEasing)
            )
        }
}

/**
 * Container Transform - For surfaces that transform into new surfaces
 * Great for card-to-detail transformations or expanding surfaces
 */
object ContainerTransform : DestinationStyle.Animated() {
    override val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
        {
            fadeIn(
                animationSpec = tween(DURATION_SHORT, delayMillis = 105, easing = StandardEasing)
            ) + scaleIn(
                initialScale = 0.80f,
                animationSpec = tween(DURATION_MEDIUM, easing = EmphasizedEasing)
            )
        }

    override val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
        {
            fadeOut(
                animationSpec = tween(105, easing = StandardEasing)
            ) + scaleOut(
                targetScale = 1.10f,
                animationSpec = tween(DURATION_MEDIUM, easing = EmphasizedEasing)
            )
        }

    override val popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
        {
            fadeIn(
                animationSpec = tween(DURATION_SHORT, delayMillis = 105, easing = StandardEasing)
            ) + scaleIn(
                initialScale = 1.10f,
                animationSpec = tween(DURATION_MEDIUM, easing = EmphasizedEasing)
            )
        }

    override val popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
        {
            fadeOut(
                animationSpec = tween(105, easing = StandardEasing)
            ) + scaleOut(
                targetScale = 0.80f,
                animationSpec = tween(DURATION_MEDIUM, easing = EmphasizedEasing)
            )
        }
}

/**
 * Simple Fade - Minimal animation for subtle transitions
 * Good for overlays, dialogs, or when you want minimal motion
 */
object SimpleFade : DestinationStyle.Animated() {
    override val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
        {
            fadeIn(animationSpec = tween(DURATION_SHORT, easing = StandardEasing))
        }

    override val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
        {
            fadeOut(animationSpec = tween(DURATION_SHORT, easing = StandardEasing))
        }

    override val popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
        {
            fadeIn(animationSpec = tween(DURATION_SHORT, easing = StandardEasing))
        }

    override val popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
        {
            fadeOut(animationSpec = tween(DURATION_SHORT, easing = StandardEasing))
        }
}

// 为了向后兼容，重命名你原来的对象为新的M3样式
typealias AnimatedNavigation = SharedAxisXForward