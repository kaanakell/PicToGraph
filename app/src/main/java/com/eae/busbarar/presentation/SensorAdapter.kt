package com.eae.busbarar.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eae.busbarar.databinding.ListItemSensorBinding


class SensorAdapter(val listener:ISensor) : RecyclerView.Adapter<SensorAdapter.ViewHolder>() {

    var list : List<String> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class ViewHolder(private val binding: ListItemSensorBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.textView.text = list[position]
            binding.root.setOnClickListener {
                listener.onItemClick(list[position])
            }
        }
    }



    /*override fun getItemViewType(
    groupPosition: Int,
    isExpanded: Boolean,
    convertView: View?,
    parent: ViewGroup
    ): View? {

    var convertView = convertView
    val groupHolder: ViewHolderGroup
    if(convertView == null) {
     convertView = LayoutInflater.from(mContext).inflate(
         R.layout.list_item_sensor, parent, false
     )
     groupHolder = ViewHolderGroup()
     groupHolder.textView = convertView.findViewById<View>(R.id.text_view) as TextView
     convertView.tag = groupHolder
    } else {
        groupHolder = convertView.tag as ViewHolderGroup
    }
    groupHolder.textView!!.text = list[groupPosition]
    return convertView
}*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListItemSensorBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}