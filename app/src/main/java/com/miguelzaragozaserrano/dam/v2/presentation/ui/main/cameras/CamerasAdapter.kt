package com.miguelzaragozaserrano.dam.v2.presentation.ui.main.cameras

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.miguelzaragozaserrano.dam.v2.R
import com.miguelzaragozaserrano.dam.v2.databinding.ListViewItemBinding
import com.miguelzaragozaserrano.dam.v2.domain.models.Camera
import com.miguelzaragozaserrano.dam.v2.presentation.utils.Constants
import com.miguelzaragozaserrano.dam.v2.presentation.utils.Constants.ORDER.*
import com.miguelzaragozaserrano.dam.v2.presentation.utils.Constants.TYPE.ALL
import com.miguelzaragozaserrano.dam.v2.presentation.utils.Constants.TYPE.FAVORITE
import com.miguelzaragozaserrano.dam.v2.presentation.utils.bindBackgroundItem
import com.miguelzaragozaserrano.dam.v2.presentation.utils.bindFavButton
import com.miguelzaragozaserrano.dam.v2.presentation.utils.bindListViewItem
import java.util.*
import kotlin.properties.Delegates

class CamerasAdapter(
    private val context: Context,
    private val onItemClicked: OnClickItemListView,
    private val onFavButtonClicked: OnClickItemListView
) : RecyclerView.Adapter<CamerasViewHolder>() {

    var order = NORMAL
    var type = ALL
    var lastCameraSelected: Camera? = null
    var lastBindingItem: ListViewItemBinding? = null
    var normalList: List<Camera> = emptyList()
    var currentList: List<Camera> by Delegates.observable(emptyList()) { _, _, _ -> notifyDataSetChanged() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CamerasViewHolder {
        return CamerasViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CamerasViewHolder, position: Int) {
        val camera = currentList[position]
        holder.itemView.apply {
            setOnClickListener {
                if (camera != lastCameraSelected) {
                    lastBindingItem?.bindBackgroundItem(
                        camera = lastCameraSelected,
                        border = getDrawable(
                            context,
                            R.drawable.border
                        ),
                        borderSelected = getDrawable(
                            context,
                            R.drawable.border_selected
                        )
                    )
                    lastBindingItem =
                        holder.bindSelectedCamera(
                            context = context,
                            camera = camera
                        )
                    onItemClicked.onClick(camera, lastBindingItem, camera.favorite)
                    lastCameraSelected = camera
                    notifyDataSetChanged()
                }
            }
        }
        holder.bindItem(camera, context, onFavButtonClicked)
    }

    override fun getItemCount(): Int = currentList.size

    fun setListByOrder(order: Constants.ORDER) {
        this.order = order
        setList()
    }

    fun setListByName(query: String?) {
        query?.let {
            if (query != "") {
                currentList = currentList.filter { camera ->
                    camera.name.toLowerCase(Locale.getDefault())
                        .contains(
                            query.toLowerCase(Locale.getDefault())
                        )
                }
            } else {
                currentList = normalList
                setListByOrder(order)
            }
        }
    }

    fun setListByType(type: Constants.TYPE) {
        this.type = type
        setList()
    }

    private fun setList() {
        currentList = when {
            type == ALL && order == ASCENDING -> {
                normalList.sortedBy { camera ->
                    camera.name
                }
            }
            type == ALL && order == DESCENDING -> {
                normalList.sortedByDescending { camera ->
                    camera.name
                }
            }
            type == FAVORITE && order == NORMAL -> {
                normalList.filter { camera ->
                    camera.favorite
                }
            }
            type == FAVORITE && order == ASCENDING -> {
                normalList.sortedBy { camera ->
                    camera.name
                }.filter { camera ->
                    camera.favorite
                }
            }
            type == FAVORITE && order == DESCENDING -> {
                normalList.sortedByDescending { camera ->
                    camera.name
                }.filter { camera ->
                    camera.favorite
                }
            }
            else -> normalList
        }
    }

}

class CamerasViewHolder private constructor(private val binding: ListViewItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindItem(camera: Camera, context: Context, onFavButtonClicked: OnClickItemListView) =
        with(binding) {
            bindListViewItem(
                name = camera.name,
                selected = camera.selected,
                favorite = camera.favorite,
                border = getDrawable(context, R.drawable.border),
                borderSelected = getDrawable(context, R.drawable.border_selected),
                favIcon = getDrawable(context, R.drawable.ic_favorite),
                favIconSelected = getDrawable(context, R.drawable.ic_favorite_selected)
            )
            favButton.apply {
                setOnClickListener {
                    bindFavButton(
                        camera = camera, favIcon = getDrawable(context, R.drawable.ic_favorite),
                        favIconSelected = getDrawable(context, R.drawable.ic_favorite_selected)
                    )
                    onFavButtonClicked.onClick(camera, null, camera.favorite)
                }
            }
        }

    fun bindSelectedCamera(
        context: Context,
        camera: Camera?
    ): ListViewItemBinding? {
        with(binding) {
            return bindBackgroundItem(
                camera = camera,
                border = getDrawable(
                    context,
                    R.drawable.border
                ),
                borderSelected = getDrawable(
                    context,
                    R.drawable.border_selected
                )
            )
        }
    }

    companion object {
        fun from(parent: ViewGroup): CamerasViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding =
                ListViewItemBinding.inflate(layoutInflater, parent, false)
            return CamerasViewHolder(binding)
        }
    }

}

class OnClickItemListView(val clickListener: (camera: Camera, lastBindingItem: ListViewItemBinding?, isFavorite: Boolean) -> Unit) {
    fun onClick(camera: Camera, lastBindingItem: ListViewItemBinding?, isFavorite: Boolean) =
        clickListener(camera, lastBindingItem, isFavorite)
}