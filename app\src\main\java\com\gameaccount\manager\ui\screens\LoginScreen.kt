package com.gameaccount.manager.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gameaccount.manager.R
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    onEnterClick: () -> Unit
) {
    var isEntering by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // 动画
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 背景图片
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data("file:///android_asset/tp/bg1.jpg")
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(if (isEntering) 10.dp else 0.dp),
            error = painterResource(id = R.drawable.ic_launcher_background),
            placeholder = painterResource(id = R.drawable.ic_launcher_background)
        )

        // 渐变遮罩
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.3f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.5f)
                        )
                    )
                )
        )

        // 内容区域
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .blur(if (isEntering) 10.dp else 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Logo
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data("file:///android_asset/tp/logo6.png")
                    .build(),
                contentDescription = "Logo",
                modifier = Modifier
                    .height(80.dp)
                    .padding(bottom = 20.dp),
                error = painterResource(id = R.drawable.ic_launcher_foreground)
            )

            // Slogan
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data("file:///android_asset/tp/slogan_1345230.png")
                    .build(),
                contentDescription = "Slogan",
                modifier = Modifier
                    .width(300.dp)
                    .padding(bottom = 60.dp),
                error = painterResource(id = R.drawable.ic_launcher_foreground)
            )

            Spacer(modifier = Modifier.weight(1f))

            // 点击进入按钮
            if (!isEntering) {
                Box(
                    modifier = Modifier
                        .size(172.dp)
                        .clickable {
                            isEntering = true
                            // 延迟后跳转
                            // 实际项目中使用 LaunchedEffect
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // 按钮背景
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = Color.White.copy(alpha = 0.1f),
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                    )

                    Text(
                        text = "点击进入",
                        color = Color.White,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // 处理进入动画后的跳转
    LaunchedEffect(isEntering) {
        if (isEntering) {
            delay(500)
            onEnterClick()
        }
    }
}
