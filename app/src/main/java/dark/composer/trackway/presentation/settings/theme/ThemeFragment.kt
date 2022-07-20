package dark.composer.trackway.presentation.settings.theme

import android.widget.CompoundButton
import android.widget.Toast
import dark.composer.trackway.R
import dark.composer.trackway.data.utils.SharedPref
import dark.composer.trackway.databinding.FragmentThemeBinding
import dark.composer.trackway.presentation.BaseFragment

class ThemeFragment : BaseFragment<FragmentThemeBinding>(FragmentThemeBinding::inflate) {
    private lateinit var shared: SharedPref
    override fun onViewCreate() {
        shared = SharedPref(requireContext())

        if (shared.getTheme() == 0) {
            binding.defaultImg.setImageResource(R.drawable.ic_check)
            binding.terrainImg.setImageResource(R.drawable.ic_unchecked)
            binding.satelliteImg.setImageResource(R.drawable.ic_unchecked)
        } else if (shared.getTheme() == 1) {
            binding.defaultImg.setImageResource(R.drawable.ic_unchecked)
            binding.terrainImg.setImageResource(R.drawable.ic_check)
            binding.satelliteImg.setImageResource(R.drawable.ic_unchecked)
        } else if (shared.getTheme() == 2) {
            binding.defaultImg.setImageResource(R.drawable.ic_unchecked)
            binding.terrainImg.setImageResource(R.drawable.ic_unchecked)
            binding.satelliteImg.setImageResource(R.drawable.ic_check)
        }
        binding.modeSwitch.isChecked = shared.getMode()

        binding.modeSwitch.setOnCheckedChangeListener { buttonView:CompoundButton, isChecked ->
            if (isChecked){
                if (buttonView.isChecked){
                    shared.setMode(true)
                }
//                else{
//                    shared.setMode(false)
//                }
            }
        }

        binding.defaultT.setOnClickListener {
            if (shared.getTheme() != 0) {
                shared.setTheme(0)
                binding.defaultImg.setImageResource(R.drawable.ic_check)
                binding.terrainImg.setImageResource(R.drawable.ic_unchecked)
                binding.satelliteImg.setImageResource(R.drawable.ic_unchecked)
                Toast.makeText(requireContext(), "default applied", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "this theme is already applied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.terrain.setOnClickListener {
            if (shared.getTheme() != 1) {
                shared.setTheme(1)
                binding.defaultImg.setImageResource(R.drawable.ic_unchecked)
                binding.terrainImg.setImageResource(R.drawable.ic_check)
                binding.satelliteImg.setImageResource(R.drawable.ic_unchecked)
                Toast.makeText(requireContext(), "terrain applied", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "this theme is already applied",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        binding.satellite.setOnClickListener {
            if (shared.getTheme() != 2) {
                shared.setTheme(2)
                binding.defaultImg.setImageResource(R.drawable.ic_unchecked)
                binding.terrainImg.setImageResource(R.drawable.ic_unchecked)
                binding.satelliteImg.setImageResource(R.drawable.ic_check)
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