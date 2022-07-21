package dark.composer.trackway.presentation.login

import android.util.Log
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dark.composer.trackway.R
import dark.composer.trackway.data.local.UserData
import dark.composer.trackway.data.utils.SharedPref
import dark.composer.trackway.databinding.FragmentLogInBinding
import dark.composer.trackway.presentation.BaseFragment
import kotlinx.coroutines.launch
import javax.inject.Inject

class LogInFragment : BaseFragment<FragmentLogInBinding>(FragmentLogInBinding::inflate) {
    lateinit var viewModel: LogInViewModel
    private val db by lazy {
        Firebase.database
    }

    lateinit var shared: SharedPref

    override fun onViewCreate() {
        viewModel = ViewModelProvider(this)[LogInViewModel::class.java]
        shared = SharedPref(requireContext())
        collect()
        textListener()

        binding.logIn.setOnClickListener {
            Log.d("EEEEE", "onViewCreate: ${binding.phoneNumber.text}")
            viewModel.logIn(
                binding.phoneNumber.text.toString().trim(),
                binding.password.text.toString().trim()
            )
        }
    }

    private fun textListener() {
        binding.passwordInput.isHelperTextEnabled = false
        binding.password.addTextChangedListener {
            viewModel.validPassword(it.toString())
        }

        binding.phoneNumberInput.isHelperTextEnabled = false
        binding.phoneNumber.addTextChangedListener {
            viewModel.validName(it.toString())
        }
    }

    private fun collect() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.whenStarted {
                viewModel.logInFlow.collect {
                    db.getReference("users").child(it.username).setValue(it)
                        .addOnCompleteListener {t->
                            shared.setUsername(it.username)
                            navController.navigate(R.id.action_logInFragment_to_mainFragment)
                        }

                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch{
            viewLifecycleOwner.lifecycle.whenStarted {
                viewModel.errorFlow.collect {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch{
            viewLifecycleOwner.lifecycle.whenStarted {
                viewModel.passwordFlow.collect {
                    if (it == "Correct") {
                        binding.passwordInput.isHelperTextEnabled = false
                    } else {
                        binding.passwordInput.helperText = it
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch{
            viewLifecycleOwner.lifecycle.whenStarted {
                viewModel.nameFlow.collect {
                    if (it == "Correct") {
                        binding.phoneNumberInput.isHelperTextEnabled = false
                    } else {
                        binding.phoneNumberInput.helperText = it
                    }
                }
            }
        }
    }
}