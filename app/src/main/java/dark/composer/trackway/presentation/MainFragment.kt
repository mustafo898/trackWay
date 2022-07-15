package dark.composer.trackway.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dark.composer.trackway.R
import dark.composer.trackway.data.local.UserData
import dark.composer.trackway.data.utils.SharedPref
import dark.composer.trackway.databinding.FragmentMainBinding


class MainFragment : BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {
    lateinit var shared: SharedPref
    val db by lazy {
        Firebase.database
    }
    override fun onViewCreate() {
        shared = SharedPref(requireContext())
        if (shared.getUsername().isNullOrEmpty()) {
            val dialog = UpdateProfileDialog(requireContext())
            dialog.setTitle("Your Profile")
            dialog.setOnAddListener { name, password ->
                shared.setUsername(name)
                db.getReference("users").child(name).setValue(UserData(name, password))
                    .addOnCompleteListener {
                        dialog.dismiss()
                    }
            }
            dialog.show()
        }

        binding.start.setOnClickListener {
            if (binding.name.text.toString().isNotEmpty()) {
                navController.navigate(
                    R.id.action_mainFragment_to_travelFragment,
                    bundleOf("TRAVEL_NAME" to binding.name.text.toString())
                )
            }
        }
    }
}