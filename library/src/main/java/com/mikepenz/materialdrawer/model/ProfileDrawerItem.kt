package com.mikepenz.materialdrawer.model

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.materialdrawer.R
import com.mikepenz.materialdrawer.holder.ImageHolder
import com.mikepenz.materialdrawer.holder.StringHolder
import com.mikepenz.materialdrawer.model.interfaces.IProfile
import com.mikepenz.materialdrawer.model.interfaces.Tagable
import com.mikepenz.materialdrawer.util.DrawerImageLoader
import com.mikepenz.materialdrawer.util.DrawerUtils.setDrawerVerticalPadding
import com.mikepenz.materialdrawer.util.themeDrawerItem

/**
 * Describes a [IProfile] being used with the [com.mikepenz.materialdrawer.widget.AccountHeaderView]
 */
open class ProfileDrawerItem : AbstractDrawerItem<ProfileDrawerItem, ProfileDrawerItem.ViewHolder>(), IProfile, Tagable {
    override var icon: ImageHolder? = null
    override var name: StringHolder? = null
    override var description: StringHolder? = null
    var isNameShown = false

    override val type: Int
        get() = R.id.material_drawer_item_profile

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.material_drawer_item_profile

    /**
     * Whether to show the profile name in the account switcher.
     *
     * @param nameShown show name in switcher
     * @return the [ProfileDrawerItem]
     */
    @Deprecated("Please consider to replace with the actual property setter")
    fun withNameShown(nameShown: Boolean): ProfileDrawerItem {
        this.isNameShown = nameShown
        return this
    }

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)

        val ctx = holder.itemView.context

        //set the identifier from the drawerItem here. It can be used to run tests
        holder.itemView.id = hashCode()

        //set the item enabled if it is
        holder.itemView.isEnabled = isEnabled
        holder.name.isEnabled = isEnabled
        holder.email.isEnabled = isEnabled
        holder.profileIcon.isEnabled = isEnabled

        //set the item selected if it is
        holder.itemView.isSelected = isSelected
        holder.name.isSelected = isSelected
        holder.email.isSelected = isSelected
        holder.profileIcon.isSelected = isSelected

        //get the correct color for the background
        val selectedColor = getSelectedColor(ctx)
        //get the correct color for the text
        val color = getColor(ctx)
        val shapeAppearanceModel = getShapeAppearanceModel(ctx)

        //set the background for the item
        themeDrawerItem(ctx, holder.view, selectedColor, isSelectedBackgroundAnimated, shapeAppearanceModel)

        if (isNameShown) {
            holder.name.visibility = View.VISIBLE
            StringHolder.applyTo(this.name, holder.name)
        } else {
            holder.name.visibility = View.GONE
        }
        //the MaterialDrawer follows the Google Apps. those only show the e-mail
        //within the profile switcher. The problem this causes some confusion for
        //some developers. And if you only set the name, the item would be empty
        //so here's a small fallback which will prevent this issue of empty items ;)
        if (!isNameShown && this.description == null && this.name != null) {
            StringHolder.applyTo(this.name, holder.email)
        } else {
            StringHolder.applyTo(this.description, holder.email)
        }

        if (typeface != null) {
            holder.name.typeface = typeface
            holder.email.typeface = typeface
        }

        if (isNameShown) {
            holder.name.setTextColor(color)
        }
        holder.email.setTextColor(color)

        //cancel previous started image loading processes
        DrawerImageLoader.instance.cancelImage(holder.profileIcon)
        //set the icon
        ImageHolder.applyToOrSetInvisible(icon, holder.profileIcon, DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name)

        //for android API 17 --> Padding not applied via xml
        setDrawerVerticalPadding(holder.view)

        //call the onPostBindView method to trigger post bind view actions (like the listener to modify the item if required)
        onPostBindView(this, holder.itemView)
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    open class ViewHolder internal constructor(internal val view: View) : RecyclerView.ViewHolder(view) {
        internal val profileIcon: ImageView = view.findViewById(R.id.material_drawer_profileIcon)
        internal val name: TextView = view.findViewById(R.id.material_drawer_name)
        internal val email: TextView = view.findViewById(R.id.material_drawer_email)
    }
}