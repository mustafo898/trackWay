package dark.composer.trackway.presentation.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dark.composer.trackway.data.local.TravelData
import dark.composer.trackway.databinding.ItemHistoryBinding

class TravelAdapter : RecyclerView.Adapter<TravelAdapter.TravelViewHolder>() {
    private val list = mutableListOf<TravelData>()

    private var clickListener: ((name: String) -> Unit)? = null

    fun setClickListener(f: (name: String) -> Unit) {
        clickListener = f
    }


    inner class TravelViewHolder(private val binding:ItemHistoryBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(name: TravelData){
            binding.name.text = name.name
            itemView.setOnClickListener {
                clickListener?.invoke(name.name)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TravelViewHolder(
        ItemHistoryBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    fun setName(list:List<TravelData>){
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: TravelViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount() = list.size
}