package com.example.shareway.viewholders

import android.util.Log
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.shareway.R
import com.example.shareway.databinding.ArticleListItemBinding
import com.example.shareway.listeners.OnArticleClickListener
import com.example.shareway.listeners.ReminderListeners
import com.example.shareway.models.Article
import com.example.shareway.utils.managers.DateTimeManager
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class ArticleListViewHolder(
    private val binding: ArticleListItemBinding,
    private val onArticleClickListener: OnArticleClickListener
) :
    RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {

    companion object {
        private const val TAG = "ArticleListViewHolder"
    }

    init {
        binding.root.setOnClickListener(this)
        binding.setDateButton.setOnClickListener(this)
//        itemView.setOnClickListener(object : DoubleClickListener() {
//            override fun onDoubleClick(v: View) {
//                Log.d(TAG, "onDoubleClick: DOUBLE CLICKED")
//            }
//
//        })
        binding.root.setOnLongClickListener(this)
        binding.popupMenu.setOnClickListener(this)
        binding.reminderIcon.setOnClickListener(this)

    }


    fun bind(articleItem: Article?) {

        Log.d(TAG, "bind: $articleItem ")
        articleItem?.let {
            Log.d(TAG, "bind: Date Added: ${it.dateAdded}")
            binding.apply {

                reminderIcon.visibility = if (it.reminder != null) View.VISIBLE else View.GONE

                if (it.alreadyRead) alreadyReadIcon1.visibility =
                    View.VISIBLE else alreadyReadIcon1.visibility = View.GONE
                titleText.text = articleItem.title

//                Glide.with(itemView)
//                    .load(articleItem.articleImage)
//                    .centerCrop()
//                    .error(Glide.with(itemView).load(articleItem.defaultImage))
//                    .into(articleImage)

                /**
                //                 *
                //                 * Coil library.
                //                 *
                //                 * @crossfade - . Suppose there is an image of size 500 x 500 on disk. Initially, Coil loads 100 x 100 and uses it as a placeholder until full quality is loaded
                //                 *
                //                 * **/
                articleImage.load(articleItem.articleImage) {
                    crossfade(true)
//                    error(articleImage.load(articleItem.defaultImage)) //TODO: Change to local error
                }


                val dateFormat = LocalDateTime.ofInstant(it.dateAdded, ZoneOffset.UTC)
//                date.text = dateFormat.toString()
//                date.text = dateFormat.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT))
                date.text = dateFormat.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))

                Log.d(TAG, "bind: ALREADY READ ${it.alreadyRead}")
                if (it.alreadyRead) binding.alreadyReadIcon.visibility =
                    View.VISIBLE else binding.alreadyReadIcon.visibility = View.GONE

                reminderText.text = it.reminder?.toString() ?: "No reminder for this article"
            }
        }

    }

    override fun onClick(v: View?) {
        Log.d(TAG, "onClick: clicked")
//        if (v == binding.setDateButton) onArticleClickListener.onSetRemainderButtonClick(
//            adapterPosition
////        ) else onArticleClickListener.onArticleClick(adapterPosition)
        when (v) {
            binding.setDateButton -> onSetReminderClicked()
            binding.popupMenu -> showPopupMenu(v)
            binding.reminderIcon -> showReminderDialog(binding.reminderText.text.toString())
            else -> onArticleClickListener.onArticleClick(adapterPosition)
        }

    }

    private fun showReminderDialog(text: String) {
        onArticleClickListener.onReminderIconClick(text, adapterPosition)
    }

    private fun showPopupMenu(v: View) {
        val popupMenu = PopupMenu(v.context, v)
        popupMenu.inflate(R.menu.article_list_item_menu)
        popupMenu.setOnMenuItemClickListener {

            when (it.itemId) {
                R.id.mark_as_read -> {
                    Log.d(TAG, "showPopupMenu: mark as read clicked")
                    Log.d(TAG, "onLongClick: clicked")
                    //check if true or false
                    if (binding.alreadyReadIcon1.isVisible) {
                        Log.d(TAG, "bind: ALREADY READ VISIBILITY = VISIBLE! NOW IT ISN'T")
                        binding.alreadyReadIcon1.visibility = View.GONE
                    } else {
                        Log.d(TAG, "bind: ALREADY READ VISIBILITY = NOT(!!!) VISIBLE! NOW IT IS")
                        binding.alreadyReadIcon1.visibility = View.VISIBLE
                    }
                    //true if the callback consumed the long click, false otherwise.
                    onArticleClickListener.onLongArticleClick(adapterPosition)
                    return@setOnMenuItemClickListener true
                }

                R.id.set_reminder -> {
                    Log.d(TAG, "showPopupMenu: set reminder clicked")
                    onSetReminderClicked()
                    return@setOnMenuItemClickListener true
                }

                R.id.add_note -> {
                    onArticleClickListener.onAddNoteClick(adapterPosition)
                    return@setOnMenuItemClickListener true
                }

                R.id.view_notes -> {
                    onArticleClickListener.onViewNotesClick(adapterPosition)
                    return@setOnMenuItemClickListener true
                }

                R.id.delete -> {
                    onArticleClickListener.onDeleteArticle(adapterPosition)
                    return@setOnMenuItemClickListener true
                }

                else -> return@setOnMenuItemClickListener true

            }
        }
        popupMenu.show()
    }


    /**
     *
     * onLongClick - check visibility and set the opposite.
     * Insert isAlreadyRead status to db
     *
     * TODO: REMOVE, LEGACY CODE BEFORE THE ACTIONM ODE
     * **/
    override fun onLongClick(v: View?): Boolean {
        Log.d(TAG, "onLongClick: clicked")
        //check if true or false
        if (binding.alreadyReadIcon.isVisible) {
            Log.d(TAG, "bind: ALREADY READ VISIBILITY = VISIBLE! NOW IT ISN'T")
            binding.alreadyReadIcon.visibility = View.GONE
        } else {
            Log.d(TAG, "bind: ALREADY READ VISIBILITY = NOT(!!!) VISIBLE! NOW IT IS")
            binding.alreadyReadIcon.visibility = View.VISIBLE
        }
        //true if the callback consumed the long click, false otherwise.
        onArticleClickListener.onLongArticleClick(adapterPosition)
        return true
    }

    fun triggerOnClickListener() {
        onClick(itemView)
    }


    private fun onSetReminderClicked() {
        DateTimeManager.launchDateTimeDialogs(binding.root.context,
            object : ReminderListeners {
                override fun onSuccess(reminder: Instant, hour: Int, minute: Int, day: Int) {
                    onArticleClickListener.onReminderSet(
                        adapterPosition,
                        reminder,
                        hour,
                        minute,
                        day
                    )

                    /**
                     *
                     * TODO: Cancel when reminder end. implement funcion what happen when reminder done(mayble check on reycler launch if there is active reminder will be a better solution)
                     *
                     * **/
                    binding.reminderIcon.visibility = View.VISIBLE
//            val string = "Date: ${headerText} Time: ${timePicker.hour}:${timePicker.minute}"
//            binding.reminderText.text = string
                }

            }
        )
    }


}