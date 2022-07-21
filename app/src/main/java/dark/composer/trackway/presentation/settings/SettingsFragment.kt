package dark.composer.trackway.presentation.settings

import android.graphics.Color
import android.widget.CompoundButton
import android.widget.Toast
import dark.composer.trackway.data.utils.SharedPref
import dark.composer.trackway.databinding.FragmentSettingsBinding
import dark.composer.trackway.presentation.BaseFragment

class SettingsFragment : BaseFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {

    private lateinit var shared: SharedPref

    override fun onViewCreate() {
        shared = SharedPref(requireContext())

        if (shared.getTheme() == 0) {
            binding.modeSwitch.isClickable = true
            binding.defaultT.setTextColor(Color.parseColor("#0B68DC"))//0B68DC
            binding.terrainT.setTextColor(Color.parseColor("#676767"))//676767
            binding.satelliteT.setTextColor(Color.parseColor("#676767"))
        } else if (shared.getTheme() == 1) {
            binding.modeSwitch.isClickable = false
            binding.defaultT.setTextColor(Color.parseColor("#676767"))//0B68DC
            binding.terrainT.setTextColor(Color.parseColor("#0B68DC"))//676767
            binding.satelliteT.setTextColor(Color.parseColor("#676767"))

        } else if (shared.getTheme() == 2) {
            binding.modeSwitch.isClickable = false
            binding.defaultT.setTextColor(Color.parseColor("#676767"))//0B68DC
            binding.terrainT.setTextColor(Color.parseColor("#676767"))//676767
            binding.satelliteT.setTextColor(Color.parseColor("#0B68DC"))
        }

        binding.modeSwitch.isChecked = shared.getMode()

        binding.modeSwitch.setOnCheckedChangeListener { _: CompoundButton, isChecked ->
            if (shared.getTheme() == 0) {
                if (isChecked) {
                    shared.setMode(true)
                    Toast.makeText(requireContext(), "dark mode", Toast.LENGTH_SHORT).show()
                } else {
                    shared.setMode(false)
                    Toast.makeText(requireContext(), "light mode", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.mode.setOnClickListener {
            if (shared.getTheme() != 0) {
                binding.modeSwitch.isClickable = false
                Toast.makeText(requireContext(), "theme should be as default", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (!shared.getMode()){
                    shared.setMode(true)
                    binding.modeSwitch.isChecked = shared.getMode()
                }else{
                    shared.setMode(false)
                    binding.modeSwitch.isChecked = shared.getMode()
                }
            }
        }

        binding.defaultR.setOnClickListener {
            if (shared.getTheme() != 0) {
                binding.modeSwitch.isClickable = true
                shared.setTheme(0)
                binding.defaultT.setTextColor(Color.parseColor("#0B68DC"))//0B68DC
                binding.terrainT.setTextColor(Color.parseColor("#676767"))//676767
                binding.satelliteT.setTextColor(Color.parseColor("#676767"))
                Toast.makeText(requireContext(), "default applied", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "this theme is already applied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.terrainR.setOnClickListener {
            if (shared.getTheme() != 1) {
                shared.setTheme(1)
                binding.modeSwitch.isClickable = false
                binding.defaultT.setTextColor(Color.parseColor("#676767"))//0B68DC
                binding.terrainT.setTextColor(Color.parseColor("#0B68DC"))//676767
                binding.satelliteT.setTextColor(Color.parseColor("#676767"))
                Toast.makeText(requireContext(), "terrain applied", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "this theme is already applied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.satelliteR.setOnClickListener {
            if (shared.getTheme() != 2) {
                shared.setTheme(2)
                binding.modeSwitch.isClickable = false
                binding.defaultT.setTextColor(Color.parseColor("#676767"))//0B68DC
                binding.terrainT.setTextColor(Color.parseColor("#676767"))//676767
                binding.satelliteT.setTextColor(Color.parseColor("#0B68DC"))
                Toast.makeText(requireContext(), "satellite applied", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "this theme is already applied",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

    }

}