package hummfinderapp.alpha

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.calibration_item.view.*


class CalViewPagerAdapter(
    val texts: List<String>
) : RecyclerView.Adapter<CalViewPagerAdapter.ViewPagerViewHolder>()
{

    inner class ViewPagerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.calibration_item, parent, false)
        return ViewPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        val curText = texts[position]
        holder.itemView.calItemTV.text = curText
    }

    override fun getItemCount(): Int {
        return texts.size
    }
}