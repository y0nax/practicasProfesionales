package com.example.p1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p1.ui.theme.P1Theme
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            P1Theme {
                CalculatorScreen()
            }
        }
    }
}

@Composable
fun CalculatorScreen() {
    var expression by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF212121))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = expression,
            fontSize = 32.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            color = Color.LightGray
        )

        Text(
            text = result,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            color = Color.White
        )

        val buttons = listOf(
            listOf("7", "8", "9", "/"),
            listOf("4", "5", "6", "*"),
            listOf("1", "2", "3", "-"),
            listOf("0", ".", "C", "+"),
            listOf("=", "←")
        )

        for (row in buttons) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (button in row) {
                    CalculatorButton(text = button, onClick = {
                        when (button) {
                            "=" -> {
                                try {
                                    val evaluated = ExpressionBuilder(expression).build().evaluate()
                                    result = evaluated.toString()
                                } catch (e: Exception) {
                                    result = "Error"
                                }
                            }
                            "C" -> {
                                expression = ""
                                result = ""
                            }
                            "←" ->{
                               if (expression.isNotEmpty()){
                                   expression = expression.dropLast(1)
                               }
                            }
                            else -> {
                                expression += button
                            }
                        }
                    })
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .size(72.dp)
            .background(Color(0xFFE0E0E0))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontSize = 24.sp)
    }
}

@Composable
fun getButtonColor(text: String): Color {
    return when (text) {
        "C", "←", "=" -> Color(0xFFDD2C00) // Color rojo para "C", "←", "="
        "/" , "*", "-", "+" -> Color(0xFF4CAF50) // Color verde para los operadores
        else -> Color(0xFF2196F3) // Color azul para los números
    }
}

@Composable
fun getTextColor(text: String): Color {
    return if (text == "C" || text == "←" || text == "=") {
        Color.White // Color blanco para los botones de control
    } else {
        Color.Black // Color negro para los botones numéricos
    }
}