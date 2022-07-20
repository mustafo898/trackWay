package dark.composer.trackway.presentation.settings

import dark.composer.trackway.R
import dark.composer.trackway.databinding.FragmentSettingsBinding
import dark.composer.trackway.presentation.BaseFragment

class SettingsFragment : BaseFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {
    override fun onViewCreate() {

        binding.theme.setOnClickListener {
            navController.navigate(R.id.action_settingsFragment_to_themeFragment)
        }

    }

}