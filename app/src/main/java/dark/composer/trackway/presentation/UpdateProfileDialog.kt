package dark.composer.trackway.presentation

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import dark.composer.trackway.R
import dark.composer.trackway.databinding.DialogAddProfileBinding

class UpdateProfileDialog(content: Context) : AlertDialog(content) {
    private var binding = DialogAddProfileBinding.inflate(layoutInflater)

    private var updateListener: ((name: String,password: String) -> Unit)? =
        null

    fun setOnAddListener(f: (name: String,password: String) -> Unit) {
        updateListener = f
    }

    private var title: String = ""
    fun setTitle(title: String) {
        this.title = title
        binding.title.text = title
    }

    init {
        window?.setBackgroundDrawable(ColorDrawable(0))

        window?.setWindowAnimations(R.style.AnimationForDialog)

        var n = false
        var s = false

        binding.name.addTextChangedListener {
            if (validName(it.toString())) {
                n = true
                binding.nameInput.isHelperTextEnabled = false
                binding.acceptFB.isClickable = true
            } else {
                binding.acceptFB.isClickable = false
            }
        }

        binding.password.addTextChangedListener {
            if (validPassword(it.toString())) {
                s = true
                binding.passwordInput.isHelperTextEnabled = false
                binding.acceptFB.isClickable = true
            } else {
                s= false
                binding.acceptFB.isClickable = false
            }
        }

        if (s && n){
            binding.acceptFB.setOnClickListener {
                updateListener?.invoke(binding.name.text.toString(),binding.password.text.toString())
            }
        }

        binding.cancelFB.setOnClickListener {
            dismiss()
        }
        setCancelable(false)
        setView(binding.root)
    }

    private fun validName(name: String): Boolean {
        if (name.isEmpty()) {
            binding.nameInput.helperText = "Name must be entered"
            return false
        } else if (name.length < 4) {
            binding.nameInput.helperText = "Minimum 4 Characters Name"
            return false
        } else {
            return true
        }
    }

    private fun validPassword(password: String): Boolean {
        if (password.length <= 6) {
            binding.passwordInput.helperText = "Minimum 6 Character Password"
            return false
        } else if (!password.matches(".*[A-Z].*".toRegex())) {
            binding.passwordInput.helperText = "Must Contain 1 Upper-case Character"
            return false
        } else if (!password.matches(".*[a-z].*".toRegex())) {
            binding.passwordInput.helperText = "Must Contain 1 Lower-case Character"
            return false
        } else if (!password.matches(".*[@#\$%^_].*".toRegex())) {
            binding.passwordInput.helperText = "Must Contain 1 Special Character (@#\\\$%^_)"
            return false
        } else {
            return true
        }
    }
}