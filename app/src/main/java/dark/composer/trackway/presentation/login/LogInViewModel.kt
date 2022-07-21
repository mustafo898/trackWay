package dark.composer.trackway.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dark.composer.trackway.data.local.UserData
import dark.composer.trackway.data.utils.SharedPref
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class LogInViewModel : ViewModel() {

    private val logInChannel = Channel<UserData>()
    val logInFlow = logInChannel.receiveAsFlow()

    private val errorChannel = Channel<String>()
    val errorFlow = errorChannel.receiveAsFlow()

    private val nameChannel = Channel<String>()
    val nameFlow = nameChannel.receiveAsFlow()

    private val passwordChannel = Channel<String>()
    val passwordFlow = passwordChannel.receiveAsFlow()

    fun logIn(
        name: String,
        password: String,
    ) {
        if (!validName(name) && !validPassword(password)) {
            validName(name)
            validPassword(password)
        } else {
            viewModelScope.launch {
                logInChannel.send(UserData(name,password))
            }
        }
    }

    fun validName(name: String): Boolean {
        if (name.isEmpty()) {
            viewModelScope.launch {
                nameChannel.send("Name must be entered")
            }
            return false
        } else if (name.length < 4) {
            viewModelScope.launch {
                nameChannel.send("Minimum 4 Characters Name")
            }
            return false
        } else {
            viewModelScope.launch {
                nameChannel.send("Correct")
            }
            return true
        }
    }

    fun validPassword(password: String): Boolean {
        if (password.length <= 6) {
            viewModelScope.launch {
                passwordChannel.send("Minimum 6 Character Password")
            }
            return false
        } else if (!password.matches(".*[A-Z].*".toRegex())) {
            viewModelScope.launch {
                passwordChannel.send("Must Contain 1 Upper-case Character")
            }
            return false
        } else if (!password.matches(".*[a-z].*".toRegex())) {
            viewModelScope.launch {
                passwordChannel.send("Must Contain 1 Lower-case Character")
            }
            return false
        }
//        else if (!password.matches(".*[@#\$%^_].*".toRegex())){
//            viewModelScope.launch {
//                passwordChannel.send("Must Contain 1 Special Character (@#\$%^_)")
//            }
//            return false
//        }
        else {
            viewModelScope.launch {
                passwordChannel.send("Correct")
            }
            return true
        }
    }
}