package dark.composer.trackway.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dark.composer.trackway.data.local.HistoryData
import dark.composer.trackway.databinding.ItemHistoryBinding

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
    private val list = mutableListOf<String>()

    private var clickListener: ((name: String) -> Unit)? = null

    fun setClickListener(f: (name: String) -> Unit) {
        clickListener = f
    }


    inner class HistoryViewHolder(private val binding:ItemHistoryBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(name: String){
            binding.name.text = name
            itemView.setOnClickListener {
                clickListener?.invoke(name)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HistoryViewHolder(
        ItemHistoryBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    fun setName(list:List<String>){
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount() = list.size
}