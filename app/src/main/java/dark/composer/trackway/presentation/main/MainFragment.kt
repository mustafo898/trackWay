package dark.composer.trackway.presentation.main

import androidx.core.os.bundleOf
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dark.composer.trackway.R
import dark.composer.trackway.data.local.UserData
import dark.composer.trackway.data.utils.SharedPref
import dark.composer.trackway.databinding.FragmentMainBinding
import dark.composer.trackway.presentation.BaseFragment
import dark.composer.trackway.presentation.dialog.UpdateProfileDialog


class MainFragment : BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {
    lateinit var shared: SharedPref
    val db by lazy {
        Firebase.database
    }
    override fun onViewCreate() {
        shared = SharedPref(requireContext())
        if (shared.getUsername().isNullOrEmpty()) {
//            val dialog = UpdateProfileDialog(requireContext())
//            dialog.setTitle("Your Profile")
//            dialog.setOnAddListener { name, password ->
//                shared.setUsername(name)
//                db.getReference("users").child(name).setValue(UserData(name, password))
//                    .addOnCompleteListener {
//                        dialog.dismiss()
//                    }
//            }
//            dialog.show()
            navController.navigate(R.id.action_mainFragment_to_logInFragment)
        }

        binding.start.setOnClickListener {
            if (binding.name.text.toString().trim().isNotEmpty()) {
                shared.setTravelName(binding.name.text.toString())
                navController.navigate(
                    R.id.action_mainFragment_to_travelFragment,
                    bundleOf("TRAVEL_NAME" to binding.name.text.toString())
                )
            }
        }
    }
}