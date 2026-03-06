import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.sample.App

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Compose Super Charts Desktop") {
        App()
    }
}
