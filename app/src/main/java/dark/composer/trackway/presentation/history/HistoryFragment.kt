package dark.composer.trackway.presentation.history

import android.util.Log
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.recyclerview.widget.LinearLayoutManager
import dark.composer.trackway.R
import dark.composer.trackway.data.utils.SharedPref
import dark.composer.trackway.databinding.FragmentHistoryBinding
import dark.composer.trackway.presentation.BaseFragment
import kotlinx.coroutines.launch
class HistoryFragment : BaseFragment<FragmentHistoryBinding>(FragmentHistoryBinding::inflate) {
    private lateinit var viewModel: HistoryViewModel
    private val travelAdapter by lazy {
        TravelAdapter()
    }

    override fun onViewCreate() {
        viewModel = ViewModelProvider(this)[HistoryViewModel::class.java]
        val shared = SharedPref(requireContext())

        binding.list.layoutManager = LinearLayoutManager(requireContext())
        binding.list.adapter = travelAdapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.whenStarted {
                viewModel.nameFlow.collect {
                    Log.d("EEEE", "collect: $it")
                    travelAdapter.setName(it)
                }
            }
        }

        travelAdapter.setClickListener {
            navController.navigate(
                R.id.action_historyFragment_to_historyDetailsFragment,
                bundleOf("TRAVEL_NAME" to it)
            )
        }
        if (shared.getUsername()?.isNotEmpty() == true){
            viewModel.readTravels( shared.getUsername().toString())
        }
    }
}